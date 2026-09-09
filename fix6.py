#!/usr/bin/env python3
import re
def rd(p):
    with open(p, encoding="utf-8") as f: return f.read()
def wr(p, s):
    with open(p, "w", encoding="utf-8") as f: f.write(s)
def sub(path, pat, rep):
    t = rd(path); o = t
    t = re.sub(pat, rep, t)
    print(("MISS " if t == o else "ok   ") + path.split("/")[-1] + " :: " + pat[:56])
    if t != o: wr(path, t)
def add_import(path, imp):
    t = rd(path)
    if imp in t: return
    m = re.search(r"package [\w.]+;\n", t)
    t = t[:m.end()] + "\n" + imp + "\n" + t[m.end():]
    wr(path, t)
    print("import added:", path)

H = "src/main/java/thunder/hack/"

# event getPos chains
for f in ["features/modules/combat/AutoCrystal.java", "features/modules/combat/AutoAnchor.java"]:
    sub(H + f, r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.toCenterPos\(\)", "e.getPos().toCenterPos()")
    sub(H + f, r'new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\) \+', 'e.getPos().toCenterPos() +')
sub(H + "features/modules/misc/Nuker.java",
    r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\) == blockData\.bp",
    "e.getPos().equals(blockData.bp)")
sub(H + "features/modules/movement/Phase.java",
    r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.equals\(", "e.getPos().equals(")
sub(H + "features/modules/movement/Phase.java",
    r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.getX\(\)", "e.getPos().getX()")
sub(H + "features/modules/movement/Phase.java",
    r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.getZ\(\)", "e.getPos().getZ()")

# Nuker other e.getX leftovers
sub(H + "features/modules/misc/Nuker.java", r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.getY\(\)", "e.getPos().getY()")
sub(H + "features/modules/misc/Nuker.java", r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.getZ\(\)", "e.getPos().getZ()")
sub(H + "features/modules/misc/Nuker.java", r"new Vec3d\(e\.getX\(\), e\.getY\(\), e\.getZ\(\)\)\.getX\(\)", "e.getPos().getX()")

# MathHelper imports
for f in ["features/hud/impl/RadarRewrite.java", "features/hud/impl/CrosshairArrows.java", "gui/clickui/ModuleButton.java"]:
    add_import(H + f, "import net.minecraft.util.math.MathHelper;")

# 2D helper signatures -> Matrix3x2fStack
sub(H + "features/hud/impl/Particles.java",
    r"public void render2D\(PoseStack matrixStack\) \{", "public void render2D(org.joml.Matrix3x2fStack matrixStack) {")
sub(H + "features/hud/impl/Particles.java",
    r"public void drawStar\(PoseStack matrices", "public void drawStar(org.joml.Matrix3x2fStack matrices")
sub(H + "gui/clickui/impl/ColorPickerElement.java",
    r"private void renderPicker\(PoseStack matrixStack", "private void renderPicker(org.joml.Matrix3x2fStack matrixStack")
sub(H + "gui/thundergui/components/ColorPickerComponent.java",
    r"private void renderPicker\(PoseStack stack", "private void renderPicker(org.joml.Matrix3x2fStack stack")
sub(H + "gui/notification/Notification.java",
    r"public void renderShaders\(PoseStack matrix", "public void renderShaders(org.joml.Matrix3x2fStack matrix")

# TargetHud sizeAnimation rewrite
sub(H + "features/hud/impl/TargetHud.java",
    r"""    public static void sizeAnimation\(PoseStack matrixStack, double width, double height, double animation\) \{
        matrixStack\.translate\(width, height, 0\);
        matrixStack\.scale\(\(float\) animation, \(float\) animation, 1\);
        matrixStack\.translate\(-width, -height, 0\);
    \}""",
    """    public static void sizeAnimation(org.joml.Matrix3x2fStack matrixStack, double width, double height, double animation) {
        matrixStack.translate((float) width, (float) height);
        matrixStack.scale((float) animation, (float) animation);
        matrixStack.translate((float) -width, (float) -height);
    }""")

print("fix6 done")
