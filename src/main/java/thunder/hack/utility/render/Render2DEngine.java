package thunder.hack.utility.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.apache.commons.lang3.RandomStringUtils;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.utility.render.Draw2D.Batch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import static thunder.hack.features.modules.Module.mc;

/**
 * 1.21.11 rebuild: all 2d primitives are recorded into {@link Batch} objects
 * (see {@link Draw2D}) instead of driving {@code RenderSystem} global state and
 * immediate {@code BufferBuilder} flushes. Scissoring ("windows") is emulated on
 * the same batch bridge so z-ordering keeps working.
 */
public class Render2DEngine {

    public static HashMap<Integer, BlurredShadow> shadowCache = new HashMap<>();
    public static HashMap<Integer, BlurredShadow> shadowCache1 = new HashMap<>();

    private static final Deque<Rectangle> clipStack = new ArrayDeque<>();
    private static Identifier currentBoundTexture = net.minecraft.client.texture.MissingSprite.getMissingSpriteId();

    public static Identifier currentBoundTexturePublic() {
        return currentBoundTexture;
    }

    public static void bindTexture(Identifier id) {
        if (id != null) currentBoundTexture = id;
    }

    /* ------------------------------------------------------------------
     * scissor windows
     * ------------------------------------------------------------------ */

    public static void addWindow(Matrix3x2fStack stack, Rectangle r1) {
        Matrix3x2f m = new Matrix3x2f(stack);
        float ax = (float) (m.m00 * r1.x() + m.m01 * r1.y() + m.m02);
        float ay = (float) (m.m10 * r1.x() + m.m11 * r1.y() + m.m12);
        float bx = (float) (m.m00 * r1.x1() + m.m01 * r1.y1() + m.m02);
        float by = (float) (m.m10 * r1.x1() + m.m11 * r1.y1() + m.m12);
        Rectangle r = new Rectangle(ax, ay, bx, by);
        if (clipStack.isEmpty()) {
            clipStack.push(r);
            beginScissor(r.x(), r.y(), r.x1(), r.y1());
        } else {
            Rectangle lastClip = clipStack.peek();
            float lsx = lastClip.x(), lsy = lastClip.y(), lstx = lastClip.x1(), lsty = lastClip.y1();
            float nsx = MathHelper.clamp(r.x(), lsx, lstx);
            float nsy = MathHelper.clamp(r.y(), lsy, lsty);
            float nstx = MathHelper.clamp(r.x1(), nsx, lstx);
            float nsty = MathHelper.clamp(r.y1(), nsy, lsty);
            Rectangle clipped = new Rectangle(nsx, nsy, nstx, nsty);
            clipStack.push(clipped);
            beginScissor(clipped.x(), clipped.y(), clipped.x1(), clipped.y1());
        }
    }

    public static void popWindow() {
        if (!clipStack.isEmpty()) clipStack.pop();
        if (clipStack.isEmpty()) {
            endScissor();
        } else {
            Rectangle r = clipStack.peek();
            beginScissor(r.x(), r.y(), r.x1(), r.y1());
        }
    }

    public static void beginScissor(double x, double y, double endX, double endY) {
        Draw2D.pushScissor((float) x, (float) y, (float) endX, (float) endY);
    }

    public static void endScissor() {
        Draw2D.popScissor();
    }

    public static void addWindow(Matrix3x2fStack stack, float x, float y, float x1, float y1, double animation_factor) {
        float h = y + y1;
        float h2 = (float) (h * (1d - MathHelper.clamp((float) animation_factor, 0, 1.0025f)));

        float x3 = x;
        float y3 = y + h2;
        float x4 = x1;
        float y4 = y1 - h2;

        if (x4 < x3) x4 = x3;
        if (y4 < y3) y4 = y3;

        addWindow(stack, new Rectangle(x3, y3, x4, y4));
    }

    /* ------------------------------------------------------------------
     * plain geometry
     * ------------------------------------------------------------------ */

    public static void horizontalGradient(Matrix3x2fStack matrices, float x1, float y1, float x2, float y2, Color startColor, Color endColor) {
        Batch b = Draw2D.quads(matrices);
        b.vertex(x1, y1, startColor.getRGB());
        b.vertex(x1, y2, startColor.getRGB());
        b.vertex(x2, y2, endColor.getRGB());
        b.vertex(x2, y1, endColor.getRGB());
        b.submit();
    }

    public static void verticalGradient(Matrix3x2fStack matrices, float left, float top, float right, float bottom, Color startColor, Color endColor) {
        Batch b = Draw2D.quads(matrices);
        b.vertex(left, top, startColor.getRGB());
        b.vertex(left, bottom, endColor.getRGB());
        b.vertex(right, bottom, endColor.getRGB());
        b.vertex(right, top, startColor.getRGB());
        b.submit();
    }

    public static void drawRect(Matrix3x2fStack matrices, float x, float y, float width, float height, Color c) {
        Batch b = Draw2D.quads(matrices);
        b.vertex(x, y + height, c.getRGB());
        b.vertex(x + width, y + height, c.getRGB());
        b.vertex(x + width, y, c.getRGB());
        b.vertex(x, y, c.getRGB());
        b.submit();
    }

    public static void drawRectWithOutline(Matrix3x2fStack matrices, float x, float y, float width, float height, Color c, Color c2) {
        Batch b = Draw2D.quads(matrices);
        b.vertex(x, y + height, c.getRGB());
        b.vertex(x + width, y + height, c.getRGB());
        b.vertex(x + width, y, c.getRGB());
        b.vertex(x, y, c.getRGB());
        b.submit();

        Batch o = Draw2D.of(matrices, VertexFormat.DrawMode.DEBUG_LINE_STRIP);
        o.vertex(x, y + height, c2.getRGB());
        o.vertex(x + width, y + height, c2.getRGB());
        o.vertex(x + width, y, c2.getRGB());
        o.vertex(x, y, c2.getRGB());
        o.vertex(x, y + height, c2.getRGB());
        o.submit();
    }

    public static void drawRectDumbWay(Matrix3x2fStack matrices, float x, float y, float x1, float y1, Color c1) {
        Batch b = Draw2D.quads(matrices);
        b.vertex(x, y1, c1.getRGB());
        b.vertex(x1, y1, c1.getRGB());
        b.vertex(x1, y, c1.getRGB());
        b.vertex(x, y, c1.getRGB());
        b.submit();
    }

    /** legacy world-space rect helper writing straight into a {@link BufferBuilder} */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static void setRectPoints(Object bufferBuilder, Object matrix, float x, float y, float x1, float y1, Color c1, Color c2, Color c3, Color c4) {
        if (bufferBuilder instanceof BufferBuilder bb && matrix instanceof org.joml.Matrix4f m) {
            bb.vertex(m, x, y1, 0.0F).color(c1.getRGB());
            bb.vertex(m, x1, y1, 0.0F).color(c2.getRGB());
            bb.vertex(m, x1, y, 0.0F).color(c3.getRGB());
            bb.vertex(m, x, y, 0.0F).color(c4.getRGB());
        }
    }

    public static void setRectPoints(Batch batch, float x, float y, float x1, float y1, Color c1, Color c2, Color c3, Color c4) {
        batch.vertex(x, y1, c1.getRGB());
        batch.vertex(x1, y1, c2.getRGB());
        batch.vertex(x1, y, c3.getRGB());
        batch.vertex(x, y, c4.getRGB());
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }

    /* ------------------------------------------------------------------
     * blurred shadows (pre-baked gaussian textures, tinted per draw)
     * ------------------------------------------------------------------ */

    public static void drawBlurredShadow(Matrix3x2fStack matrices, float x, float y, float width, float height, int blurRadius, Color color) {
        if (!HudEditor.glow.getValue()) return;
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        int identifier = (int) (width * height + width * blurRadius);
        if (shadowCache.containsKey(identifier)) {
            shadowCache.get(identifier).bind(matrices, x, y, width, height, color);
        } else {
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();
            BufferedImage blurred = new GaussianFilter(blurRadius).filter(original, null);
            shadowCache.put(identifier, new BlurredShadow(blurred));
        }
    }

    public static void drawGradientBlurredShadow(Matrix3x2fStack matrices, float x, float y, float width, float height, int blurRadius, Color color1, Color color2, Color color3, Color color4) {
        if (!HudEditor.glow.getValue()) return;
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        int identifier = (int) (width * height + width * blurRadius);
        if (shadowCache.containsKey(identifier)) {
            Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.QUADS, shadowCache.get(identifier).id);
            b.vertex(x, y + height, 0f, 1f, color1.getRGB());
            b.vertex(x + width, y + height, 1f, 1f, color2.getRGB());
            b.vertex(x + width, y, 1f, 0f, color3.getRGB());
            b.vertex(x, y, 0f, 0f, color4.getRGB());
            b.submit();
        } else {
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();
            BufferedImage blurred = new GaussianFilter(blurRadius).filter(original, null);
            shadowCache.put(identifier, new BlurredShadow(blurred));
        }
    }

    public static void drawGradientBlurredShadow1(Matrix3x2fStack matrices, float x, float y, float width, float height, int blurRadius, Color color1, Color color2, Color color3, Color color4) {
        if (!HudEditor.glow.getValue()) return;
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        int identifier = (int) (width * height + width * blurRadius);
        if (shadowCache1.containsKey(identifier)) {
            Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.QUADS, shadowCache1.get(identifier).id);
            b.vertex(x, y + height, 0f, 1f, color1.getRGB());
            b.vertex(x + width, y + height, 1f, 1f, color2.getRGB());
            b.vertex(x + width, y, 1f, 0f, color3.getRGB());
            b.vertex(x, y, 0f, 0f, color4.getRGB());
            b.submit();
        } else {
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();
            BufferedImage blurred = new GaussianFilter(blurRadius).filter(original, null);

            BufferedImage black = new BufferedImage((int) width + blurRadius * 2, (int) height + blurRadius * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics g2 = black.getGraphics();
            g2.setColor(new Color(0x000000));
            g2.fillRect(0, 0, (int) width + blurRadius * 2, (int) height + blurRadius * 2);
            g2.dispose();

            BufferedImage combined = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics g1 = combined.getGraphics();
            g1.drawImage(black, -blurRadius, -blurRadius, null);
            g1.drawImage(blurred, 0, 0, null);
            g1.dispose();

            shadowCache1.put(identifier, new BlurredShadow(combined));
        }
    }



    /* ------------------------------------------------------------------
     * textures
     * ------------------------------------------------------------------ */

    public static void renderTexture(Matrix3x2fStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
        renderGradientTextureInternal(matrices, x0, y0, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight,
                Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, currentBoundTexture);
    }

    public static void renderGradientTexture(Matrix3x2fStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4) {
        renderGradientTextureInternal(matrices, x0, y0, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight, c1, c2, c3, c4, currentBoundTexture);
    }

    /** legacy signature kept for stale callers; no-op */
    @Deprecated
    public static void renderGradientTextureInternal(Object buff, Matrix3x2fStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4) {
    }

    public static void renderGradientTextureInternal(Matrix3x2fStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4, @NotNull Identifier texture) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.QUADS, texture);
        b.vertex((float) x0, (float) y1, u / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight, c1.getRGB());
        b.vertex((float) x1, (float) y1, (u + (float) regionWidth) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight, c2.getRGB());
        b.vertex((float) x1, (float) y0, (u + (float) regionWidth) / (float) textureWidth, v / (float) textureHeight, c3.getRGB());
        b.vertex((float) x0, (float) y0, u / (float) textureWidth, v / (float) textureHeight, c4.getRGB());
        b.submit();
    }

    /* ------------------------------------------------------------------
     * rounded rects
     * ------------------------------------------------------------------ */

    /** x0,y0,x1,y1 corner form — used as renderRoundedQuad(matrices, color, fromX, fromY, toX, toY, radius, samples) too */
    public static void renderRoundedGradientRect(Matrix3x2fStack matrices, Color color1, Color color2, Color color3, Color color4, float x, float y, float width, float height, float Radius) {
        renderRoundedQuad2(matrices, color1, color2, color3, color4, x, y, x + width, y + height, Radius);
    }

    public static void drawRound(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, Color color) {
        renderRoundedQuad(matrices, color, x, y, width + x, height + y, radius, 4);
    }

    public static void drawRound(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, Color c1, Color c2, Color c3, Color c4) {
        renderRoundedQuad2(matrices, c1, c2, c3, c4, x, y, x + width, y + height, radius);
    }

    public static void renderRoundedQuad(Matrix3x2fStack matrices, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.TRIANGLE_FAN);
        renderRoundedQuadInternal(b, cr, cg, cb, ca, fromX, fromY, toX, toY, radius, samples);
        b.submit();
    }

    public static void renderRoundedQuad(Matrix3x2fStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.TRIANGLE_FAN);
        renderRoundedQuadInternal(b, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f, fromX, fromY, toX, toY, radius, samples);
        b.submit();
    }

    public static void renderRoundedQuad(Matrix3x2fStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius) {
        renderRoundedQuad(matrices, c, fromX, fromY, toX, toY, radius, 4);
    }

    public static void renderRoundedQuad2(Matrix3x2fStack matrices, Color c, Color c2, Color c3, Color c4, double fromX, double fromY, double toX, double toY, double radius) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.TRIANGLE_FAN);
        renderRoundedQuadInternal2(b,
                c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f,
                c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, c2.getAlpha() / 255f,
                c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f, c3.getAlpha() / 255f,
                c4.getRed() / 255f, c4.getGreen() / 255f, c4.getBlue() / 255f, c4.getAlpha() / 255f,
                fromX, fromY, toX, toY, radius);
        b.submit();
    }

    public static void renderRoundedQuadInternal(Batch bufferBuilder, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        int color = argb(cr, cg, cb, ca);
        double[][] map = new double[][]{new double[]{toX - radius, toY - radius, radius}, new double[]{toX - radius, fromY + radius, radius}, new double[]{fromX + radius, fromY + radius, radius}, new double[]{fromX + radius, toY - radius, radius}};
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex((float) current[0] + sin, (float) current[1] + cos, color);
            }
            float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            bufferBuilder.vertex((float) current[0] + sin, (float) current[1] + cos, color);
        }
    }

    public static void renderRoundedQuadInternal2(Batch bufferBuilder, float cr, float cg, float cb, float ca, float cr1, float cg1, float cb1, float ca1, float cr2, float cg2, float cb2, float ca2, float cr3, float cg3, float cb3, float ca3, double fromX, double fromY, double toX, double toY, double radC1) {
        int c0 = argb(cr, cg, cb, ca);
        int c1 = argb(cr1, cg1, cb1, ca1);
        int c2 = argb(cr2, cg2, cb2, ca2);
        int c3 = argb(cr3, cg3, cb3, ca3);

        double[][] map = new double[][]{new double[]{toX - radC1, toY - radC1, radC1}, new double[]{toX - radC1, fromY + radC1, radC1}, new double[]{fromX + radC1, fromY + radC1, radC1}, new double[]{fromX + radC1, toY - radC1, radC1}};

        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            int col = switch (i) {
                case 0 -> c1;
                case 1 -> c0;
                case 2 -> c2;
                default -> c3;
            };
            for (double r = i * 90; r < (90 + i * 90); r += 10) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex((float) current[0] + sin, (float) current[1] + cos, col);
            }
        }
    }

    private static int argb(float r, float g, float b, float a) {
        int ai = MathHelper.clamp((int) (a * 255), 0, 255);
        int ri = MathHelper.clamp((int) (r * 255), 0, 255);
        int gi = MathHelper.clamp((int) (g * 255), 0, 255);
        int bi = MathHelper.clamp((int) (b * 255), 0, 255);
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }

    /* ------------------------------------------------------------------
     * misc primitives
     * ------------------------------------------------------------------ */

    public static void draw2DGradientRect(Matrix3x2fStack matrices, float left, float top, float right, float bottom, Color leftBottomColor, Color leftTopColor, Color rightBottomColor, Color rightTopColor) {
        Batch b = Draw2D.quads(matrices);
        b.vertex(right, top, rightTopColor.getRGB());
        b.vertex(left, top, leftTopColor.getRGB());
        b.vertex(left, bottom, leftBottomColor.getRGB());
        b.vertex(right, bottom, rightBottomColor.getRGB());
        b.submit();
    }

    public static void setupRender() {
    }

    public static void endRender() {
    }

    public static void drawTracerPointer(Matrix3x2fStack matrices, float x, float y, float size, float tracerWidth, float downHeight, boolean down, boolean glow, int color) {
        switch (HudEditor.arrowsStyle.getValue()) {
            case Default -> drawDefaultArrow(matrices, x, y, size, tracerWidth, downHeight, down, glow, color);
            case New -> drawNewArrow(matrices, x, y, size + 8, new Color(color));
        }
    }

    public static void drawNewArrow(Matrix3x2fStack matrices, float x, float y, float size, Color color) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.QUADS, TextureStorage.arrow);
        int rgb = color.getRGB();
        b.vertex(x - (size / 2f), y + size, 0f, 1f, rgb);
        b.vertex(x + size / 2f, y + size, 1f, 1f, rgb);
        b.vertex(x + size / 2f, y, 1f, 0f, rgb);
        b.vertex(x - (size / 2f), y, 0f, 0f, rgb);
        b.submit();
    }

    public static void drawDefaultArrow(Matrix3x2fStack matrices, float x, float y, float size, float tracerWidth, float downHeight, boolean down, boolean glow, int color) {
        if (glow)
            drawBlurredShadow(matrices, x - size * tracerWidth, y, (x + size * tracerWidth) - (x - size * tracerWidth), size, 10, injectAlpha(new Color(color), 140));

        matrices.pushMatrix();
        Batch b = Draw2D.quads(matrices);
        b.vertex(x, y, color);
        b.vertex(x - size * tracerWidth, y + size, color);
        b.vertex(x, y + size - downHeight, color);
        b.vertex(x, y, color);
        color = darker(new Color(color), 0.8f).getRGB();
        b.vertex(x, y, color);
        b.vertex(x, y + size - downHeight, color);
        b.vertex(x + size * tracerWidth, y + size, color);
        b.vertex(x, y, color);

        if (down) {
            color = darker(new Color(color), 0.6f).getRGB();
            b.vertex(x - size * tracerWidth, y + size, color);
            b.vertex(x + size * tracerWidth, y + size, color);
            b.vertex(x, y + size - downHeight, color);
            b.vertex(x - size * tracerWidth, y + size, color);
        }
        b.submit();
        matrices.popMatrix();
    }

    public static void drawGradientRound(Matrix3x2fStack ms, float x, float y, float w, float h, float radius, Color c1, Color c2, Color c3, Color c4) {
        renderRoundedQuad2(ms, c1, c2, c3, c4, x, y, x + w, y + h, radius);
    }

    public static float scrollAnimate(float endPoint, float current, float speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0f) speed = 0.0f;
        else if (speed > 1.0f) speed = 1.0f;

        float dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        float factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }

    public static Color injectAlpha(final Color color, final float alpha) {
        return injectAlpha(color, (int) alpha);
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed, double count) {
        int r1 = cl1.getRed(), g1 = cl1.getGreen(), b1 = cl1.getBlue();
        int r2 = cl2.getRed(), g2 = cl2.getGreen(), b2 = cl2.getBlue();
        float progress = (float) ((Math.sin(System.currentTimeMillis() / (speed * 1000d) + count) + 1) / 2d);
        return interpolateColorC(cl1, cl2, progress);
    }

    public static Color astolfo(boolean clickgui, int yOffset) {
        float offset = clickgui ? 0 : 90f;
        return Color.getHSBColor((float) ((System.currentTimeMillis() % 3600L) / 3600d) + (yOffset * 0.01f) + offset / 360f % 1f, 0.7f, 1f);
    }

    public static Color rainbow(int delay, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % 20000L) / 20000f + delay / 360f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public static Color skyRainbow(int speed, int index) {
        float hue = (System.currentTimeMillis() / speed + index) % 36000L / 36000f;
        return Color.getHSBColor(hue, 0.65f, 0.9f);
    }

    public static Color fade(int speed, int index, Color color, float alpha) {
        float factor = (float) Math.abs((Math.sin((System.currentTimeMillis() / (double) speed) + index / 10.0) + 1) / 2.0);
        return applyOpacity(color, (1 - alpha) + alpha * factor);
    }

    public static Color getAnalogousColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[0] = (hsb[0] + 30f / 360f) % 1f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static int applyOpacity(int color_int, float opacity) {
        return applyOpacity(new Color(color_int, true), opacity).getRGB();
    }

    public static Color darker(Color color, float factor) {
        return new Color(Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / (speed * 10L) + (index * 40L)) % 360);
        float hue = angle / 360f;
        Color c = Color.getHSBColor(hue, saturation, brightness);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * opacity));
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis() / speed) + index) % 360) * 2;
        angle = (angle >= 180 ? 360 : 0) - angle;
        float progress = angle / 180f;
        return trueColor ? interpolateColorHue(start, end, progress) : interpolateColorC(start, end, progress);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = MathHelper.clamp(amount, 0, 1);
        int red = Math.round(color1.getRed() + (color2.getRed() - color1.getRed()) * amount);
        int green = Math.round(color1.getGreen() + (color2.getGreen() - color1.getGreen()) * amount);
        int blue = Math.round(color1.getBlue() + (color2.getBlue() - color1.getBlue()) * amount);
        int alpha = Math.round(color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * amount);
        return new Color(red, green, blue, alpha);
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        float[] hsb1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        float[] out = new float[]{
                hsb1[0] + (hsb2[0] - hsb1[0]) * amount,
                hsb1[1] + (hsb2[1] - hsb1[1]) * amount,
                hsb1[2] + (hsb2[2] - hsb1[2]) * amount
        };
        Color c = new Color(Color.HSBtoRGB(out[0], out[1], out[2]));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * amount));
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return (float) interpolate(oldValue, newValue, interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int) interpolate(oldValue, newValue, interpolationValue);
    }

    /* ------------------------------------------------------------------
     * shader-era entries, rebuilt on the batch path
     * ------------------------------------------------------------------ */

    public static void drawArc(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, float thickness, float start, float end, Color c1, Color c2) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.TRIANGLE_STRIP);
        float cx = x, cy = y;
        float total = Math.abs(end - start);
        int steps = Math.max(6, (int) (total / 6f));
        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;
            float ang = (float) Math.toRadians(start + total * t);
            int col = interpolateColorC(c1, c2, t).getRGB();
            float cos = (float) Math.cos(ang), sin = (float) Math.sin(ang);
            b.vertex(cx + cos * radius, cy + sin * radius, col);
            b.vertex(cx + cos * (radius + thickness), cy + sin * (radius + thickness), col);
        }
        b.submit();
    }

    public static void drawRect(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, float alpha) {
        if (HudEditor.glow.getValue())
            drawBlurredShadow(matrices, x, y, width, height, 6, new Color(0, 0, 0, (int) (alpha * 100)));
        renderRoundedQuad(matrices, new Color(0, 0, 0, alpha), x, y, x + width, y + height, radius, 6);
    }

    public static void drawRect(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, float alpha, Color c1, Color c2, Color c3, Color c4) {
        if (HudEditor.glow.getValue())
            drawBlurredShadow(matrices, x, y, width, height, 6, new Color(0, 0, 0, (int) (alpha * 100)));
        renderRoundedQuad2(matrices, applyOpacity(c1, alpha), applyOpacity(c2, alpha), applyOpacity(c3, alpha), applyOpacity(c4, alpha), x, y, x + width, y + height, radius);
    }

    public static void drawHudBase(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius) {
        drawHudBase(matrices, x, y, width, height, radius, HudEditor.alpha.getValue());
    }

    public static void drawHudBase2(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, float blurStrenth, float blurOpacity, float animationFactor) {
        Color c = interpolateColorC(new Color(0, 0, 0, (int) (blurOpacity * 255)), HudEditor.blurColor.getValue().getColorObject(), animationFactor);
        renderRoundedQuad(matrices, c, x, y, x + width, y + height, radius, 6);
    }

    public static void drawHudBase(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, boolean hud) {
        drawHudBase(matrices, x, y, width, height, radius, hud ? HudEditor.alpha.getValue() : 1f);
    }

    public static void drawHudBase(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, float alpha) {
        if (HudEditor.glow.getValue())
            drawBlurredShadow(matrices, x, y, width, height, 6, new Color(0, 0, 0, (int) (alpha * 100)));
        renderRoundedQuad(matrices, new Color(0, 0, 0, alpha), x, y, x + width, y + height, radius, 6);
    }

    public static void drawRoundedBlur(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, Color c1) {
        drawRoundedBlur(matrices, x, y, width, height, radius, c1, HudEditor.blurStrength.getValue(), HudEditor.blurOpacity.getValue());
    }

    public static void drawRoundedBlur(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, Color c1, float blurStrenth, float blurOpacity) {
        Color tint = applyOpacity(c1, blurOpacity);
        renderRoundedQuad(matrices, tint, x, y, x + width, y + height, radius, 6);
    }

    public static void drawGuiBase(Matrix3x2fStack matrices, float x, float y, float width, float height, float radius, float opacity) {
        renderRoundedQuad(matrices, new Color(0, 0, 0, opacity), x, y, x + width, y + height, radius, 6);
    }

    public static void drawMainMenuShader(Matrix3x2fStack matrices, float x, float y, float width, float height) {
        verticalGradient(matrices, x, y, x + width, y + height, new Color(0x101010), new Color(0x080808));
    }

    public static void drawOrbiz(Matrix3x2fStack matrices, float z, final double r, Color c) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.TRIANGLE_FAN);
        int col = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (0.4f * 255)).getRGB();
        for (int i = 0; i <= 20; i++) {
            final float x2 = (float) (Math.sin(((i * 56.548656f) / 180f)) * r);
            final float y2 = (float) (Math.cos(((i * 56.548656f) / 180f)) * r);
            b.vertex(x2, y2 + z, col);
        }
        b.submit();
    }

    public static void drawStar(Matrix3x2fStack matrices, Color c, float scale) {
        bindTexture(TextureStorage.star);
        renderGradientTexture(matrices, 0, 0, scale, scale, 0, 0, 128, 128, 128, 128, c, c, c, c);
    }

    public static void drawHeart(Matrix3x2fStack matrices, Color c, float scale) {
        bindTexture(TextureStorage.heart);
        renderGradientTexture(matrices, 0, 0, scale, scale, 0, 0, 128, 128, 128, 128, c, c, c, c);
    }

    public static void drawBloom(Matrix3x2fStack matrices, Color c, float scale) {
        bindTexture(TextureStorage.firefly);
        renderGradientTexture(matrices, 0, 0, scale, scale, 0, 0, 128, 128, 128, 128, c, c, c, c);
    }

    public static void drawBubble(Matrix3x2fStack matrices, float angle, float factor) {
        bindTexture(TextureStorage.bubble);
        matrices.pushMatrix();
        matrices.rotate((float) Math.toRadians(angle));
        float scale = factor * 2f;
        renderGradientTexture(matrices, -scale / 2, -scale / 2, scale, scale, 0, 0, 128, 128, 128, 128,
                applyOpacity(HudEditor.getColor(270), 1f - factor),
                applyOpacity(HudEditor.getColor(0), 1f - factor),
                applyOpacity(HudEditor.getColor(180), 1f - factor),
                applyOpacity(HudEditor.getColor(90), 1f - factor));
        matrices.popMatrix();
    }

    public static void drawLine(float x, float y, float x1, float y1, int color) {
        Batch b = Draw2D.of(new Matrix3x2f(), VertexFormat.DrawMode.DEBUG_LINES);
        b.vertex(x, y, color);
        b.vertex(x1, y1, color);
        b.submit();
    }

    public static boolean isDark(Color color) {
        return isDark(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public static boolean isDark(float r, float g, float b) {
        return colorDistance(r, g, b, 0f, 0f, 0f) < colorDistance(r, g, b, 1f, 1f, 1f);
    }

    public static float colorDistance(float r1, float g1, float b1, float r2, float g2, float b2) {
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    /** shader subsystem is gone on 1.21.11; kept so bootstrap code compiles */
    public static void initShaders() {
    }

    public static @NotNull Color getColor(@NotNull Color start, @NotNull Color end, float progress, boolean smooth) {
        if (!smooth)
            return progress >= 0.95 ? end : start;

        final int rDiff = end.getRed() - start.getRed();
        final int gDiff = end.getGreen() - start.getGreen();
        final int bDiff = end.getBlue() - start.getBlue();
        final int aDiff = end.getAlpha() - start.getAlpha();

        return new Color(
                fixColorValue(start.getRed() + (int) (rDiff * progress)),
                fixColorValue(start.getGreen() + (int) (gDiff * progress)),
                fixColorValue(start.getBlue() + (int) (bDiff * progress)),
                fixColorValue(start.getAlpha() + (int) (aDiff * progress)));
    }

    private static int fixColorValue(int colorVal) {
        return colorVal > 255 ? 255 : Math.max(colorVal, 0);
    }

    /**
     * Legacy flush: takes a {@link BufferBuilder} obtained from {@code Tessellator.begin(...)},
     * figures out its vertex format/draw mode via accessor mixin and draws it through a matching
     * {@link THRenderLayers} world pipeline. GUI code should prefer {@link Draw2D.Batch}.
     */
    @Deprecated
    public static void endBuilding(Object obj) {
        if (!(obj instanceof BufferBuilder bb)) return;
        BuiltBuffer built = bb.endNullable();
        if (built == null) return;
        thunder.hack.injection.accesors.IBufferBuilder acc = (thunder.hack.injection.accesors.IBufferBuilder) bb;
        flushBuffer(built, acc.th$vertexFormat(), acc.th$drawMode());
    }

    @Deprecated
    public static void endBuildingTextured(Object obj) {
        endBuilding(obj);
    }

    private static void flushBuffer(BuiltBuffer built, com.mojang.blaze3d.vertex.VertexFormat format, VertexFormat.DrawMode mode) {
        net.minecraft.client.render.RenderLayer layer;
        if (format == net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR)
            layer = THRenderLayers.worldTextured(mode, currentBoundTexture, false);
        else if (format == net.minecraft.client.render.VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH)
            layer = THRenderLayers.worldLines(mode);
        else
            layer = THRenderLayers.worldColored(mode);
        THRenderLayers.drawWorld(layer, built);
    }

    @Deprecated
    public static BufferBuilder beginBuilding(VertexFormat.DrawMode mode, Object format) {
        return Tessellator.getInstance().begin(mode, (com.mojang.blaze3d.vertex.VertexFormat) format);
    }

    public static void registerBufferedImageTexture(net.minecraft.util.Identifier id, java.awt.image.BufferedImage image) {
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

    public static Matrix4f toMatrix4f(org.joml.Matrix3x2fc mm) {
        org.joml.Matrix3x2f t = new org.joml.Matrix3x2f(mm);
        return new Matrix4f(
                t.m00, t.m01, 0f, t.m02,
                t.m10, t.m11, 0f, t.m12,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
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

    public record Rectangle(float x, float y, float x1, float y1) {
        public boolean contains(double x, double y) {
            return x >= this.x && x <= this.x1 && y >= this.y && y <= this.y1;
        }
    }
}
