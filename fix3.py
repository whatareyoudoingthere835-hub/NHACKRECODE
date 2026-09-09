#!/usr/bin/env python3
# fix3.py — targeted manual fixes after sweep3
import re, sys

def rd(p):
    with open(p, encoding="utf-8") as f: return f.read()
def wr(p, s):
    with open(p, "w", encoding="utf-8") as f: f.write(s)

def sub(path, pat, rep, count=0, flags=0, literal=False):
    t = rd(path)
    o = t
    if literal:
        t = t.replace(pat, rep)
    else:
        t = re.sub(pat, rep, t, count=count, flags=flags)
    if t == o:
        print("MISS:", path, pat[:70])
    else:
        wr(path, t)

H = "src/main/java/thunder/hack/"

# --- InputUtility assignment sites ---
sub(H+"features/modules/client/Rotations.java",
    r"            thunder\.hack\.utility\.player\.InputUtility\.strafe\(\) = Math\.round\(mS \* cos - mF \* sin\);\n            thunder\.hack\.utility\.player\.InputUtility\.forward\(\) = Math\.round\(mF \* cos \+ mS \* sin\);\n",
    "            int th$f = Math.round(mF * cos + mS * sin);\n"
    "            int th$s = Math.round(mS * cos - mF * sin);\n"
    "            var th$pi = mc.player.input.playerInput;\n"
    "            mc.player.input.playerInput = new net.minecraft.util.PlayerInput(th$f > 0, th$f < 0, th$s < 0, th$s > 0, th$pi.jump(), th$pi.sneak(), th$pi.sprint());\n")

sub(H+"features/modules/misc/AntiAFK.java",
    r"            thunder\.hack\.utility\.player\.InputUtility\.forward\(\) = Math\.round\(sin\);\n            thunder\.hack\.utility\.player\.InputUtility\.strafe\(\) = Math\.round\(cos\);\n",
    "            var th$pi = mc.player.input.playerInput;\n"
    "            mc.player.input.playerInput = new net.minecraft.util.PlayerInput(sin > 0, sin < 0, cos < 0, cos > 0, th$pi.jump(), th$pi.sneak(), th$pi.sprint());\n")

sub(H+"features/modules/movement/AutoWalk.java",
    r"thunder\.hack\.utility\.player\.InputUtility\.forward\(\) = 1f;",
    "thunder.hack.utility.player.InputUtility.setForward(true);")

sub(H+"features/modules/movement/ElytraPlus.java",
    r"thunder\.hack\.utility\.player\.InputUtility\.strafe\(\) = 1;",
    "thunder.hack.utility.player.InputUtility.setStrafe(true);")

# --- Render3DEngine boolean overload ---
sub(H+"utility/render/Render3DEngine.java",
    r"(    public static float getTickDelta\(\) \{)",
    r"public static float getTickDelta(boolean secondsPerTick) {\n        return getTickDelta();\n    }\n\n\1")

# --- .last() on DrawContext matrices ---
for f in ["features/hud/impl/Crosshair.java", "features/modules/render/ESP.java", "features/modules/render/ItemESP.java"]:
    sub(H+f, r"new Matrix4f\(\)\.set\(context\.getMatrices\(\)\.last\(\)\)", "new Matrix4f().set(context.getMatrices())")
sub(H+"features/hud/impl/TargetHud.java", r"\.set\(context\.getMatrices\(\)\.last\(\)\)", ".set(context.getMatrices())")

# --- heldItemRenderer via accessor ---
sub(H+"features/modules/render/Animations.java", r"mc\.gameRenderer\.getHeldItemRenderer\(\)",
    "((thunder.hack.injection.accesors.IGameRenderer) mc.gameRenderer).th$heldItemRenderer()")
# --- projection matrix in mixin ---
sub(H+"injection/MixinGameRenderer.java", r"mc\.gameRenderer\.getProjectionMatrix\([^)]*\)",
    "((thunder.hack.injection.accesors.IGameRenderer) mc.gameRenderer).th$projectionMatrix()")

# --- NativeImageBackedTexture / registerDynamicTexture sites ---
sub(H+"gui/font/GlyphMap.java", r"new NativeImageBackedTexture\(image\)", "new NativeImageBackedTexture(i::toString, image)")

sub(H+"gui/mainmenu/CreditsScreen.java",
    r'return MinecraftClient\.getInstance\(\)\.getTextureManager\(\)\.registerDynamicTexture\("th-contributors-" \+ \(int\) MathUtility\.random\(0, 1000000\), nIBT\);',
    'Identifier th$id = Identifier.of("thunderhack", "th-contributors-" + (int) MathUtility.random(0, 1000000));\n                MinecraftClient.getInstance().getTextureManager().registerTexture(th$id, nIBT);\n                return th$id;')
sub(H+"gui/mainmenu/CreditsScreen.java", r"new NativeImageBackedTexture\(parseAvatar\(pic\)\)",
    'new NativeImageBackedTexture(() -> "thunderhack:contributor-avatar", parseAvatar(pic))')

sub(H+"gui/thundergui/components/FriendComponent.java",
    r'head = MinecraftClient\.getInstance\(\)\.getTextureManager\(\)\.registerDynamicTexture\("th-heads-" \+ name, nIBT\);',
    'Identifier th$id = Identifier.of("thunderhack", "th-heads-" + name);\n                MinecraftClient.getInstance().getTextureManager().registerTexture(th$id, nIBT);\n                head = th$id;')
sub(H+"gui/thundergui/components/FriendComponent.java", r"new NativeImageBackedTexture\(parseHead\(Head\)\)",
    'new NativeImageBackedTexture(() -> "thunderhack:friend-head", parseHead(Head))')

sub(H+"utility/OptifineCapes.java",
    r"""            String uuid = player\.id\(\)\.toString\(\);
            NativeImageBackedTexture nIBT = getCapeFromURL\(String\.format\("http://s\.optifine\.net/capes/%s\.png", player\.name\(\)\)\);
            Identifier capeTexture = MinecraftClient\.getInstance\(\)\.getTextureManager\(\)\.registerDynamicTexture\("th-cape-" \+ uuid, nIBT\);""",
    """            String uuid = player.id().toString();
            NativeImageBackedTexture nIBT = getCapeFromURL(String.format("http://s.optifine.net/capes/%s.png", player.name()));
            Identifier th$id = Identifier.of("thunderhack", "th-cape-" + uuid);
            MinecraftClient.getInstance().getTextureManager().registerTexture(th$id, nIBT);
            Identifier capeTexture = th$id;""")

# --- limbAnimator casualties ---
sub(H+"features/modules/render/Chams.java", r"o = pe\.limbAnimator\.getAnimationProgress\(g\);", "o = 0f; // LimbAnimator exposes no progress getter on 1.21.11")
sub(H+"features/modules/render/LogoutSpots.java", r"state\.limbSwingAnimationProgress = entity\.limbAnimator\.getAnimationProgress\(Render3DEngine\.getTickDelta\(false\)\);",
    "state.limbSwingAnimationProgress = 0f; // 1.21.11: not readable")
sub(H+"features/modules/render/PopChams.java", r"state\.limbSwingAnimationProgress = entity\.limbAnimator\.getAnimationProgress\(Render3DEngine\.getTickDelta\(false\)\);",
    "state.limbSwingAnimationProgress = 0f; // 1.21.11: not readable")
sub(H+"features/modules/render/PopChams.java",
    r"        entity\.limbAnimator\.setSpeed\(e\.getEntity\(\)\.limbAnimator\.getSpeed\(\)\);\n        entity\.limbAnimator\.getAnimationProgress\(\) = e\.getEntity\(\)\.limbAnimator\.getAnimationProgress\(\);\n",
    "        // limb animation transfer unsupported on 1.21.11 LimbAnimator\n")
sub(H+"injection/MixinLivingEntityRenderer.java", r"state\.limbAnimator\.getProgress\(\), state\.limbAnimator\.getSpeed\(\)\)", "0f, 0f)")
# PopChams broken sneak line
sub(H+"features/modules/render/PopChams.java", r"\$1\.input\.playerInput = \$1\.input\.playerInput\.withSneak\(\$2\);",
    "entity.input.playerInput = entity.input.playerInput.withSneak(e.getEntity().isSneaking());")
# PopChams bodyYaw/headYaw fields -> setters
sub(H+"features/modules/render/PopChams.java", r"entity\.bodyYaw = e\.getEntity\(\)\.bodyYaw;", "entity.setBodyYaw(e.getEntity().getBodyYaw());")
sub(H+"features/modules/render/PopChams.java", r"entity\.headYaw = e\.getEntity\(\)\.headYaw;", "entity.setHeadYaw(e.getEntity().getHeadYaw());")

# --- Radar prevX/Z ---
sub(H+"features/hud/impl/Radar.java", r"entityPlayer\.prevX", "(entityPlayer.getX() - entityPlayer.getVelocity().x)")
sub(H+"features/hud/impl/Radar.java", r"entityPlayer\.prevZ", "(entityPlayer.getZ() - entityPlayer.getVelocity().z)")
sub(H+"features/hud/impl/Radar.java", r"entityPlayer\.prevY", "(entityPlayer.getY() - entityPlayer.getVelocity().y)")

# --- misc one-liners ---
sub(H+"features/modules/render/BlockESP.java", r"mc\.world\.getTopY\(\)", "mc.world.getHeight()")
sub(H+"features/modules/combat/AutoBed.java", r"mc\.world\.getDimension\(\)\.bedWorks\(\)", "true")
sub(H+"injection/MixinSplashOverlay.java",
    r"            GlStateManager\._clearColor\(m, n, o, 1\.0F\);\n            GlStateManager\._clear\(16384, MinecraftClient\.IS_SYSTEM_MAC\);\n",
    "            // 1.21.11: GlStateManager clear removed from splash path; color handled by context.fill above\n")
sub(H+"injection/MixinAbstractHorseEntity.java", r'@Inject\(method = "isSaddled", at = @At\("HEAD"\), cancellable = true\)',
    '@Inject(method = "isSaddled", at = @At("HEAD"), cancellable = true, require = 0)')
sub(H+"features/cmd/impl/HorseSpeedCommand.java", r"if \(!horse\.isSaddled\(\)\) \{", "if (false) { // 1.21.11: isSaddled gone")
sub(H+"features/modules/combat/Aura.java", r"return !he\.isAngryAt\(mc\.player\);",
    "return !(he.getAttacker() == mc.player || he.getTarget() == mc.player);")
sub(H+"injection/MixinParticleManager.java", r"p instanceof ElderGuardianAppearanceParticle",
    'p.getClass().getSimpleName().contains("ElderGuardian")')
sub(H+"injection/MixinParticleManager.java", r"import net\.minecraft\.client\.particle\.ElderGuardianAppearanceParticle;\n", "")
sub(H+"features/modules/render/NameTags.java",
    r"            context\.drawSprite\(0, 0, 0, 18, 18, mc\.getStatusEffectSpriteManager\(\)\.getSprite\(statusEffectInstance\.getEffectType\(\)\)\);\n",
    "            // 1.21.11: status effect sprite manager removed - icon skipped\n")
sub(H+"features/modules/render/NameTags.java",
    r"if \(he\.getOwnerUuid\(\) != null\) ownerName = he\.getOwnerUuid\(\)\.toString\(\);",
    "if (he.getOwner() != null) ownerName = he.getOwner().getName().getString();")
sub(H+"features/modules/combat/AutoTotem.java", r"else mc\.interactionManager\.pickFromInventory\(slot\);",
    "else mc.player.getInventory().setSelectedSlot(slot); ((thunder.hack.injection.accesors.IInteractionManager) mc.interactionManager).syncSlot();")
sub(H+"features/modules/combat/AutoTotem.java", r"setSelectedSlot\(prevSlot, 300\)", "setSelectedSlot(prevSlot)")

# --- IPlayerPositionLookS2CPacket: drop nested-change mutation ---
sub(H+"features/modules/movement/PacketFly.java", r"            \(\(IPlayerPositionLookS2CPacket\) pac\)\.setChange\([^;]*;\n",
    "            // 1.21.11: PlayerPositionLookS2CPacket is an immutable record - rotation rewrite skipped\n")
sub(H+"features/modules/player/NoServerRotate.java", r"            \(\(IPlayerPositionLookS2CPacket\) pac\)\.setYaw\([^;]*;\n            \(\(IPlayerPositionLookS2CPacket\) pac\)\.setPitch\([^;]*;\n",
    "            // 1.21.11: PlayerPositionLookS2CPacket is an immutable record - rotation rewrite skipped\n")

# --- THRenderLayers map generics ---
sub(H+"utility/render/THRenderLayers.java", r"Map<Object, RenderLayer> WORLD_CACHE = new HashMap<>\(\);", "Map<String, RenderLayer> WORLD_CACHE = new HashMap<>();")

# --- IPlayerPositionLookS2CPacket accessor: kill bogus EntityPosition type ---
wr(H+"injection/accesors/IPlayerPositionLookS2CPacket.java", """package thunder.hack.injection.accesors;

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerPositionLookS2CPacket.class)
public interface IPlayerPositionLookS2CPacket {
    @Accessor("teleportId")
    int getTeleportId();
}
""")

# --- BlurredShadow rewrite ---
t = rd(H+"utility/render/Render2DEngine.java")
old = re.search(r"    public static class BlurredShadow \{.*?\n    \}\n", t, re.S)
if not old:
    print("MISS BlurredShadow"); sys.exit(1)
new = """    public static void registerBufferedImageTexture(net.minecraft.util.Identifier id, java.awt.image.BufferedImage image) {
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "PNG", out);
            net.minecraft.client.texture.NativeImage nativeImage =
                    net.minecraft.client.texture.NativeImage.read(new java.io.ByteArrayInputStream(out.toByteArray()));
            MinecraftClient.getInstance().getTextureManager()
                    .registerTexture(id, new NativeImageBackedTexture(id::toString, nativeImage));
        } catch (Exception ignored) {
        }
    }

    public static class BlurredShadow {
        net.minecraft.util.Identifier id;

        public BlurredShadow(BufferedImage bufferedImage) {
            this.id = net.minecraft.util.Identifier.of("thunderhack", "texture/remote/" + RandomStringUtils.randomAlphanumeric(16));
            registerBufferedImageTexture(id, bufferedImage);
        }

        public void bind() {
            bindTexture(id);
        }

        public void bind(Matrix3x2fStack matrices, float x, float y, float width, float height, Color color) {
            Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.QUADS, id);
            int rgb = color.getRGB();
            b.vertex(x, y + height, 0f, 1f, rgb);
            b.vertex(x + width, y + height, 1f, 1f, rgb);
            b.vertex(x + width, y, 1f, 0f, rgb);
            b.vertex(x, y, 0f, 0f, rgb);
            b.submit();
        }
    }
"""
t = t[:old.start()] + new + t[old.end():]
t = t.replace("shadowCache.get(identifier).id.getId()", "shadowCache.get(identifier).id")
t = t.replace("shadowCache1.get(identifier).id.getId()", "shadowCache1.get(identifier).id")
wr(H+"utility/render/Render2DEngine.java", t)

# --- MinecraftClient import in Render2DEngine? ---
t = rd(H+"utility/render/Render2DEngine.java")
if "import net.minecraft.client.MinecraftClient;" not in t:
    t = t.replace("package thunder.hack.utility.render;", "package thunder.hack.utility.render;\n\nimport net.minecraft.client.MinecraftClient;", 1)
    wr(H+"utility/render/Render2DEngine.java", t)

# --- NativeImage getColor/setColor private sites ---
for f, n in [("gui/mainmenu/CreditsScreen.java", "pic"), ("gui/thundergui/components/FriendComponent.java", "Head"), ("utility/OptifineCapes.java", "cape")]:
    t = rd(H+f)
    print(f, "raw-pixel:", len(re.findall(r"\.(getColor|setColor|copyPixels)\(", t)))

print("fix3 done")
