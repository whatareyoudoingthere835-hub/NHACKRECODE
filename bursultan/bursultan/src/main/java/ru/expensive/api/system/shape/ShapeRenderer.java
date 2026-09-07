package ru.expensive.api.system.shape;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import ru.expensive.common.util.render.ExpensiveRenderLayers;

public class ShapeRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(color);
        ExpensiveRenderLayers.submitGuiOrDraw(ExpensiveRenderLayers.coloredGui(), bufferBuilder.end());
    }

    public static void drawRectOutline(MatrixStack matrices, float x, float y, float width, float height, float thickness, int color) {
        drawRect(matrices, x, y, width, thickness, color);
        drawRect(matrices, x, y + height - thickness, width, thickness, color);
        drawRect(matrices, x, y, thickness, height, color);
        drawRect(matrices, x + width - thickness, y, thickness, height, color);
    }

    public static double[] project(double x, double y, double z) {
        return null;
    }

    public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        radius = Math.min(radius, Math.min(width, height) / 2);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x + radius, y, 0).color(color);
        bufferBuilder.vertex(matrix, x + width - radius, y, 0).color(color);
        bufferBuilder.vertex(matrix, x + width - radius, y + height, 0).color(color);
        bufferBuilder.vertex(matrix, x + radius, y + height, 0).color(color);
        bufferBuilder.vertex(matrix, x, y + radius, 0).color(color);
        bufferBuilder.vertex(matrix, x + radius, y + radius, 0).color(color);
        bufferBuilder.vertex(matrix, x + radius, y + height - radius, 0).color(color);
        bufferBuilder.vertex(matrix, x, y + height - radius, 0).color(color);
        bufferBuilder.vertex(matrix, x + width - radius, y + radius, 0).color(color);
        bufferBuilder.vertex(matrix, x + width, y + radius, 0).color(color);
        bufferBuilder.vertex(matrix, x + width, y + height - radius, 0).color(color);
        bufferBuilder.vertex(matrix, x + width - radius, y + height - radius, 0).color(color);
        ExpensiveRenderLayers.submitGuiOrDraw(ExpensiveRenderLayers.coloredGui(), bufferBuilder.end());
        drawCorners(matrices, x, y, width, height, radius, color);
    }

    private static void drawCorners(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        drawCorner(bufferBuilder, matrix, x + radius, y + radius, radius, 180, 270, color);
        drawCorner(bufferBuilder, matrix, x + width - radius, y + radius, radius, 270, 360, color);
        drawCorner(bufferBuilder, matrix, x + width - radius, y + height - radius, radius, 0, 90, color);
        drawCorner(bufferBuilder, matrix, x + radius, y + height - radius, radius, 90, 180, color);
        ExpensiveRenderLayers.submitGuiOrDraw(ExpensiveRenderLayers.coloredGui(), bufferBuilder.end());
    }

    private static void drawCorner(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float radius, int startAngle, int endAngle, int color) {
        float prevX = x + (float) Math.cos(Math.toRadians(startAngle)) * radius;
        float prevY = y + (float) Math.sin(Math.toRadians(startAngle)) * radius;
        for (int i = startAngle + 5; i <= endAngle; i += 5) {
            float angle = (float) Math.toRadians(i);
            float currX = x + (float) Math.cos(angle) * radius;
            float currY = y + (float) Math.sin(angle) * radius;
            bufferBuilder.vertex(matrix, x, y, 0).color(color);
            bufferBuilder.vertex(matrix, prevX, prevY, 0).color(color);
            bufferBuilder.vertex(matrix, currX, currY, 0).color(color);
            bufferBuilder.vertex(matrix, currX, currY, 0).color(color);
            prevX = currX;
            prevY = currY;
        }
    }

    public static void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height, float radius, float thickness, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        radius = Math.min(radius, Math.min(width, height) / 2);

        drawRect(matrices, x + radius, y, width - 2 * radius, thickness, color);
        drawRect(matrices, x + radius, y + height - thickness, width - 2 * radius, thickness, color);
        drawRect(matrices, x, y + radius, thickness, height - 2 * radius, color);
        drawRect(matrices, x + width - thickness, y + radius, thickness, height - 2 * radius, color);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        drawCornerOutline(bufferBuilder, matrix, x + radius, y + radius, radius, thickness, 180, 270, color);
        drawCornerOutline(bufferBuilder, matrix, x + width - radius, y + radius, radius, thickness, 270, 360, color);
        drawCornerOutline(bufferBuilder, matrix, x + width - radius, y + height - radius, radius, thickness, 0, 90, color);
        drawCornerOutline(bufferBuilder, matrix, x + radius, y + height - radius, radius, thickness, 90, 180, color);
        ExpensiveRenderLayers.submitGuiOrDraw(ExpensiveRenderLayers.coloredGui(), bufferBuilder.end());
    }

    private static void drawCornerOutline(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float radius, float thickness, int startAngle, int endAngle, int color) {
        float prevOuterX = 0;
        float prevOuterY = 0;
        float prevInnerX = 0;
        float prevInnerY = 0;
        boolean first = true;
        for (int i = startAngle; i <= endAngle; i += 5) {
            float angle = (float) Math.toRadians(i);
            float outerX = x + (float) Math.cos(angle) * radius;
            float outerY = y + (float) Math.sin(angle) * radius;
            float innerX = x + (float) Math.cos(angle) * (radius - thickness);
            float innerY = y + (float) Math.sin(angle) * (radius - thickness);
            if (!first) {
                bufferBuilder.vertex(matrix, prevOuterX, prevOuterY, 0).color(color);
                bufferBuilder.vertex(matrix, prevInnerX, prevInnerY, 0).color(color);
                bufferBuilder.vertex(matrix, innerX, innerY, 0).color(color);
                bufferBuilder.vertex(matrix, outerX, outerY, 0).color(color);
            }
            prevOuterX = outerX;
            prevOuterY = outerY;
            prevInnerX = innerX;
            prevInnerY = innerY;
            first = false;
        }
    }
}
