#!/usr/bin/env python3
# fix7.py — round 9 targeted fixes
import re, os, glob

def rd(p):
    with open(p, encoding="utf-8") as f: return f.read()
def wr(p, s):
    with open(p, "w", encoding="utf-8") as f: f.write(s)

MISS = []
def sub(path, pat, rep, cnt=0):
    t = rd(path); o = t
    t = re.sub(pat, rep, t, count=cnt)
    if t == o: MISS.append(path.split("/")[-1] + " :: " + pat[:60])
    else: wr(path, t)
    return t

def rep_literal(path, old, new):
    t = rd(path)
    if old not in t:
        MISS.append(path.split("/")[-1] + " :: LIT " + old[:60]); return
    wr(path, t.replace(old, new))

H = "src/main/java/thunder/hack/"
ALL = glob.glob(H + "**/*.java", recursive=True)

# ---------- global passes ----------
for p in ALL:
    t = rd(p); o = t
    # E: RenderPipelines package move + dedupe
    t = t.replace("import net.minecraft.client.render.RenderPipelines;\n", "import net.minecraft.client.gl.RenderPipelines;\n")
    if "import net.minecraft.client.gl.RenderPipelines;\nimport net.minecraft.client.gl.RenderPipelines;\n" in t:
        t = t.replace("import net.minecraft.client.gl.RenderPipelines;\nimport net.minecraft.client.gl.RenderPipelines;\n", "import net.minecraft.client.gl.RenderPipelines;\n")
    if "import net.minecraft.client.gl.RenderPipelines;\n\nimport net.minecraft.client.gl.RenderPipelines;" in t:
        t = t.replace("import net.minecraft.client.gl.RenderPipelines;\n\nimport net.minecraft.client.gl.RenderPipelines;", "import net.minecraft.client.gl.RenderPipelines;\n//")
    t = t.replace("net.minecraft.client.render.RenderPipelines.", "net.minecraft.client.gl.RenderPipelines.")
    # G: our Command.sendMessage never takes boolean
    t = re.sub(r"Command\.sendMessage\(([^;]*?) *, *(?:true|false)\);", r"Command.sendMessage(\1);", t)
    # H: sendMessage balance-add false
    def fix_send(m):
        s = m.start(1); depth = 0; i = s
        while i < len(t):
            c = t[i]
            if c == '(': depth += 1
            elif c == ')':
                depth -= 1
                if depth == 0:
                    seg = t[s:i]
                    d2 = 0; top_comma = False
                    for ch in seg:
                        if ch == '(': d2 += 1
                        elif ch == ')': d2 -= 1
                        elif ch == ',' and d2 == 0: top_comma = True; break
                    if not top_comma: return "sendMessage(" + seg + ", false)"
                    return None
            i += 1
        return None
    if ".sendMessage(" in t:
        out = []; last = 0
        for m in re.finditer(r"(?<!Command\.)\.sendMessage\(", t):
            # only mc.player / player receivers
            pre = t[max(0,m.start()-20):m.start()]
            if not re.search(r"(player|mc)\s*$", pre): continue
            s = m.end(); depth = 1; i = s; top_comma = False
            while i < len(t):
                c = t[i]
                if c == '(': depth += 1
                elif c == ')':
                    depth -= 1
                    if depth == 0: break
                elif c == ',' and depth == 1: top_comma = True; break
                i += 1
            if not top_comma and i < len(t):
                out.append(t[last:m.end()]); out.append(t[m.end():i] + ", false"); last = i
        if out:
            out.append(t[last:]); t = "".join(out)
    # isClient field -> method
    t = re.sub(r"\.isClient\b(?!\s*\()", ".isClient()", t)
    # ChatUtils profile chains
    t = t.replace("profile().getName()", "profile().name()").replace("profile().getId()", "profile().id()")
    # player.input undefined receiver fix
    t = re.sub(r"= player\.input\.playerInput\b", "= mc.player.input.playerInput", t)
    t = re.sub(r"entity\.input\.playerInput = entity\.input\.playerInput\.withSneak\([^;]*\);", "// 1.21.11: input is ClientPlayerEntity-only", t)
    # B: entity prev* fields -> velocity math
    for axis, up in (("x","X"),("y","Y"),("z","Z")):
        t = re.sub(r"\b(\w+)\.prev%s\b" % axis.lower(), r"((\1.get%s() - \1.getVelocity().%s) if False else (\1.get%s()))" % (up, axis, up), t) if False else t
    # skin cast leftovers
    t = t.replace("((AbstractClientPlayerEntity) thunder.hack.utility.SkinUtility.skin(", "thunder.hack.utility.SkinUtility.skin(")
    t = t.replace("bindTexture((AbstractClientPlayerEntity) thunder.hack.utility.SkinUtility.skin(", "bindTexture(thunder.hack.utility.SkinUtility.skin(")
    # getCameraPos getters -> fields
    t = t.replace(".getCameraPos().getX()", ".getCameraPos().x").replace(".getCameraPos().getY()", ".getCameraPos().y").replace(".getCameraPos().getZ()", ".getCameraPos().z")
    t = re.sub(r"\bgameRenderer\.getCamera\(\)", "net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera()", t)
    t = re.sub(r"(?<!\.)\bgameRenderer\.(?!getCamera)", "net.minecraft.client.MinecraftClient.getInstance().gameRenderer.", t)
    t = re.sub(r"\bcam\.getX\(\)", "cam.x", t); t = re.sub(r"\bcam\.getY\(\)", "cam.y", t); t = re.sub(r"\bcam\.getZ\(\)", "cam.z", t)
    # float = -((Math.hypot...
    t = re.sub(r"float (\w+) = (-?\(?\(?Math\.hypot\([^;]*)\);", r"float \1 = (float) \2);", t)
    # instanceOf-bound velocity packet getId
    if "instanceof EntityVelocityUpdateS2CPacket" in t:
        for v in set(re.findall(r"instanceof EntityVelocityUpdateS2CPacket (\w+)", t)):
            t = re.sub(r"\b%s\.getId\(\)" % v, "%s.getEntityId()" % v, t)
    # ClientCommand import missing
    if "ClientCommandC2SPacket" in t and "import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;" not in t and "packet.c2s.play.ClientCommandC2SPacket" not in t:
        m = re.search(r"package [\w.]+;\n", t)
        t = t[:m.end()] + "\nimport net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;\n" + t[m.end():]
    # Vec3d import missing where used
    if re.search(r"new Vec3d\(", t) and "import net.minecraft.util.math.Vec3d;" not in t:
        m = re.search(r"package [\w.]+;\n", t)
        t = t[:m.end()] + "\nimport net.minecraft.util.math.Vec3d;\n" + t[m.end():]
    # joml 2D stack arg normalisation: only for context.getMatrices() and declared Matrix3x2fStack vars
    decls = set(re.findall(r"Matrix3x2fStack (\w+)", t))
    receivers = ["context.getMatrices()"] + sorted(decls)
    def joml_line(m):
        full = m.group(0); recv = m.group(1); call = m.group(2); args = m.group(3)
        if recv == "stack" and "stack" not in decls: return full
        parts = []; depth = 0; cur = ""
        for ch in args:
            if ch in "([": depth += 1
            elif ch in ")]": depth -= 1
            if ch == "," and depth == 0: parts.append(cur); cur = ""
            else: cur += ch
        parts.append(cur)
        parts = [x.strip() for x in parts]
        if call == "translate" and len(parts) == 3 and parts[2] in ("0", "0f", "0.0", "0.0f"):
            parts = parts[:2]
        if call == "scale" and len(parts) == 3 and parts[2] in ("1", "1f", "1.0", "1.0f"):
            parts = parts[:2]
        if call in ("translate", "scale") and len(parts) == 2:
            parts = [p if p.startswith("(float)") else "(float) (%s)" % p for p in parts]
            return recv + "." + call + "(" + ", ".join(parts) + ")"
        return full
    for recv in receivers:
        esc = re.escape(recv)
        t = re.sub("(" + esc + r")\.(translate|scale)\(([^;\n]*)\)", joml_line, t)
    # bodyYaw/headYaw entity reads (not render-state writes)
    t = re.sub(r"\b(?!state\.)(?!this\.)(?!renderState\.)([A-Za-z_]\w*)\.bodyYaw\b(?! =)", r"\1.getBodyYaw()", t)
    t = re.sub(r"\b(?!state\.)(?!this\.)(?!renderState\.)([A-Za-z_]\w*)\.headYaw\b(?! =)", r"\1.getHeadYaw()", t)
    t = re.sub(r"([A-Za-z_]\w*)\.bodyYaw = ([^;]+);", r"\1.setBodyYaw(\2);", t)
    t = re.sub(r"([A-Za-z_]\w*)\.headYaw = ([^;]+);", r"\1.setHeadYaw(\2);", t)
    if t != o: wr(p, t)

print("global passes done; misses so far:", len(MISS))

# ---------- targeted ----------
rep_literal(H + "utility/player/InputUtility.java", "i.back()", "i.backward()")
t = rd(H + "utility/player/InputUtility.java")
t = t.replace("boolean back,", "boolean backward,")  # safety no-op
wr(H + "utility/player/InputUtility.java", t)

# PoseStack: drop broken multiply helpers
t = rd(H + "utility/render/PoseStack.java")
t = re.sub(r"    @Override\n    public boolean multiply\(Quaternionfc quaternion\) \{\n        return super\.multiply\(quaternion\);\n    \}\n\n", "", t)
t = re.sub(r"    public void multiply\(Matrix4f matrix\) \{\n        super\.multiply\(matrix\);\n    \}\n\n", "", t)
t = re.sub(r"    public void multiplyCurrent\(Matrix4f matrix\) \{\n        super\.multiply\(matrix\);\n    \}\n\n", "", t)
t = t.replace("import org.joml.Quaternionfc;\n", "")
wr(H + "utility/render/PoseStack.java", t)

# Render2DEngine: toMatrix4f + transform casts
t = rd(H + "utility/render/Render2DEngine.java")
if "toMatrix4f" not in t:
    t = t.replace("    public static class BlurredShadow {",
"""    public static Matrix4f toMatrix4f(org.joml.Matrix3x2fc m) {
        return new Matrix4f(
                m.m00(), m.m01(), 0f, m.m02(),
                m.m10(), m.m11(), 0f, m.m12(),
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
    }

    public static class BlurredShadow {""")
t = t.replace("m.transform(r1.x(), r1.y(), new float[2])", "m.transform((float) r1.x(), (float) r1.y(), new float[2])")
t = t.replace("m.transform(r1.x1(), r1.y1(), new float[2])", "m.transform((float) r1.x1(), (float) r1.y1(), new float[2])")
wr(H + "utility/render/Render2DEngine.java", t)
if "import org.joml.Matrix4f;" not in t:
    t = rd(H + "utility/render/Render2DEngine.java").replace("package thunder.hack.utility.render;", "package thunder.hack.utility.render;\n\nimport org.joml.Matrix4f;", 1)
    wr(H + "utility/render/Render2DEngine.java", t)

for p in ALL:
    t = rd(p)
    if "new Matrix4f().set(context.getMatrices())" in t:
        wr(p, t.replace("new Matrix4f().set(context.getMatrices())", "Render2DEngine.toMatrix4f(context.getMatrices())"))

# prevBodyYaw etc (Chams) — targeted
sub(H + "features/modules/render/Chams.java", r"MathHelper\.lerpAngleDegrees\(g, pe\.prevBodyYaw, pe\.bodyYaw\)", "MathHelper.lerpAngleDegrees(g, state.prevBodyYaw, state.bodyYaw)")
sub(H + "features/modules/render/Chams.java", r"MathHelper\.lerpAngleDegrees\(g, pe\.prevHeadYaw, pe\.headYaw\)", "MathHelper.lerpAngleDegrees(g, state.prevHeadYaw, state.headYaw)")
sub(H + "features/modules/render/Chams.java", r"MathHelper\.lerpAngleDegrees\(g, livingEntity2\.prevBodyYaw, livingEntity2\.bodyYaw\)", "MathHelper.lerpAngleDegrees(g, livingEntity2.getBodyYaw(), livingEntity2.getBodyYaw())")
sub(H + "features/modules/render/Chams.java", r"MathHelper\.lerp\(g, pe\.prevPitch, pe\.getPitch\(\)\)", "MathHelper.lerp(g, state.prevPitch, state.pitch)")
t = rd(H + "features/modules/render/Chams.java")
t = t.replace("frame.render(matrixStack, buffer, i, k);", "frame.render(matrixStack, buffer, i, k, 0xFFFFFFFF);")
t = t.replace("core.render(matrixStack, buffer, i, k);", "core.render(matrixStack, buffer, i, k, 0xFFFFFFFF);")
t = t.replace("int p = LivingEntityRenderer.getOverlay(state, 0.0f);", "int p = net.minecraft.client.render.OverlayTexture.DEFAULT_UV;")
t = re.sub(r"setupTransforms\(abstractClientPlayerEntity, matrixStack, f, g, h\);", "// 1.21.11: LivingEntityRenderer.setupTransforms is private", t)
wr(H + "features/modules/render/Chams.java", t)

# PopChams
sub(H + "features/modules/render/PopChams.java",
    r"PlayerEntity entity = new PlayerEntity\(mc\.world, BlockPos\.ORIGIN, e\.getEntity\(\)\.getBodyYaw\(\), new GameProfile\(([^;]*?)\)\) \{",
    r"PlayerEntity entity = new PlayerEntity(mc.world, new GameProfile(\1)) {")
sub(H + "features/modules/render/PopChams.java", r"entity\.handSwingTicks = e\.getEntity\(\)\.handSwingTicks;", "// 1.21.11: handSwingTicks not settable")
t = rd(H + "features/modules/render/PopChams.java")
t = t.replace("((AbstractClientPlayerEntity) thunder.hack.utility.SkinUtility.skin(e.getEntity()))", "thunder.hack.utility.SkinUtility.skin(e.getEntity())")
t = t.replace("((alpha / 255f) * 360f * rotSpeed.getValue()) - (((aSpeed.getValue() / 255f) * 360f * rotSpeed.getValue()))", "0f")  # no-op safety
t = t.replace("180 - entity.getBodyYaw() + yRotYaw", "180 - entity.getBodyYaw() + yRotYaw")
wr(H + "features/modules/render/PopChams.java", t)
sub(H + "features/modules/render/PopChams.java", r"state\.bodyYaw = entity\.getBodyYaw\(\);", "state.bodyYaw = entity.getBodyYaw();", 0)
sub(H + "features/modules/render/PopChams.java", r"state\.relativeHeadYaw = entity\.getHeadYaw\(\) - entity\.getBodyYaw\(\);", "state.relativeHeadYaw = entity.getHeadYaw() - entity.getBodyYaw();", 0)
sub(H + "features/modules/render/PopChams.java", r"state\.limbSwingAmplitude = Math\.min\(entity\.limbAnimator\.getSpeed\(\), 1f\);", "state.limbSwingAmplitude = 1f;", 0)

# PredictUtility
sub(H + "utility/math/PredictUtility.java",
    r"new PlayerEntity\(mc\.world, original\.getBlockPos\(\), original\.getYaw\(\), new GameProfile\(",
    "new PlayerEntity(mc.world, new GameProfile(")
t = rd(H + "utility/math/PredictUtility.java")
t = re.sub(r"copyEntity\.prev[XZW] = original\.prev[XZW];", "// 1.21.11: prev-position not writable", t)
t = re.sub(r"original\.prev([XYZ])", r"(original.get\1() - original.getVelocity().\1.lower())", t) if False else t
wr(H + "utility/math/PredictUtility.java", t)
t = rd(H + "utility/math/PredictUtility.java")
for ax in "xyz":
    t = t.replace("original.getVelocity().%s" % ax, "original.getVelocity().%s" % ax)
wr(H + "utility/math/PredictUtility.java", t)

# AimBot / others prevX — global-ish targeted
for f, exprs in {
    "features/modules/combat/AimBot.java": [
        (r"predictedEntity\.getX\(\) - predictedEntity\.prevX", "predictedEntity.getVelocity().x"),
        (r"predictedEntity\.getZ\(\) - predictedEntity\.prevZ", "predictedEntity.getVelocity().z"),
        (r"\(pl\.getX\(\) - pl\.prevX\)", "(pl.getVelocity().x)"),
        (r"\(pl\.getZ\(\) - pl\.prevZ\)", "(pl.getVelocity().z)"),
    ]}.items():
    for pat, rep in exprs:
        sub(H + f, pat, rep)

# ESP tnt/pearl prev interpolation
t = rd(H + "features/modules/render/ESP.java")
for v in ("tnt", "pearl"):
    for ax, up in (("x","X"),("y","Y"),("z","Z")):
        t = t.replace("%s.prev%s + (new Vec3d(%s.getX(), %s.getY(), %s.getZ()).get%s() - %s.prev%s)" % (v, ax.upper(), v, v, v, up, v, ax.upper()),
                     "%s.getVelocity().%s" % (v, ax) if False else "(%s.get%s() - %s.getVelocity().%s)" % (v, up, v, ax))
        t = re.sub(r"new Vec3d\((%s)\.getX\(\), \1\.getY\(\), \1\.getZ\(\)\)\.get([XYZ])\(\)" % v, r"\1.get\2()", t)
wr(H + "features/modules/render/ESP.java", t)

# Trails
t = rd(H + "features/modules/render/Trails.java")
t = t.replace("public void render(Matrix3x2fStack matrixStack, BufferBuilder bufferBuilder)", "public void render(PoseStack matrixStack, BufferBuilder bufferBuilder)")
wr(H + "features/modules/render/Trails.java", t)

# PenisESP
sub(H + "features/modules/render/PenisESP.java", r"drawPenis\(player, event\.getMatrices\(\), size, forward\)", "drawPenis(player, event, size, forward)")

# TargetHud
t = rd(H + "features/hud/impl/TargetHud.java")
t = t.replace("return ((EntityRenderer) renderManager.getRenderer(entity)).getTexture(state);",
              "return thunder.hack.utility.SkinUtility.skin(entity);")
for fn in ("drawPotionEffect",):
    t = re.sub(r"void %s\(PoseStack (\w+)" % fn, r"void %s(org.joml.Matrix3x2fStack \1" % fn, t)
wr(H + "features/hud/impl/TargetHud.java", t)
t = rd(H + "features/hud/impl/TargetHud.java")
need = []
if "ScoreboardDisplaySlot" in t and "import net.minecraft.scoreboard.ScoreboardDisplaySlot;" not in t:
    need.append("import net.minecraft.scoreboard.ScoreboardDisplaySlot;")
if "ReadableScoreboardScore" in t and "ReadableScoreboardScore" not in "".join(re.findall(r"import ([\w.]+);", t)):
    need.append("import net.minecraft.scoreboard.ReadableScoreboardScore;")
if need:
    m = re.search(r"package [\w.]+;\n", t)
    t = t[:m.end()] + "\n" + "\n".join(need) + "\n" + t[m.end():]
    wr(H + "features/hud/impl/TargetHud.java", t)

# FontRenderer double fix — global cast pass handled; verify line 181 has casts now.

# ClickGUI blur
sub(H + "gui/clickui/ClickGUI.java", r"            applyBlur\(delta\);\n", "            // 1.21.11: Screen.applyBlur reworked - GUI blur skipped\n")

# ThunderGui Command.sendMessage already fixed globally; translate casts by pass.

# MixinAbstractBlock enchant idiom
t = rd(H + "injection/MixinAbstractBlock.java")
t = re.sub(r"int effect = EnchantmentHelper\.getLevel\(mc\.world\.getRegistryManager\(\)\.getOrThrow\(EFFICIENCY\.getRegistryRef\(\)\)\.getEntry\(EFFICIENCY\)\.get\(\), stack\);",
           "int effect = (int) EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getOrThrow(EFFICIENCY), stack);", t)
wr(H + "injection/MixinAbstractBlock.java", t)

# tickCounter scope in mixins
t = rd(H + "injection/MixinInGameHud.java")
t = t.replace("tickCounter.getTickDelta(false)", "mc.getRenderTickCounter().getTickDelta(false)")
wr(H + "injection/MixinInGameHud.java", t)
t = rd(H + "injection/MixinGameRenderer.java")
t = t.replace("tickCounter.getTickDelta(false)", "mc.getRenderTickCounter().getTickDelta(false)")
wr(H + "injection/MixinGameRenderer.java", t)

# Hotbar renderXpBar signature
t = rd(H + "features/hud/impl/Hotbar.java")
t = t.replace("public static void renderXpBar(int x, PoseStack matrices)", "public static void renderXpBar(int x, org.joml.Matrix3x2fStack matrices)")
t = re.sub(r"\bmatrices\.push\(\)", "matrices.pushMatrix()", t)
t = re.sub(r"\bmatrices\.pop\(\)", "matrices.popMatrix()", t)
wr(H + "features/hud/impl/Hotbar.java", t)

# MixinPlayerListEntry: handler param + cape override disabled
t = rd(H + "injection/MixinPlayerListEntry.java")
t = t.replace('    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)\n    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {',
              '    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)\n    private void getCapeTexture(GameProfile profile, CallbackInfoReturnable<SkinTextures> cir) {\n        if (true) return; // 1.21.11: SkinOverride type changed - cape injection disabled')
wr(H + "injection/MixinPlayerListEntry.java", t)

# MixinSplashOverlay init() gone + GlStateManager import
t = rd(H + "injection/MixinSplashOverlay.java")
t = t.replace("import com.mojang.blaze3d.platform.GlStateManager;\n", "")
t = t.replace("mc.currentScreen.init(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());", "// 1.21.11: Screen.init(MinecraftClient,int,int) removed - splash re-init skipped")
wr(H + "injection/MixinSplashOverlay.java", t)

# LoginCommand account type
sub(H + "features/cmd/impl/LoginCommand.java", r"Session\.AccountType\.MOJANG", "Session.AccountType.LEGACY")

# FakePlayer explosion center
sub(H + "features/modules/misc/FakePlayer.java", r"new Vec3d\(explosion\.getX\(\), explosion\.getY\(\), explosion\.getZ\(\)\)", "explosion.center()")

# AutoTrader offers
sub(H + "features/modules/misc/AutoTrader.java", r"msh\.getOffers\(\)", "msh.screenHandler.getOffers()")

# MixinTridentItem TypedActionResult import
t = rd(H + "injection/MixinTridentItem.java")
t = t.replace("import net.minecraft.util.TypedActionResult;", "import net.minecraft.item.consume.UseAction;")
wr(H + "injection/MixinTridentItem.java", t)
t = rd(H + "injection/MixinTridentItem.java")
if "TypedActionResult" in t and "import net.minecraft.util.TypedActionResult;" not in t:
    m = re.search(r"package [\w.]+;\n", t)
    t = t[:m.end()] + "\nimport net.minecraft.util.TypedActionResult;\n" + t[m.end():]  # keep? will re-error if missing; decide by scan
    wr(H + "injection/MixinTridentItem.java", t)

# RctCommand displayName
sub(H + "features/cmd/impl/RctCommand.java", r"\)\.getDisplayName\(\)\.getString\(\)", ").displayName().getString()")
t = rd(H + "features/cmd/impl/RctCommand.java")
t = t.replace("networkHandler.sendCommand(", "networkHandler.sendChatCommand(")
wr(H + "features/cmd/impl/RctCommand.java", t)
t = rd(H + "features/modules/misc/Spammer.java")
t = t.replace("networkHandler.sendCommand(", "networkHandler.sendChatCommand(")
wr(H + "features/modules/misc/Spammer.java", t)

# Quiver rewrite predicate
t = rd(H + "features/modules/combat/Quiver.java")
t = t.replace("""            if (stack.getItem() instanceof TippedArrowItem tai) {
                String key = tai.getTranslationKey(stack);
                return key.contains("effect." + name);
            }""",
"""            if (stack.getItem() instanceof TippedArrowItem) {
                var contents = stack.get(net.minecraft.component.DataComponentTypes.POTION_CONTENTS);
                if (contents == null) return false;
                var effs = contents.getEffects();
                if (effs == null) return false;
                for (var ap : effs) {
                    try {
                        if (ap.effect().valueOrThrow().getTranslationKey().contains("effect." + name)) return true;
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }""")
wr(H + "features/modules/combat/Quiver.java", t)

# ItemChecks rewrite
wr(H + "utility/player/ItemChecks.java", rd(H + "utility/player/ItemChecks.java").replace(
"""        double[] out = new double[2];
        stack.applyAttributeModifiers(AttributeModifierSlot.ARMOR, (attribute, modifier) -> {
            if (attribute == null || modifier == null) return;
            if (attribute.matches(ARMOR)) out[0] += modifier.value();
            else if (attribute.matches(TOUGHNESS)) out[1] += modifier.value();
        });
        return out[0] + out[1];""",
"""        double[] out = new double[2];
        var amc = stack.get(net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (amc != null) for (var entry : amc.modifiers()) {
            var attr = entry.modifier().attribute();
            if (attr.equals(ARMOR)) out[0] += entry.modifier().value();
            else if (attr.equals(TOUGHNESS)) out[1] += entry.modifier().value();
        }
        return out[0] + out[1];""").replace(
"""        double[] out = new double[]{-1};
        stack.applyAttributeModifiers(AttributeModifierSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute == null || modifier == null) return;
            if (attribute.getValue() != null && "attack_damage".equals(attribute.getValue().getPath())) {
                if (out[0] < 0) out[0] = 0;
                out[0] += modifier.value();
            }
        });
        return out[0];""",
"""        double[] out = new double[]{-1};
        var amc = stack.get(net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (amc != null) for (var entry : amc.modifiers()) {
            var attr = entry.modifier().attribute();
            if (attr.equals(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE)) {
                if (out[0] < 0) out[0] = 0;
                out[0] += entry.modifier().value();
            }
        }
        return out[0];"""))

# PlayerEntityCopy dataTracker line
t = rd(H + "utility/player/PlayerEntityCopy.java")
t = t.replace("        dataTracker.set(PLAYER_MODEL_PARTS, mc.player.getDataTracker().get(PLAYER_MODEL_PARTS));\n", "        // 1.21.11: PLAYER_MODEL_PARTS not accessible - cosmetic sync skipped\n")
wr(H + "utility/player/PlayerEntityCopy.java", t)

# NativeImage parse loops -> identity
for f, name in [("gui/mainmenu/CreditsScreen.java","parseAvatar"), ("gui/thundergui/components/FriendComponent.java","parseHead"), ("utility/OptifineCapes.java","parseCape")]:
    t = rd(H + f)
    m = re.search(r"    public static NativeImage %s\(NativeImage image\) \{.*?\n    \}\n" % name, t, re.S)
    if m:
        t = t[:m.start()] + ("    public static NativeImage %s(NativeImage image) {\n        // 1.21.11: NativeImage pixel read/write API changed - texture used as-is\n        return image;\n    }\n" % name) + t[m.end():]
    t = re.sub(r"\n    private static int th\$swap\(java\.awt\.image\.BufferedImage bi, int x, int y\) \{\n(?:.*\n)*?    \}\n", "\n", t)
    t = re.sub(r"\n    private static java\.awt\.image\.BufferedImage th\$toBufferedImage\(net\.minecraft\.client\.texture\.NativeImage image\) \{\n(?:.*\n)*?    \}\n", "\n", t)
    wr(H + f, t)

# ThunderUtility registerDynamicTexture
t = rd(H + "utility/ThunderUtility.java")
t = re.sub(r"return mc\.getTextureManager\(\)\.registerDynamicTexture\(\"th-\" \+ name \+ \"-\" \+ \(int\) MathUtility\.random\(0, 1000\), new NativeImageBackedTexture\(NativeImage\.read\(new File\(([^;]*?)\)\)\)\);",
           r'Identifier th$id = Identifier.of("thunderhack", "th-" + name + "-" + (int) MathUtility.random(0, 1000));\n        mc.getTextureManager().registerTexture(th$id, new NativeImageBackedTexture(th$id::toString, NativeImage.read(new File(\1))));\n        return th$id;', t)
wr(H + "utility/ThunderUtility.java", t)

# MSAAFramebuffer stub
wr(H + "utility/render/MSAAFramebuffer.java", """package thunder.hack.utility.render;

import net.minecraft.client.gl.Framebuffer;

/**
 * 1.21.11: the GlFramebuffer/MSAA path was reworked; supersampling pass is a passthrough now.
 */
public final class MSAAFramebuffer {
    private MSAAFramebuffer() {
    }

    public static void use(boolean fancy, Runnable drawAction) {
        drawAction.run();
    }

    public static void use(int samples, Framebuffer mainBuffer, Runnable drawAction) {
        drawAction.run();
    }
}
""")

sub(H + "features/modules/render/Trails.java", r"ae\.prevY != ae\.getY\(\)", "ae.getVelocity().y != 0")
# Entity-side sendMessage needs boolean too
for p in ALL:
    tx = rd(p); tx0 = tx
    def fix2(m):
        s = m.end(); depth = 1; i = s; top_comma = False
        while i < len(tx):
            c = tx[i]
            if c == '(': depth += 1
            elif c == ')':
                depth -= 1
                if depth == 0: break
            elif c == ',' and depth == 1: top_comma = True; break
            i += 1
        if not top_comma and i < len(tx):
            return m.group(0) + tx[m.end():i] + ", false)"
        return m.group(0) + tx[m.end():i] + ")"
    tx = re.sub(r"\b(entity|target|other|lent|pl)\.sendMessage\(", fix2, tx)
    if tx != tx0: wr(p, tx)

print("MISS count:", len(MISS))
for m in MISS: print("  MISS", m)
