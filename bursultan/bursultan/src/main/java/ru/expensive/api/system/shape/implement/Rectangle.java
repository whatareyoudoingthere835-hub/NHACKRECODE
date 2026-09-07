package ru.expensive.api.system.shape.implement;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4i;
import ru.expensive.api.system.shape.Shape;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.render.ExpensiveRenderLayers;

import java.util.ArrayList;
import java.util.List;

public class Rectangle implements Shape, QuickImports {

    private record ContourPoint(float x, float y, float nx, float ny) {}

    @Override
    public void render(ShapeProperties shapeProperties) {
        float x = shapeProperties.getX();
        float y = shapeProperties.getY();
        float w = shapeProperties.getWidth();
        float h = shapeProperties.getHeight();

        if (w <= 0 || h <= 0) {
            return;
        }

        float scale = (float) (mc != null && mc.getWindow() != null ? mc.getWindow().getScaleFactor() : 2.0F);
        if (scale < 1.0F) scale = 2.0F;

        Matrix4f matrix = shapeProperties.getMatrix4f() != null ? shapeProperties.getMatrix4f() : new Matrix4f();
        Vector4i color = shapeProperties.getColor();
        Vector4f round = shapeProperties.getRound();

        float rTR = (round != null ? Math.max(0, round.x) : 0) / scale;
        float rBR = (round != null ? Math.max(0, round.y) : 0) / scale;
        float rTL = (round != null ? Math.max(0, round.z) : 0) / scale;
        float rBL = (round != null ? Math.max(0, round.w) : 0) / scale;

        float factor = 1.0F;
        if (rTL + rTR > w && (rTL + rTR) > 0) factor = Math.min(factor, w / (rTL + rTR));
        if (rBL + rBR > w && (rBL + rBR) > 0) factor = Math.min(factor, w / (rBL + rBR));
        if (rTL + rBL > h && (rTL + rBL) > 0) factor = Math.min(factor, h / (rTL + rBL));
        if (rTR + rBR > h && (rTR + rBR) > 0) factor = Math.min(factor, h / (rTR + rBR));

        if (factor < 1.0F) {
            rTR *= factor;
            rBR *= factor;
            rTL *= factor;
            rBL *= factor;
        }

        float rawThickness = shapeProperties.getThickness();
        int outlineColor = shapeProperties.getOutlineColor();
        boolean hasOutline = (rawThickness > 0 && (outlineColor >>> 24) != 0);

        boolean hasFill = (color != null && (
                (color.x >>> 24) != 0 ||
                (color.y >>> 24) != 0 ||
                (color.z >>> 24) != 0 ||
                (color.w >>> 24) != 0
        ));

        if (!hasFill && !hasOutline) {
            return;
        }

        float thickness = Math.max(0.25F, (rawThickness * 0.5F) / scale);
        float softness = shapeProperties.getSoftness() / scale;
        float aa = 1.0F / scale;
        boolean hasGlow = softness > 0;

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        List<ContourPoint> contour = buildContour(x, y, w, h, rTL, rTR, rBR, rBL);

        // 1. Glow / Softness
        if (hasGlow && softness >= 0.5F) {
            int glowColor = hasOutline ? outlineColor : (color != null ? color.x : outlineColor);
            drawGlowRibbon(buffer, matrix, contour, glowColor, softness);
        }

        // 2. Interior Fill
        if (hasFill) {
            drawInteriorFill(buffer, matrix, x, y, w, h, rTL, rTR, rBR, rBL, color);
            if (!hasOutline && (!hasGlow || softness < 0.5F)) {
                drawAARibbon(buffer, matrix, contour, color, x, y, w, h, aa);
            }
        }

        // 3. Outline
        if (hasOutline) {
            drawOutline(buffer, matrix, contour, thickness, outlineColor);
            drawAAOutlineRibbon(buffer, matrix, contour, outlineColor, aa);
        }

        ExpensiveRenderLayers.submitGuiOrDraw(ExpensiveRenderLayers.coloredGui(), buffer.end());
    }

    private List<ContourPoint> buildContour(float x, float y, float w, float h,
                                            float rTL, float rTR, float rBR, float rBL) {
        List<ContourPoint> points = new ArrayList<>();

        float ctlX = x + rTL, ctlY = y + rTL;
        float ctrX = x + w - rTR, ctrY = y + rTR;
        float cbrX = x + w - rBR, cbrY = y + h - rBR;
        float cblX = x + rBL, cblY = y + h - rBL;

        // 1. Top-Left: PI to 1.5 * PI
        addArcOrSharp(points, ctlX, ctlY, rTL, (float) Math.PI, (float) (1.5 * Math.PI), -1, 0, 0, -1);

        // 2. Top-Right: 1.5 * PI to 2.0 * PI
        addArcOrSharp(points, ctrX, ctrY, rTR, (float) (1.5 * Math.PI), (float) (2.0 * Math.PI), 0, -1, 1, 0);

        // 3. Bottom-Right: 0.0 to 0.5 * PI
        addArcOrSharp(points, cbrX, cbrY, rBR, 0.0F, (float) (0.5 * Math.PI), 1, 0, 0, 1);

        // 4. Bottom-Left: 0.5 * PI to PI
        addArcOrSharp(points, cblX, cblY, rBL, (float) (0.5 * Math.PI), (float) Math.PI, 0, 1, -1, 0);

        return points;
    }

    private void addArcOrSharp(List<ContourPoint> points, float cx, float cy, float radius,
                               float startAngle, float endAngle,
                               float nStart1, float nStart2, float nEnd1, float nEnd2) {
        if (radius > 0.5F) {
            int steps = Math.max(8, Math.min(24, (int) (radius * 2.0F)));
            for (int i = 0; i <= steps; i++) {
                float angle = startAngle + (endAngle - startAngle) * ((float) i / steps);
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);
                points.add(new ContourPoint(cx + cos * radius, cy + sin * radius, cos, sin));
            }
        } else {
            points.add(new ContourPoint(cx, cy, nStart1, nStart2));
            points.add(new ContourPoint(cx, cy, nEnd1, nEnd2));
        }
    }

    private void drawInteriorFill(BufferBuilder buffer, Matrix4f matrix,
                                  float x, float y, float w, float h,
                                  float rTL, float rTR, float rBR, float rBL,
                                  Vector4i color) {
        float ctlX = x + rTL, ctlY = y + rTL;
        float ctrX = x + w - rTR, ctrY = y + rTR;
        float cbrX = x + w - rBR, cbrY = y + h - rBR;
        float cblX = x + rBL, cblY = y + h - rBL;

        // Central Quad
        addQuad(buffer, matrix,
                ctlX, ctlY,
                ctrX, ctrY,
                cbrX, cbrY,
                cblX, cblY,
                color, x, y, w, h);

        // Top Quad
        if (rTL > 0 || rTR > 0) {
            addQuad(buffer, matrix,
                    ctlX, y,
                    ctrX, y,
                    ctrX, ctrY,
                    ctlX, ctlY,
                    color, x, y, w, h);
        }

        // Right Quad
        if (rTR > 0 || rBR > 0) {
            addQuad(buffer, matrix,
                    ctrX, ctrY,
                    x + w, ctrY,
                    x + w, cbrY,
                    cbrX, cbrY,
                    color, x, y, w, h);
        }

        // Bottom Quad
        if (rBL > 0 || rBR > 0) {
            addQuad(buffer, matrix,
                    cblX, cblY,
                    cbrX, cbrY,
                    cbrX, y + h,
                    cblX, y + h,
                    color, x, y, w, h);
        }

        // Left Quad
        if (rTL > 0 || rBL > 0) {
            addQuad(buffer, matrix,
                    x, ctlY,
                    ctlX, ctlY,
                    cblX, cblY,
                    x, cblY,
                    color, x, y, w, h);
        }

        // 4 Corner Fans
        if (rTL > 0.5F) {
            addCornerFan(buffer, matrix, ctlX, ctlY, rTL, (float) Math.PI, (float) (1.5 * Math.PI), color, x, y, w, h);
        }
        if (rTR > 0.5F) {
            addCornerFan(buffer, matrix, ctrX, ctrY, rTR, (float) (1.5 * Math.PI), (float) (2.0 * Math.PI), color, x, y, w, h);
        }
        if (rBR > 0.5F) {
            addCornerFan(buffer, matrix, cbrX, cbrY, rBR, 0.0F, (float) (0.5 * Math.PI), color, x, y, w, h);
        }
        if (rBL > 0.5F) {
            addCornerFan(buffer, matrix, cblX, cblY, rBL, (float) (0.5 * Math.PI), (float) Math.PI, color, x, y, w, h);
        }
    }

    private void addCornerFan(BufferBuilder buffer, Matrix4f matrix,
                              float cx, float cy, float radius,
                              float startAngle, float endAngle,
                              Vector4i color, float rx, float ry, float rw, float rh) {
        int steps = Math.max(8, Math.min(24, (int) (radius * 2.0F)));
        int centerCol = interpolateColor(color, cx, cy, rx, ry, rw, rh);

        float prevX = cx + (float) Math.cos(startAngle) * radius;
        float prevY = cy + (float) Math.sin(startAngle) * radius;
        int prevCol = interpolateColor(color, prevX, prevY, rx, ry, rw, rh);

        for (int i = 1; i <= steps; i++) {
            float angle = startAngle + (endAngle - startAngle) * ((float) i / steps);
            float curX = cx + (float) Math.cos(angle) * radius;
            float curY = cy + (float) Math.sin(angle) * radius;
            int curCol = interpolateColor(color, curX, curY, rx, ry, rw, rh);

            buffer.vertex(matrix, cx, cy, 0.0F).color(centerCol);
            buffer.vertex(matrix, curX, curY, 0.0F).color(curCol);
            buffer.vertex(matrix, prevX, prevY, 0.0F).color(prevCol);

            prevX = curX;
            prevY = curY;
            prevCol = curCol;
        }
    }

    private void addQuad(BufferBuilder buffer, Matrix4f matrix,
                         float x0, float y0,
                         float x1, float y1,
                         float x2, float y2,
                         float x3, float y3,
                         Vector4i color, float rx, float ry, float rw, float rh) {
        int c0 = interpolateColor(color, x0, y0, rx, ry, rw, rh);
        int c1 = interpolateColor(color, x1, y1, rx, ry, rw, rh);
        int c2 = interpolateColor(color, x2, y2, rx, ry, rw, rh);
        int c3 = interpolateColor(color, x3, y3, rx, ry, rw, rh);

        buffer.vertex(matrix, x0, y0, 0.0F).color(c0);
        buffer.vertex(matrix, x1, y1, 0.0F).color(c1);
        buffer.vertex(matrix, x2, y2, 0.0F).color(c2);

        buffer.vertex(matrix, x0, y0, 0.0F).color(c0);
        buffer.vertex(matrix, x2, y2, 0.0F).color(c2);
        buffer.vertex(matrix, x3, y3, 0.0F).color(c3);
    }

    private void drawGlowRibbon(BufferBuilder buffer, Matrix4f matrix,
                                List<ContourPoint> contour, int baseColor, float softness) {
        int n = contour.size();
        if (n < 3 || softness <= 0) return;

        int baseAlpha = (baseColor >>> 24) & 0xFF;
        if (baseAlpha == 0) return;

        int rings = Math.min(16, Math.max(6, (int) (softness * 1.5F)));
        for (int ring = 0; ring < rings; ring++) {
            float t0 = (float) ring / rings;
            float t1 = (float) (ring + 1) / rings;
            float d0 = t0 * softness;
            float d1 = t1 * softness;

            float a0 = (1.0F - t0) * (1.0F - t0);
            float a1 = (1.0F - t1) * (1.0F - t1);

            drawRibbonBand(buffer, matrix, contour, baseColor, d0, d1, a0, a1);
        }
    }

    private void drawRibbonBand(BufferBuilder buffer, Matrix4f matrix,
                                List<ContourPoint> contour, int baseColor,
                                float d1, float d2, float a1, float a2) {
        int n = contour.size();
        int color1 = withAlpha(baseColor, a1);
        int color2 = withAlpha(baseColor, a2);

        for (int i = 0; i < n; i++) {
            ContourPoint p1 = contour.get(i);
            ContourPoint p2 = contour.get((i + 1) % n);

            float p1x_in = p1.x + p1.nx * d1;
            float p1y_in = p1.y + p1.ny * d1;

            float p2x_in = p2.x + p2.nx * d1;
            float p2y_in = p2.y + p2.ny * d1;

            float p1x_out = p1.x + p1.nx * d2;
            float p1y_out = p1.y + p1.ny * d2;

            float p2x_out = p2.x + p2.nx * d2;
            float p2y_out = p2.y + p2.ny * d2;

            buffer.vertex(matrix, p1x_in, p1y_in, 0.0F).color(color1);
            buffer.vertex(matrix, p2x_in, p2y_in, 0.0F).color(color1);
            buffer.vertex(matrix, p2x_out, p2y_out, 0.0F).color(color2);

            buffer.vertex(matrix, p1x_in, p1y_in, 0.0F).color(color1);
            buffer.vertex(matrix, p2x_out, p2y_out, 0.0F).color(color2);
            buffer.vertex(matrix, p1x_out, p1y_out, 0.0F).color(color2);
        }
    }

    private void drawOutline(BufferBuilder buffer, Matrix4f matrix,
                             List<ContourPoint> contour, float thickness, int outlineColor) {
        drawRibbonBand(buffer, matrix, contour, outlineColor, -thickness, 0.0F, 1.0F, 1.0F);
    }

    private void drawAAOutlineRibbon(BufferBuilder buffer, Matrix4f matrix,
                                     List<ContourPoint> contour, int outlineColor, float aa) {
        drawRibbonBand(buffer, matrix, contour, outlineColor, 0.0F, aa, 1.0F, 0.0F);
    }

    private void drawAARibbon(BufferBuilder buffer, Matrix4f matrix,
                              List<ContourPoint> contour, Vector4i color,
                              float rx, float ry, float rw, float rh, float aa) {
        int n = contour.size();
        for (int i = 0; i < n; i++) {
            ContourPoint p1 = contour.get(i);
            ContourPoint p2 = contour.get((i + 1) % n);

            int c1 = interpolateColor(color, p1.x, p1.y, rx, ry, rw, rh);
            int c2 = interpolateColor(color, p2.x, p2.y, rx, ry, rw, rh);

            int c1_fade = withAlpha(c1, 0.0F);
            int c2_fade = withAlpha(c2, 0.0F);

            float d2 = aa;
            float p1x_out = p1.x + p1.nx * d2;
            float p1y_out = p1.y + p1.ny * d2;

            float p2x_out = p2.x + p2.nx * d2;
            float p2y_out = p2.y + p2.ny * d2;

            buffer.vertex(matrix, p1.x, p1.y, 0.0F).color(c1);
            buffer.vertex(matrix, p2.x, p2.y, 0.0F).color(c2);
            buffer.vertex(matrix, p2x_out, p2y_out, 0.0F).color(c2_fade);

            buffer.vertex(matrix, p1.x, p1.y, 0.0F).color(c1);
            buffer.vertex(matrix, p2x_out, p2y_out, 0.0F).color(c2_fade);
            buffer.vertex(matrix, p1x_out, p1y_out, 0.0F).color(c1_fade);
        }
    }

    private static int withAlpha(int color, float alphaFactor) {
        int a = (color >>> 24) & 0xFF;
        int newA = MathHelper.clamp((int) (a * alphaFactor), 0, 255);
        return (color & 0x00FFFFFF) | (newA << 24);
    }

    private int interpolateColor(Vector4i color, float px, float py, float x, float y, float w, float h) {
        if (color == null) return -1;
        if (color.x == color.y && color.y == color.z && color.z == color.w) {
            return color.x;
        }
        float u = w > 0 ? MathHelper.clamp((px - x) / w, 0.0F, 1.0F) : 0.0F;
        float v = h > 0 ? MathHelper.clamp((py - y) / h, 0.0F, 1.0F) : 0.0F;
        int top = lerpColor(u, color.x, color.w);
        int bottom = lerpColor(u, color.y, color.z);
        return lerpColor(v, top, bottom);
    }

    private static int lerpColor(float delta, int start, int end) {
        int a1 = (start >>> 24) & 0xFF;
        int r1 = (start >>> 16) & 0xFF;
        int g1 = (start >>> 8) & 0xFF;
        int b1 = start & 0xFF;

        int a2 = (end >>> 24) & 0xFF;
        int r2 = (end >>> 16) & 0xFF;
        int g2 = (end >>> 8) & 0xFF;
        int b2 = end & 0xFF;

        int a = (int) (a1 + delta * (a2 - a1));
        int r = (int) (r1 + delta * (r2 - r1));
        int g = (int) (g1 + delta * (g2 - g1));
        int b = (int) (b1 + delta * (b2 - b1));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
