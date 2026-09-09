#!/usr/bin/env python3
# fix11: round-13 error batch (75 errors) — targeted, verified against yarn 1.21.11 (delta/bursultan/Expensive donors)
import os, re, sys

ROOT = "/home/user/NHACKRECODE/src/main/java/thunder/hack"
applied, missed = [], []

def rd(p):
    with open(os.path.join(ROOT, p), encoding="utf-8") as f: return f.read()
def wr(p, s):
    with open(os.path.join(ROOT, p), "w", encoding="utf-8") as f: f.write(s)

def patch(tag, p, old, new, count=None, regex=False):
    s = rd(p)
    if regex:
        pat = re.compile(old)
        hits = len(pat.findall(s))
        if hits == 0:
            missed.append(tag); return
        s2 = pat.sub(new, s)
    else:
        hits = s.count(old)
        if hits == 0:
            missed.append(tag); return
        s2 = s.replace(old, new)
    if count is not None and hits != count:
        print(f"!! {tag}: expected {count} hits, got {hits}"); sys.exit(1)
    wr(p, s2); applied.append(f"{tag} ({hits})")

def find_file(name):
    for dp, _, fn in os.walk(ROOT):
        if name in fn: return os.path.relpath(os.path.join(dp, name), ROOT)
    return None

# 1. ModuleManager: PVETools lives in misc, not render
patch("1.pve-import", "core/manager/client/ModuleManager.java",
      "import thunder.hack.features.modules.render.PVETools;",
      "import thunder.hack.features.modules.misc.PVETools;")

# 2. MixinLivingEntityRenderer
P = "injection/MixinLivingEntityRenderer.java"
patch("2a.features-shadow", P,
      "protected List<FeatureRenderer<T, M>> features;",
      "protected List<FeatureRenderer<?, ?>> features;")
patch("2b.features-loop", P,
      "for (FeatureRenderer<T, M> featureRenderer : features)",
      "for (FeatureRenderer<?, ?> featureRenderer : features)")
patch("2c.bodyyaw-field", P, "Managers.PLAYER.getBodyYaw()", "Managers.PLAYER.bodyYaw", count=2)
patch("2d.inner-require", P, 'VertexConsumer;III)V", require = 0), require = 0)', 'VertexConsumer;III)V"), require = 0)')

# 3. global getControllingVehicle -> getVehicle (1.21.11)
n = 0
for dp, _, fn in os.walk(ROOT):
    for f in fn:
        if not f.endswith(".java"): continue
        fp = os.path.join(dp, f)
        s = open(fp, encoding="utf-8").read()
        if "getControllingVehicle()" in s:
            c = s.count("getControllingVehicle()")
            open(fp, "w", encoding="utf-8").write(s.replace("getControllingVehicle()", "getVehicle()"))
            n += c
print(f"3.getVehicle: {n} replacements")

# 4. CommandManager ClientCommandSource 3-arg
patch("4.ccmdsrc", "core/manager/client/CommandManager.java",
      "new ClientCommandSource(null, MinecraftClient.getInstance())",
      "new ClientCommandSource((net.minecraft.client.network.ClientPlayNetworkHandler) null, MinecraftClient.getInstance(), net.minecraft.command.permission.PermissionPredicate.ALL)")

# 5. MouseElytraFix imports
P = find_file("MouseElytraFix.java")
s = rd(P)
if "import net.minecraft.item.ItemStack;" not in s:
    s = s.replace("import net.minecraft.item.Items;",
                  "import net.minecraft.item.Items;\nimport net.minecraft.item.ItemStack;\nimport net.minecraft.entity.EquipmentSlot;")
    wr(P, s); applied.append("5.mef-imports")
else: missed.append("5.mef-imports")

# 6. ToolSaver MiningToolItem -> TOOL component
patch("6.toolsaver", find_file("ToolSaver.java"),
      "if(!(tool.getItem() instanceof MiningToolItem))",
      "if(!(tool.contains(net.minecraft.component.DataComponents.TOOL)))")

# 7. TotemAnimation drawItem arity
patch("7.totem.drawItem", find_file("TotemAnimation.java"),
      "context.drawItem(m, floatingItem, -8, -8, 0);",
      "context.drawItem(floatingItem, -8, -8);")

# 8. TridentBoost useRiptide
patch("8.riptide", find_file("TridentBoost.java"),
      "mc.player.tryUseRiptide();",
      "mc.player.useRiptide(20, 8f, mc.player.getActiveItem());")

# 9. Strafe/TargetStrafe entityId()
for f in ("Strafe.java", "TargetStrafe.java"):
    patch(f"9.{f}", find_file(f), "(velocity = e.getPacket()).getId()", "(velocity = e.getPacket()).entityId()")

# 10. HitParticles texture bind
patch("10.hitpart.bind", find_file("HitParticles.java"),
      "com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, texture);",
      "thunder.hack.utility.render.Render2DEngine.bindTexture(texture);")

# 11. ServerHelper
P = find_file("ServerHelper.java")
patch("11a.sendChatCommand", P, "networkHandler.sendCommand(", "networkHandler.sendChatCommand(", count=2)
patch("11b.transkey", P, "return stack.getTranslationKey();", "return stack.getItem().getTranslationKey();")

# 12. Entity.getPos() removed
for f in ("PearlChaser.java", "PearlBait.java"):
    patch(f"12.{f}", find_file(f),
          "p.squaredDistanceTo(e.getEntity().getPos())",
          "p.squaredDistanceTo(new net.minecraft.util.math.Vec3d(e.getEntity().getX(), e.getEntity().getY(), e.getEntity().getZ()))")
patch("12c.boatpos", find_file("BoatFly.java"),
      "Vec3d boatPos = mc.player.getVehicle().getPos();",
      "Vec3d boatPos = new Vec3d(mc.player.getVehicle().getX(), mc.player.getVehicle().getY(), mc.player.getVehicle().getZ());")

# 13. BoatFly packets + BoatEntity
P = find_file("BoatFly.java")
patch("13a.vehiclemove", P,
      r"new VehicleMoveC2SPacket\((\w+)\.getX\(\),\s*\1\.getY\(\),\s*\1\.getZ\(\),\s*\1\.getYaw\(\),\s*\1\.getPitch\(\)\)",
      r"new VehicleMoveC2SPacket(\1)", regex=True)
patch("13b.boatentity", P,
      "new BoatEntity(net.minecraft.entity.EntityType.OAK_BOAT, mc.world)",
      "new BoatEntity(net.minecraft.entity.EntityType.OAK_BOAT, mc.world, () -> net.minecraft.item.Items.OAK_BOAT)")

# 14. model construction via LoadedEntityModels (LogoutSpots/PopChams)
for f in ("LogoutSpots.java", "PopChams.java"):
    patch(f"14a.{f}", find_file(f),
          "PlayerEntityModel.getTexturedModelData(Dilation.NONE, false)",
          "mc.getLoadedEntityModels().getModelPart(net.minecraft.client.render.entity.model.EntityModelLayers.PLAYER)")
patch("14b.model-render", find_file("LogoutSpots.java"),
      "modelBase.render(state, matrices, buffer, 10, 0);",
      "modelBase.render(matrices, buffer, 10, 0);")
# 14c. render-state setBodyYaw -> public field
n = 0
for dp, _, fn in os.walk(ROOT):
    for f in fn:
        if not f.endswith(".java"): continue
        fp = os.path.join(dp, f); s = open(fp, encoding="utf-8").read()
        if "state.setBodyYaw(" in s:
            c = s.count("state.setBodyYaw(")
            s2 = re.sub(r"state\.setBodyYaw\(([^;]*)\);", r"state.bodyYaw = \1;", s)
            open(fp, "w", encoding="utf-8").write(s2); n += c
print(f"14c.state.bodyYaw: {n} replacements")

# 15. Animations heldItemRenderer arg order
patch("15.anim.renderItem", find_file("Animations.java"),
      ".renderItem(entity, renderMode, stack, matrices, vertexConsumers, light)",
      ".renderItem(entity, stack, renderMode, matrices, vertexConsumers, light)")

# 16. Render2DEngine.drawBubble 3D rewrite
P = os.path.join(ROOT, "utility/render/Render2DEngine.java")
s = open(P, encoding="utf-8").read()
sig = "    public static void drawBubble(Matrix3x2fStack matrices, float angle, float factor) {"
i = s.find(sig)
if i < 0:
    missed.append("16.drawbubble")
else:
    depth, j = 0, s.index("{", i)
    k = j
    while True:
        if s[k] == "{": depth += 1
        elif s[k] == "}":
            depth -= 1
            if depth == 0: break
        k += 1
    NEW = """    public static void drawBubble(net.minecraft.client.util.math.MatrixStack matrices, float angle, float factor) {
        bindTexture(TextureStorage.bubble);
        setupRender();
        matrices.push();
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotation((float) Math.toRadians(angle)));
        float scale = factor * 2f;
        float h = scale / 2f;
        org.joml.Matrix4f matrix = matrices.peek().getPositionMatrix();
        Color c = HudEditor.getColor(0);
        int a = (int) (255f * Math.max(0f, Math.min(1f, 1f - factor)));
        net.minecraft.client.render.BufferBuilder buffer = net.minecraft.client.render.Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, -h, -h, 0f).texture(0f, 0f).color(c.getRed(), c.getGreen(), c.getBlue(), a);
        buffer.vertex(matrix, -h, h, 0f).texture(0f, 1f).color(c.getRed(), c.getGreen(), c.getBlue(), a);
        buffer.vertex(matrix, h, h, 0f).texture(1f, 1f).color(c.getRed(), c.getGreen(), c.getBlue(), a);
        buffer.vertex(matrix, h, -h, 0f).texture(1f, 0f).color(c.getRed(), c.getGreen(), c.getBlue(), a);
        endBuilding(buffer);
        matrices.pop();
    }"""
    s = s[:i] + NEW + s[k+1:]
    open(P, "w", encoding="utf-8").write(s); applied.append("16.drawbubble")

# 17. AutoTotem isCoolingDown(ItemStack)
patch("17.autototem", "features/modules/combat/AutoTotem.java",
      "isCoolingDown(Items.SHIELD)", "isCoolingDown(Items.SHIELD.getDefaultStack())")

# 18. enchant registry lookups
patch("18a.speedmine", find_file("SpeedMine.java"),
      "EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(Enchantments.PROTECTION.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get(), stack)",
      "EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY), stack)")
patch("18b.autoarmor", find_file("AutoArmor.java"),
      "mc.world.getRegistryManager().getOrThrow(Enchantments.BLAST_PROTECTION.getRegistryRef()).getEntry(Enchantments.BINDING_CURSE).get()",
      "mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE)")

# 19. NameTags
P = find_file("NameTags.java")
patch("19a.nt.contains", P,
      "enchants.getEnchantments().contains(mc.world.getRegistryManager().getOrThrow(Enchantments.PROTECTION.getRegistryRef()).getEntry(enchantment).get())",
      "enchants.getEnchantments().contains(mc.world.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getOrThrow(enchantment))")
patch("19b.nt.level", P,
      "enchants.getLevel(mc.world.getRegistryManager().getOrThrow(Enchantments.PROTECTION.getRegistryRef()).getEntry(enchantment).get())",
      "enchants.getLevel(mc.world.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getOrThrow(enchantment))")
patch("19c.nt.gui", P,
      "context.drawGuiTexture(type.getTexture(half), x, 0, 9, 9);",
      "context.drawGuiTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, type.getTexture(half), x, 0, 9, 9);")
patch("19d.nt.scoreboard", P, "ent.getScoreboard()", "ent.getEntityWorld().getScoreboard()", count=None)
patch("19e.nt.shulker", P,
      "        DyeColor dc = DyeColor.byName(name, DyeColor.WHITE);\n        return new Color(dc.getTooltipColor(), false);",
      "        return new Color(0xA59586, false); // 1.21.11: DyeColor.getTooltipColor removed")

# 20. HoleSnap forward set
patch("20.holesnap", find_file("HoleSnap.java"),
      "thunder.hack.utility.player.InputUtility.forward() = 1;",
      "thunder.hack.utility.player.InputUtility.setForward(true);")

# 21. GuiMove ClickSlot record getter + garbage line + import
P = find_file("GuiMove.java")
patch("21a.guimove.action", P,
      "((IClickSlotC2SPacket) click).th$actionType() != SlotActionType.PICKUP && ((IClickSlotC2SPacket) click).th$actionType() != SlotActionType.PICKUP_ALL",
      "click.actionType() != SlotActionType.PICKUP && click.actionType() != SlotActionType.PICKUP_ALL")
patch("21b.guimove.garbage", P,
      r"[ ]*\(thunder\.hack\.utility\.player\.InputUtility\.forward\(\) > 0\) = false;\n", "", regex=True)
patch("21c.guimove.import", P,
      "import thunder.hack.injection.accesors.IClickSlotC2SPacket;\n", "")
acc = os.path.join(ROOT, "injection/accesors/IClickSlotC2SPacket.java")
if os.path.exists(acc):
    os.remove(acc); applied.append("21d.delete-accessor")
# mixins config reference?
CFG = "/home/user/NHACKRECODE/src/main/resources"
for dp, _, fn in os.walk(CFG):
    for f in fn:
        if f.endswith(".json"):
            fp = os.path.join(dp, f); s = open(fp, encoding="utf-8").read()
            if "IClickSlotC2SPacket" in s:
                print("!! mixins config references IClickSlotC2SPacket:", fp)

# 22. Velocity: cast final record through Object (mixin iface injection)
P = find_file("Velocity.java")
patch("22.velocity.cast", P, "(IExplosionS2CPacket) explosion", "(IExplosionS2CPacket) (Object) explosion", count=None)

# 23. AutoCrystal explosion record getters
patch("23.autocrystal", find_file("AutoCrystal.java"),
      "crystal.squaredDistanceTo(explosion.getX(), explosion.getY(), explosion.getZ())",
      "crystal.squaredDistanceTo(explosion.x(), explosion.y(), explosion.z())")

# 24. AutoAnchor BlockHitResult pos
patch("24.autoanchor", find_file("AutoAnchor.java"), r"bestAnchor\.get([XYZ])\(\)", r"bestAnchor.getBlockPos().get\1()", regex=True, count=3)

# 25. FakePlayer mangled line
patch("25.fakeplayer", find_file("FakePlayer.java"),
      r"[ ]*fakePlayer\.setPosition\(p\.x, p\.y, p\.z, p\.yaw, p\.pitch\);.*\n",
      "                fakePlayer.setPosition(new net.minecraft.util.math.Vec3d(p.x, p.y, p.z));\n", regex=True)

# 26. KillEffect addParticle overload
patch("26.killeffect", find_file("KillEffect.java"),
      "mc.world.addParticle(ParticleTypes.FALLING_LAVA, entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0);",
      "mc.world.addParticle(entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0, ParticleTypes.FALLING_LAVA);")

# 27. Matrix3x2fStack translate 3-arg -> 2-arg (receiver must be Matrix3x2fStack in same file)
tn = 0
for dp, _, fn in os.walk(ROOT):
    for f in fn:
        if not f.endswith(".java"): continue
        fp = os.path.join(dp, f); s = open(fp, encoding="utf-8").read()
        if "Matrix3x2fStack" not in s: continue
        recv = set(re.findall(r"Matrix3x2fStack\s+(\w+)", s))
        if not recv: continue
        def fix(m):
            var = m.group(1)
            if var not in recv: return m.group(0)
            return f"{var}.translate({m.group(2)});"
        s2 = re.sub(r"\b(\w+)\.translate\(([^;{]*?), 0(?:\.0+)?F\);", fix, s)
        if s2 != s:
            open(fp, "w", encoding="utf-8").write(s2); tn += 1
print(f"27.translate-scan: {tn} files")

# 28. Avoid EventCollision getters
P = find_file("Avoid.java")
patch("28a.avoid.chunk", P, "Math.floor(e.getX())", "Math.floor(e.getPos().getX())")
patch("28b.avoid.chunkz", P, "Math.floor(e.getZ())", "Math.floor(e.getPos().getZ())")
patch("28c.avoid.void", P, "e.getY() < mc.world.getBottomY()", "e.getPos().getY() < mc.world.getBottomY()")

print("APPLIED:")
for a in applied: print("  +", a)
if missed:
    print("MISSED:")
    for m in missed: print("  -", m)
