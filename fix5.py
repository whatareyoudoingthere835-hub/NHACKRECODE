#!/usr/bin/env python3
import re
def rd(p):
    with open(p, encoding="utf-8") as f: return f.read()
def wr(p, s):
    with open(p, "w", encoding="utf-8") as f: f.write(s)
def sub(path, pat, rep, literal=False):
    t = rd(path); o = t
    t = t.replace(pat, rep) if literal else re.sub(pat, rep, t)
    print(("MISS: " if t == o else "ok:   ") + path.split("/")[-1] + " | " + pat[:60])
    if t != o: wr(path, t)

H = "src/main/java/thunder/hack/"

SWAP = """
    private static int th$swap(java.awt.image.BufferedImage bi, int x, int y) {
        int p = bi.getRGB(x, y);
        return (p & 0xFF000000) | ((p & 0xFF) << 16) | (p & 0x00FF00) | ((p >> 16) & 0xFF);
    }

    private static java.awt.image.BufferedImage th$toBufferedImage(net.minecraft.client.texture.NativeImage image) {
        try {
            return javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(image.toByteArray()));
        } catch (Exception e) {
            return null;
        }
    }
"""

# ---- OptifineCapes ----
f = H + "utility/OptifineCapes.java"
t = rd(f)
t = t.replace("String uuid = player.getId().toString();", "String uuid = player.id().toString();")
t = t.replace('getCapeFromURL(String.format("http://s.optifine.net/capes/%s.png", player.getName()));',
              'getCapeFromURL(String.format("http://s.optifine.net/capes/%s.png", player.name()));')
t = t.replace('Identifier capeTexture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("th-cape-" + uuid, nIBT);',
              'Identifier th$id = Identifier.of("thunderhack", "th-cape-" + uuid);\n            MinecraftClient.getInstance().getTextureManager().registerTexture(th$id, nIBT);\n            Identifier capeTexture = th$id;')
t = t.replace("return new NativeImageBackedTexture(parseCape(cape));", 'return new NativeImageBackedTexture(() -> "thunderhack:optifine-cape", parseCape(cape));')
t = t.replace("""        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;""",
"""        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        java.awt.image.BufferedImage bi = th$toBufferedImage(image);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.writePixel(x, y, bi == null ? 0 : th$swap(bi, x, y));
            }
        }
        image.close();
        return imgNew;""")
if "th$toBufferedImage" in t and "private static java.awt.image.BufferedImage th$toBufferedImage" not in t:
    t = t.rstrip()[:-1].rstrip() + "\n" + SWAP + "\n}\n"
wr(f, t); print("optifine done")

# ---- CreditsScreen ----
f = H + "gui/mainmenu/CreditsScreen.java"
t = rd(f)
t = t.replace("return new NativeImageBackedTexture(parseAvatar(pic));", 'return new NativeImageBackedTexture(() -> "thunderhack:contributor-avatar", parseAvatar(pic));')
t = t.replace("""        NativeImage imgNew = new NativeImage(96, 96, true);
        for (int x = 0; x < 96; x++) {
            for (int y = 0; y < 96; y++) {
                if (Math.hypot(x - 48, y - 48) > 45)
                    imgNew.setColor(x, y, Render2DEngine.injectAlpha(new Color(image.getColor(x, y)), (int) ((float) (48 - Math.hypot(x - 48, y - 48)) / 3f * 255f)).getRGB());
                else imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;""",
"""        NativeImage imgNew = new NativeImage(96, 96, true);
        java.awt.image.BufferedImage bi = th$toBufferedImage(image);
        for (int x = 0; x < 96; x++) {
            for (int y = 0; y < 96; y++) {
                int src = bi == null ? 0 : th$swap(bi, x, y);
                if (Math.hypot(x - 48, y - 48) > 45)
                    imgNew.writePixel(x, y, Render2DEngine.injectAlpha(new Color(src), (int) ((float) (48 - Math.hypot(x - 48, y - 48)) / 3f * 255f)).getRGB());
                else imgNew.writePixel(x, y, src);
            }
        }
        image.close();
        return imgNew;""")
if "th$toBufferedImage" in t and "private static java.awt.image.BufferedImage th$toBufferedImage" not in t:
    t = t.rstrip()[:-1].rstrip() + "\n" + SWAP + "\n}\n"
# Identifier import
if "import net.minecraft.util.Identifier;" not in t:
    t = t.replace("import net.minecraft.client.MinecraftClient;", "import net.minecraft.client.MinecraftClient;\nimport net.minecraft.util.Identifier;", 1)
wr(f, t); print("credits done")

# ---- FriendComponent ----
f = H + "gui/thundergui/components/FriendComponent.java"
t = rd(f)
t = t.replace("NativeImageBackedTexture nIBT = new NativeImageBackedTexture(parseHead(Head));",
              'NativeImageBackedTexture nIBT = new NativeImageBackedTexture(() -> "thunderhack:friend-head", parseHead(Head));')
t = t.replace("""        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;""",
"""        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        java.awt.image.BufferedImage bi = th$toBufferedImage(image);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.writePixel(x, y, bi == null ? 0 : th$swap(bi, x, y));
            }
        }
        image.close();
        return imgNew;""")
if "th$toBufferedImage" in t and "private static java.awt.image.BufferedImage th$toBufferedImage" not in t:
    t = t.rstrip()[:-1].rstrip() + "\n" + SWAP + "\n}\n"
wr(f, t); print("friend done")

# ---- LogoutSpots skin ----
sub(H + "features/modules/render/LogoutSpots.java",
    r"\(\(OtherClientPlayerEntity\) data\)\.getSkinTextures\(\)\.body\(\)\.id\(\)",
    "thunder.hack.utility.SkinUtility.skin(data)")

# ---- AntiCrash record access ----
sub(H + "features/modules/client/AntiCrash.java",
    r"instanceof PlayerPositionLookS2CPacket pos && \(pos\.getX\(\) > 1E9 \|\| pos\.getY\(\) > 1E9 \|\| pos\.getZ\(\) > 1E9 \|\| pos\.getYaw\(\) > 1E9 \|\| pos\.getPitch\(\) > 1E9\)",
    "instanceof PlayerPositionLookS2CPacket pos && (pos.change().position().x > 1E9 || pos.change().position().y > 1E9 || pos.change().position().z > 1E9 || pos.change().yaw() > 1E9 || pos.change().pitch() > 1E9)")

# ---- Aura UseAction ----
sub(H + "features/modules/combat/Aura.java",
    r"import static net\.minecraft\.util\.UseAction\.BLOCK;",
    "import static net.minecraft.item.consume.UseAction.BLOCK;")
sub(H + "features/modules/combat/Aura.java",
    r"mc\.player\.getActiveItem\(\)\.getItem\(\)\.getUseAction\(mc\.player\.getActiveItem\(\)\)",
    "mc.player.getActiveItem().getUseAction()")

# ---- AutoBed crafting block no-op ----
sub(H + "features/modules/combat/AutoBed.java",
    r"""                        mc\.player\.getRecipeBook\(\)\.setGuiOpen\(craft\.getCategory\(\), true\);
                        for \(RecipeResultCollection results : mc\.player\.getRecipeBook\(\)\.getOrderedResults\(\)\) \{
                            for \(var recipe : results\.getRecipes\(true\)\) \{
                                if \(recipe\.recipe\(\)\.getResult\(results\.getRegistryManager\(\)\)\.getItem\(\) instanceof BedItem\) \{
                                    for \(int i = 0; i < bedsPerCraft\.getValue\(\); i\+\+\)
                                        mc\.interactionManager\.clickRecipe\(mc\.player\.currentScreenHandler\.syncId, recipe, false\);
                                    mc\.interactionManager\.clickSlot\(mc\.player\.currentScreenHandler\.syncId, 0, 0, SlotActionType\.QUICK_MOVE, mc\.player\);
                                    break;
                                \}
                            \}
                        \}
""",
"""                        // 1.21.11: RecipeResultCollection crafting API changed; bed auto-craft disabled.
                        // Craft a bed manually and keep it in inventory.
""")

# ---- AutoTrader offers ----
sub(H + "features/modules/misc/AutoTrader.java", r"msh\.getRecipes\(\)", "msh.getOffers()")

# ---- MixinOtherClientPlayerEntity serverX/Y/Z ----
sub(H + "injection/MixinOtherClientPlayerEntity.java",
    r"Vec3d to = new Vec3d\(serverX, serverY, serverZ\);",
    "Vec3d to = new Vec3d(getX(), getY(), getZ());")
print("fix5 done")
