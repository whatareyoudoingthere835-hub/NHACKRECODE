#!/usr/bin/env python3
# fix9: undo tr2 damage (2-arg Matrix3x2fStack.translate got ', 0.0' + lost ';') and restore HitParticles as true 3D (MatrixStack) module.
import re, os

ROOT = '/home/user/NHACKRECODE/src/main/java/thunder/hack'
n2d = 0
for d, _, fs in os.walk(ROOT):
    for f in fs:
        if not f.endswith('.java'): continue
        p = os.path.join(d, f)
        if 'HitParticles' in f: continue
        s = open(p).read()
        lines = s.split('\n'); out = []; ch = False
        for i, ln in enumerate(lines):
            m = re.match(r'^(\s*\w+\.translate\(.*)\)$', ln)
            if m and ln.rstrip().endswith(', 0.0)') and 'matrixStack' in ln:
                out.append(m.group(1).rstrip()[:-len(', 0.0')] + ');')
                ch = True; n2d += 1
            else:
                out.append(ln)
        if ch:
            open(p, 'w').write('\n'.join(out))
            print(f'2D translate restored: {os.path.relpath(p, ROOT)}')
print(f'total 2D lines fixed: {n2d}')

# ---- HitParticles: restore 3D render ----
p = os.path.join(ROOT, 'features/modules/render/HitParticles.java')
s = open(p).read()
new_render = '''        public void render(MatrixStack matrixStack) {
            float size = starsScale.getValue();
            float scale = mode.is(Mode.Text) ? 0.025f * size : 0.07f;

            final double posX = Render2DEngine.interpolate(px, x, Render3DEngine.getTickDelta(false)) - net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos().x;
            final double posY = Render2DEngine.interpolate(py, y, Render3DEngine.getTickDelta(false)) + 0.1 - net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos().y;
            final double posZ = Render2DEngine.interpolate(pz, z, Render3DEngine.getTickDelta(false)) - net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos().z;

            matrixStack.push();
            matrixStack.translate(posX, posY, posZ);

            matrixStack.scale(scale, scale, scale);

            matrixStack.translate(size / 2, size / 2, size / 2);
            matrixStack.multiplyPositionMatrix(RotationAxis.POSITIVE_Y.rotationDegrees(-net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getYaw()));
            matrixStack.multiplyPositionMatrix(RotationAxis.POSITIVE_X.rotationDegrees(net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getPitch()));

            if (mode.is(Mode.Text))
                matrixStack.multiplyPositionMatrix(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            else
                matrixStack.multiplyPositionMatrix(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAngle += (float) (AnimationUtility.deltaTime() * rotationSpeed)));

            matrixStack.translate(-size / 2, -size / 2, -size / 2);

            switch (mode.getValue()) {
                case Orbiz -> {
                    drawOrbiz3D(matrixStack, 0.0f, 0.3, color);
                    drawOrbiz3D(matrixStack, -0.1f, 0.5, color);
                    drawOrbiz3D(matrixStack, -0.2f, 0.7, color);
                }
                case Stars -> drawSprite3D(matrixStack, thunder.hack.utility.render.TextureStorage.star, size, color);
                case Hearts -> drawSprite3D(matrixStack, thunder.hack.utility.render.TextureStorage.heart, size, color);
                case Bloom -> drawSprite3D(matrixStack, thunder.hack.utility.render.TextureStorage.firefly, size, color);
                case Text -> Render3DEngine.drawTextIn3D(MathUtility.round2(health) + " ", new net.minecraft.util.math.Vec3d(Render2DEngine.interpolate(px, x, Render3DEngine.getTickDelta(false)), Render2DEngine.interpolate(py, y, Render3DEngine.getTickDelta(false)) + 0.1, Render2DEngine.interpolate(pz, z, Render3DEngine.getTickDelta(false))), 0, 0, 0, (health > 0 ? colorH.getValue() : colorD.getValue()).getColorObject());
            }

            matrixStack.scale(0.8f, 0.8f, 0.8f);
            matrixStack.pop();
        }
'''
pat = re.compile(r'        public void render\(Matrix3x2fStack matrixStack\) \{\n(?:.*?\n)*?            matrixStack\.popMatrix\(\);\n        \}\n')
if pat.search(s):
    s = pat.sub(new_render, s, count=1)
    print('HitParticles render() restored to 3D')
else:
    print('!! render() pattern miss — check manually')

helpers = '''    private static void drawOrbiz3D(MatrixStack matrices, float z, final double r, Color c) {
        org.joml.Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        net.minecraft.client.render.BufferBuilder bufferBuilder = net.minecraft.client.render.Tessellator.getInstance().begin(com.mojang.blaze3d.vertex.VertexFormat.DrawMode.TRIANGLE_FAN, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 20; i++) {
            final float x2 = (float) (Math.sin(((i * 56.548656f) / 180f)) * r);
            final float y2 = (float) (Math.cos(((i * 56.548656f) / 180f)) * r);
            bufferBuilder.vertex(matrix, x2, y2, z).color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 0.4f);
        }
        Render2DEngine.endBuilding(bufferBuilder);
    }

    private static void drawSprite3D(MatrixStack matrices, net.minecraft.util.Identifier texture, float scale, Color c) {
        org.joml.Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, texture);
        net.minecraft.client.render.BufferBuilder bufferBuilder = net.minecraft.client.render.Tessellator.getInstance().begin(com.mojang.blaze3d.vertex.VertexFormat.DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix, 0f, 0f, 0f).texture(0f, 0f).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        bufferBuilder.vertex(matrix, 0f, scale, 0f).texture(0f, 1f).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        bufferBuilder.vertex(matrix, scale, scale, 0f).texture(1f, 1f).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        bufferBuilder.vertex(matrix, scale, 0f, 0f).texture(1f, 0f).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        Render2DEngine.endBuilding(bufferBuilder);
    }

    public enum Physics {'''
if 'private static void drawOrbiz3D' not in s:
    s = s.replace('    public enum Physics {', helpers, 1)
    print('HitParticles 3D draw helpers inserted')
open(p, 'w').write(s)

# ---- verify no leftover broken translate lines ----
import subprocess
out = subprocess.run(['grep', '-rn', '0\\.0)$', ROOT, '--include=*.java'], capture_output=True, text=True).stdout
left = [l for l in out.splitlines() if 'translate' in l]
print('remaining suspicious translate lines:', len(left))
for l in left[:10]: print('  ', l.split('/thunder/hack/')[-1])
