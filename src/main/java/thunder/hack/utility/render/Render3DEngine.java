package thunder.hack.utility.render;

import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import thunder.hack.utility.render.PoseStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import thunder.hack.ThunderHack;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static thunder.hack.features.modules.Module.mc;

/**
 * 1.21.11 port: every world-space overlay is batched into a {@link BufferBuilder} and flushed
 * through a {@link RenderLayer} from {@link THRenderLayers} instead of poking GL state by hand.
 */
public class Render3DEngine {
    public static List<FillAction> FILLED_QUEUE = new ArrayList<>();
    public static List<OutlineAction> OUTLINE_QUEUE = new ArrayList<>();
    public static List<FadeAction> FADE_QUEUE = new ArrayList<>();
    public static List<FillSideAction> FILLED_SIDE_QUEUE = new ArrayList<>();
    public static List<OutlineSideAction> OUTLINE_SIDE_QUEUE = new ArrayList<>();
    public static List<DebugLineAction> DEBUG_LINE_QUEUE = new ArrayList<>();
    public static List<LineAction> LINE_QUEUE = new ArrayList<>();

    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

    private static float prevCircleStep;
    private static float circleStep;

    public static void onRender3D(PoseStack stack) {
        if (!FILLED_QUEUE.isEmpty() || !FADE_QUEUE.isEmpty() || !FILLED_SIDE_QUEUE.isEmpty()) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Matrix4f matrix = stack.peek().getPositionMatrix();

            FILLED_QUEUE.forEach(action -> setFilledBoxVertexes(bufferBuilder, matrix, action.box(), action.color()));

            FADE_QUEUE.forEach(action -> setFilledFadePoints(action.box(), bufferBuilder, matrix, action.color(), action.color2()));

            FILLED_SIDE_QUEUE.forEach(action -> setFilledSidePoints(bufferBuilder, matrix, action.box, action.color(), action.side()));

            flush(bufferBuilder, THRenderLayers.worldColored(VertexFormat.DrawMode.QUADS));

            FADE_QUEUE.clear();
            FILLED_SIDE_QUEUE.clear();
            FILLED_QUEUE.clear();
        }

        if (!OUTLINE_QUEUE.isEmpty() || !OUTLINE_SIDE_QUEUE.isEmpty()) {
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);

            OUTLINE_QUEUE.forEach(action ->
                    setOutlinePoints(action.box(), matrixFrom(action.box().minX, action.box().minY, action.box().minZ), buffer, action.color()));

            OUTLINE_SIDE_QUEUE.forEach(action ->
                    setSideOutlinePoints(action.box, matrixFrom(action.box().minX, action.box().minY, action.box().minZ), buffer, action.color(), action.side()));

            flush(buffer, THRenderLayers.worldLines(VertexFormat.DrawMode.LINES));

            OUTLINE_QUEUE.clear();
            OUTLINE_SIDE_QUEUE.clear();
        }

        if (!DEBUG_LINE_QUEUE.isEmpty()) {
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);

            DEBUG_LINE_QUEUE.forEach(action -> {
                PoseStack matrices = matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
                vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (action.end.getX() - action.start.getX()), (float) (action.end.getY() - action.start.getY()), (float) (action.end.getZ() - action.start.getZ()), action.color);
            });

            flush(buffer, THRenderLayers.worldLines(VertexFormat.DrawMode.DEBUG_LINES));
            DEBUG_LINE_QUEUE.clear();
        }

        if (!LINE_QUEUE.isEmpty()) {
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);

            LINE_QUEUE.forEach(action -> {
                PoseStack matrices = matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
                vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (action.end.getX() - action.start.getX()), (float) (action.end.getY() - action.start.getY()), (float) (action.end.getZ() - action.start.getZ()), action.color);
            });

            flush(buffer, THRenderLayers.worldLines(VertexFormat.DrawMode.LINES));
            LINE_QUEUE.clear();
        }
    }

    private static void flush(BufferBuilder buffer, RenderLayer layer) {
        THRenderLayers.drawWorld(layer, buffer.end());
    }

    @Deprecated
    @SuppressWarnings("unused")
    public static void drawFilledBox(PoseStack stack, Box box, Color c) {
        FILLED_QUEUE.add(new FillAction(box, c));
    }

    public static void setFilledBoxVertexes(@NotNull BufferBuilder bufferBuilder, Matrix4f m, @NotNull Box box, @NotNull Color c) {
        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        float minX = (float) (box.minX - cam.x);
        float minY = (float) (box.minY - cam.y);
        float minZ = (float) (box.minZ - cam.z);
        float maxX = (float) (box.maxX - cam.x);
        float maxY = (float) (box.maxY - cam.y);
        float maxZ = (float) (box.maxZ - cam.z);

        int color = c.getRGB();

        bufferBuilder.vertex(m, minX, minY, minZ).color(color);
        bufferBuilder.vertex(m, maxX, minY, minZ).color(color);
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(color);
        bufferBuilder.vertex(m, minX, minY, maxZ).color(color);

        bufferBuilder.vertex(m, minX, minY, minZ).color(color);
        bufferBuilder.vertex(m, minX, maxY, minZ).color(color);
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(color);
        bufferBuilder.vertex(m, maxX, minY, minZ).color(color);

        bufferBuilder.vertex(m, maxX, minY, minZ).color(color);
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(color);
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(color);
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(color);

        bufferBuilder.vertex(m, minX, minY, maxZ).color(color);
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(color);
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(color);
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(color);

        bufferBuilder.vertex(m, minX, minY, minZ).color(color);
        bufferBuilder.vertex(m, minX, minY, maxZ).color(color);
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(color);
        bufferBuilder.vertex(m, minX, maxY, minZ).color(color);

        bufferBuilder.vertex(m, minX, maxY, minZ).color(color);
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(color);
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(color);
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(color);
    }

    public static @NotNull Box interpolateBox(@NotNull Box from, @NotNull Box to, float delta) {
        double X = Render2DEngine.interpolate(from.maxX, to.maxX, delta);
        double Y = Render2DEngine.interpolate(from.maxY, to.maxY, delta);
        double Z = Render2DEngine.interpolate(from.maxZ, to.maxZ, delta);
        double X1 = Render2DEngine.interpolate(from.minX, to.minX, delta);
        double Y1 = Render2DEngine.interpolate(from.minY, to.minY, delta);
        double Z1 = Render2DEngine.interpolate(from.minZ, to.minZ, delta);
        return new Box(X1, Y1, Z1, X, Y, Z);
    }

    @Deprecated
    public static void drawFilledSide(PoseStack stack, @NotNull Box box, Color c, Direction dir) {
        FILLED_SIDE_QUEUE.add(new FillSideAction(box, c, dir));
    }

    public static void setFilledSidePoints(BufferBuilder buffer, Matrix4f matrix, Box box, Color c, Direction dir) {
        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        float minX = (float) (box.minX - cam.x);
        float minY = (float) (box.minY - cam.y);
        float minZ = (float) (box.minZ - cam.z);
        float maxX = (float) (box.maxX - cam.x);
        float maxY = (float) (box.maxY - cam.y);
        float maxZ = (float) (box.maxZ - cam.z);

        int color = c.getRGB();

        if (dir == Direction.DOWN) {
            buffer.vertex(matrix, minX, minY, minZ).color(color);
            buffer.vertex(matrix, maxX, minY, minZ).color(color);
            buffer.vertex(matrix, maxX, minY, maxZ).color(color);
            buffer.vertex(matrix, minX, minY, maxZ).color(color);
        }

        if (dir == Direction.NORTH) {
            buffer.vertex(matrix, minX, minY, minZ).color(color);
            buffer.vertex(matrix, minX, maxY, minZ).color(color);
            buffer.vertex(matrix, maxX, maxY, minZ).color(color);
            buffer.vertex(matrix, maxX, minY, minZ).color(color);
        }

        if (dir == Direction.EAST) {
            buffer.vertex(matrix, maxX, minY, minZ).color(color);
            buffer.vertex(matrix, maxX, maxY, minZ).color(color);
            buffer.vertex(matrix, maxX, maxY, maxZ).color(color);
            buffer.vertex(matrix, maxX, minY, maxZ).color(color);
        }
        if (dir == Direction.SOUTH) {
            buffer.vertex(matrix, minX, minY, maxZ).color(color);
            buffer.vertex(matrix, maxX, minY, maxZ).color(color);
            buffer.vertex(matrix, maxX, maxY, maxZ).color(color);
            buffer.vertex(matrix, minX, maxY, maxZ).color(color);
        }

        if (dir == Direction.WEST) {
            buffer.vertex(matrix, minX, minY, minZ).color(color);
            buffer.vertex(matrix, minX, minY, maxZ).color(color);
            buffer.vertex(matrix, minX, maxY, maxZ).color(color);
            buffer.vertex(matrix, minX, maxY, minZ).color(color);
        }

        if (dir == Direction.UP) {
            buffer.vertex(matrix, minX, maxY, minZ).color(color);
            buffer.vertex(matrix, minX, maxY, maxZ).color(color);
            buffer.vertex(matrix, maxX, maxY, maxZ).color(color);
            buffer.vertex(matrix, maxX, maxY, minZ).color(color);
        }
    }

    /**
     * Billboard text: projected to screen space and rendered through the GUI render state
     * (the vanilla {@link DrawContext} of the current frame, see {@link Draw2D#CURRENT}).
     */
    public static void drawTextIn3D(String text, @NotNull Vec3d pos, double offX, double offY, double textOffset, @NotNull Color color) {
        DrawContext context = Draw2D.CURRENT;
        if (context == null || mc.getNetworkHandler() == null) return;

        Vec3d screen = worldSpaceToScreenSpace(pos);
        if (screen.z < -1.0 || screen.z > 1.0) return;

        Matrix3x2fStack m = context.getMatrices();
        m.pushMatrix();
        m.translate((float) screen.x + (float) offX, (float) screen.y + (float) offY);
        FontRenderers.sf_medium.drawCenteredString(m, text, (float) textOffset, 0f, color.getRGB());
        m.popMatrix();
    }

    public static @NotNull Vec3d worldSpaceToScreenSpace(@NotNull Vec3d pos) {
        Camera camera = mc.net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera();
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[]{0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight()};
        Vector3f target = new Vector3f();

        double deltaX = pos.x - camera.getCameraPos().x;
        double deltaY = pos.y - camera.getCameraPos().y;
        double deltaZ = pos.z - camera.getCameraPos().z;

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(lastWorldSpaceMatrix);
        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);
        matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

        return new Vec3d(target.x / getScaleFactor(), (displayHeight - target.y) / getScaleFactor(), target.z);
    }

    public static double getScaleFactor() {
        return ClientSettings.scaleFactorFix.getValue() ? ClientSettings.scaleFactorFixValue.getValue() : mc.getWindow().getScaleFactor();
    }

    @Deprecated
    @SuppressWarnings("unused")
    public static void drawFilledFadeBox(@NotNull PoseStack stack, @NotNull Box box, @NotNull Color c, @NotNull Color c1) {
        FADE_QUEUE.add(new FadeAction(box, c, c1));
    }

    public static void setFilledFadePoints(Box box, BufferBuilder buffer, Matrix4f posMatrix, Color c, Color c1) {
        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        float minX = (float) (box.minX - cam.x);
        float minY = (float) (box.minY - cam.y);
        float minZ = (float) (box.minZ - cam.z);
        float maxX = (float) (box.maxX - cam.x);
        float maxY = (float) (box.maxY - cam.y);
        float maxZ = (float) (box.maxZ - cam.z);

        int bottom = c.getRGB();
        int top = c1.getRGB();

        buffer.vertex(posMatrix, minX, minY, minZ).color(bottom);
        buffer.vertex(posMatrix, minX, maxY, minZ).color(top);
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(top);
        buffer.vertex(posMatrix, maxX, minY, minZ).color(bottom);

        buffer.vertex(posMatrix, maxX, minY, minZ).color(bottom);
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(top);
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(top);
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(bottom);

        buffer.vertex(posMatrix, minX, minY, maxZ).color(bottom);
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(bottom);
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(top);
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(top);

        buffer.vertex(posMatrix, minX, minY, minZ).color(bottom);
        buffer.vertex(posMatrix, minX, minY, maxZ).color(bottom);
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(top);
        buffer.vertex(posMatrix, minX, maxY, minZ).color(bottom);

        buffer.vertex(posMatrix, minX, maxY, minZ).color(top);
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(top);
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(top);
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(top);
    }

    public static void drawLine(@NotNull Vec3d start, @NotNull Vec3d end, @NotNull Color color) {
        LINE_QUEUE.add(new LineAction(start, end, color));
    }

    @Deprecated
    public static void drawBoxOutline(@NotNull Box box, Color color, float lineWidth) {
        OUTLINE_QUEUE.add(new OutlineAction(box, color, lineWidth));
    }

    public static void setOutlinePoints(Box box, PoseStack matrices, BufferBuilder buffer, Color color) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
        vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
        vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
        vertexLine(matrices, buffer, x1, y2, z1, x2, y2, z1, color);
        vertexLine(matrices, buffer, x2, y2, z1, x2, y2, z2, color);
        vertexLine(matrices, buffer, x2, y2, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
    }

    @Deprecated
    public static void drawSideOutline(@NotNull Box box, Color color, float lineWidth, Direction dir) {
        OUTLINE_SIDE_QUEUE.add(new OutlineSideAction(box, color, lineWidth, dir));
    }

    public static void setSideOutlinePoints(Box box, PoseStack matrices, BufferBuilder buffer, Color color, Direction dir) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        switch (dir) {
            case UP -> {
                vertexLine(matrices, buffer, x1, y2, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y2, z1, x2, y2, z2, color);
                vertexLine(matrices, buffer, x2, y2, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
            }
            case DOWN -> {
                vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
                vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
                vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
            }
            case EAST -> {
                vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
                vertexLine(matrices, buffer, x2, y2, z2, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y1, z1, color);
            }
            case WEST -> {
                vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
            }
            case NORTH -> {
                vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z1, x1, y1, z1, color);
                vertexLine(matrices, buffer, x2, y2, z1, x1, y2, z1, color);
            }
            case SOUTH -> {
                vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
                vertexLine(matrices, buffer, x1, y1, z2, x2, y1, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x2, y2, z2, color);
            }
        }
    }

    public static void drawHoleOutline(@NotNull Box box, Color color, float lineWidth) {
        PoseStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);

        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float y2 = (float) box.maxY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float z2 = (float) box.maxZ;

        vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
        vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);

        vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
        vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);

        flush(buffer, THRenderLayers.worldLines(VertexFormat.DrawMode.LINES));
    }

    public static void vertexLine(@NotNull PoseStack matrices, @NotNull VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, @NotNull Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Vector3f normalVec = getNormal(x1, y1, z1, x2, y2, z2);
        buffer.vertex(model, x1, y1, z1).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(normalVec.x(), normalVec.y(), normalVec.z());
        buffer.vertex(model, x2, y2, z2).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(normalVec.x(), normalVec.y(), normalVec.z());
    }

    public static @NotNull Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

    public static @NotNull PoseStack matrixFrom(double x, double y, double z) {
        PoseStack matrices = new PoseStack();

        Camera camera = MinecraftClient.getInstance().net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

        matrices.translate(x - camera.getCameraPos().x, y - camera.getCameraPos().y, z - camera.getCameraPos().z);

        return matrices;
    }

    @Deprecated
    public static void setupRender() {
    }

    @Deprecated
    public static void endRender() {
    }

    public static void drawTargetEsp(PoseStack stack, @NotNull Entity target) {
        ArrayList<Vec3d> vecs = new ArrayList<>();
        ArrayList<Vec3d> vecs1 = new ArrayList<>();
        ArrayList<Vec3d> vecs2 = new ArrayList<>();

        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        double x = (target.getX() - target.getVelocity().x) + (target.getX() - (target.getX() - target.getVelocity().x)) * getTickDelta() - cam.x;
        double y = (target.getY() - target.getVelocity().y) + (target.getY() - (target.getY() - target.getVelocity().y)) * getTickDelta() - cam.y;
        double z = (target.getZ() - target.getVelocity().z) + (target.getZ() - (target.getZ() - target.getVelocity().z)) * getTickDelta() - cam.z;

        double height = target.getHeight();

        for (int i = 0; i <= 361; ++i) {
            double v = Math.sin(Math.toRadians(i));
            double u = Math.cos(Math.toRadians(i));
            Vec3d vec = new Vec3d((float) (u * 0.5f), height, (float) (v * 0.5f));
            vecs.add(vec);

            double v1 = Math.sin(Math.toRadians((i + 120) % 360));
            double u1 = Math.cos(Math.toRadians(i + 120) % 360);
            Vec3d vec1 = new Vec3d((float) (u1 * 0.5f), height, (float) (v1 * 0.5f));
            vecs1.add(vec1);

            double v2 = Math.sin(Math.toRadians((i + 240) % 360));
            double u2 = Math.cos(Math.toRadians((i + 240) % 360));
            Vec3d vec2 = new Vec3d((float) (u2 * 0.5f), height, (float) (v2 * 0.5f));
            vecs2.add(vec2);
            height -= 0.004f;
        }

        stack.push();
        stack.translate(x, y, z);

        Matrix4f matrix = stack.peek().getPositionMatrix();
        RenderLayer layer = THRenderLayers.worldColored(VertexFormat.DrawMode.TRIANGLE_STRIP);

        for (List<Vec3d> ring : List.of(vecs, vecs1, vecs2)) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int j = 0; j < ring.size() - 1; ++j) {
                float alpha = 1f - (((float) j + ((System.currentTimeMillis() - ThunderHack.initTime) / 5f)) % 360) / 60f;
                int c = Render2DEngine.injectAlpha(HudEditor.getColor((int) (j / 20f)), (int) (alpha * 255)).getRGB();
                bufferBuilder.vertex(matrix, (float) ring.get(j).x, (float) ring.get(j).y, (float) ring.get(j).z).color(c);
                bufferBuilder.vertex(matrix, (float) ring.get(j + 1).x, (float) ring.get(j + 1).y + 0.1f, (float) ring.get(j + 1).z).color(c);
            }
            flush(bufferBuilder, layer);
        }

        stack.translate(-x, -y, -z);
        stack.pop();
    }

    public static void renderCrosses(@NotNull Box box, Color color, float lineWidth) {
        PoseStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);

        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        vertexLine(matrices, buffer, (float) box.maxX, (float) box.minY, (float) box.minZ, (float) box.minX, (float) box.minY, (float) box.maxZ, color);
        vertexLine(matrices, buffer, (float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.minY, (float) box.maxZ, color);

        flush(buffer, THRenderLayers.worldLines(VertexFormat.DrawMode.LINES));
    }

    public static void drawSphere(PoseStack matrix, float radius, int slices, int stacks, int color) {
        float drho = 3.1415927F / ((float) stacks);
        float dtheta = 6.2831855F / ((float) slices - 1f);
        float rho;
        float theta;
        float x;
        float y;
        float z;
        int i;
        int j;
        RenderLayer layer = THRenderLayers.worldColored(VertexFormat.DrawMode.DEBUG_LINE_STRIP);

        for (i = 1; i < stacks; ++i) {
            rho = (float) i * drho;

            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

            for (j = 0; j < slices; ++j) {
                theta = (float) j * dtheta;
                x = (float) (Math.cos(theta) * Math.sin(rho));
                y = (float) (Math.sin(theta) * Math.sin(rho));
                z = (float) Math.cos(rho);
                buffer.vertex(matrix.peek().getPositionMatrix(), x * radius, y * radius, z * radius).color(color);
            }
            flush(buffer, layer);
        }

        for (j = 0; j < slices; ++j) {
            theta = (float) j * dtheta;

            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

            for (i = 0; i <= stacks; ++i) {
                rho = (float) i * drho;
                x = (float) (Math.cos(theta) * Math.sin(rho));
                y = (float) (Math.sin(theta) * Math.sin(rho));
                z = (float) Math.cos(rho);
                buffer.vertex(matrix.peek().getPositionMatrix(), x * radius, y * radius, z * radius).color(color);
            }
            flush(buffer, layer);
        }
    }

    public static void drawCylinder(PoseStack stack, final float radius, final float height, final int slices, final int stacks, int color) {

        final float da = (float) ((Math.PI * 2f) / slices);
        final float dz = height / stacks;
        RenderLayer layer = THRenderLayers.worldColored(VertexFormat.DrawMode.DEBUG_LINE_STRIP);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        float y = 0;

        for (int j = 0; j <= stacks; ++j) {
            for (int i = 0; i <= slices; ++i) {
                final float x = (float) Math.cos(i * da);
                final float z = (float) Math.sin(i * da);
                buffer.vertex(stack.peek().getPositionMatrix(), x * radius, y, z * radius).color(color);
            }
            y += dz;
        }

        flush(buffer, layer);

        buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        for (int i = 0; i <= slices; ++i) {
            final float x = (float) Math.cos(i * da);
            final float z = (float) Math.sin(i * da);

            buffer.vertex(stack.peek().getPositionMatrix(), x * radius, 0, z * radius).color(color);
            buffer.vertex(stack.peek().getPositionMatrix(), x * radius, height, z * radius).color(color);
        }

        flush(buffer, layer);
    }


    public static void drawCircle3D(PoseStack stack, Entity ent, float radius, int color, int points, boolean hudColor, int colorOffset) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        double x = (ent.getX() - ent.getVelocity().x) + (ent.getX() - (ent.getX() - ent.getVelocity().x)) * getTickDelta() - cam.x;
        double y = (ent.getY() - ent.getVelocity().y) + (ent.getY() - (ent.getY() - ent.getVelocity().y)) * getTickDelta() - cam.y;
        double z = (ent.getZ() - ent.getVelocity().z) + (ent.getZ() - (ent.getZ() - ent.getVelocity().z)) * getTickDelta() - cam.z;
        stack.push();
        stack.translate(x, y, z);

        Matrix4f matrix = stack.peek().getPositionMatrix();
        for (int i = 0; i <= points; i++) {
            if (hudColor)
                color = HudEditor.getColor(i * colorOffset).getRGB();

            bufferBuilder.vertex(matrix, (float) (radius * Math.cos(i * 6.28 / points)), 0f, (float) (radius * Math.sin(i * 6.28 / points))).color(color);
        }

        flush(bufferBuilder, THRenderLayers.worldColored(VertexFormat.DrawMode.DEBUG_LINE_STRIP));
        stack.translate(-x, -y, -z);
        stack.pop();
    }

    public static void drawOldTargetEsp(PoseStack stack, Entity target) {
        double cs = prevCircleStep + (circleStep - prevCircleStep) * getTickDelta();
        double prevSinAnim = absSinAnimation(cs - 0.45f);
        double sinAnim = absSinAnimation(cs);
        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        double x = (target.getX() - target.getVelocity().x) + (target.getX() - (target.getX() - target.getVelocity().x)) * getTickDelta() - cam.x;
        double y = (target.getY() - target.getVelocity().y) + (target.getY() - (target.getY() - target.getVelocity().y)) * getTickDelta() - cam.y + prevSinAnim * target.getHeight();
        double z = (target.getZ() - target.getVelocity().z) + (target.getZ() - (target.getZ() - target.getVelocity().z)) * getTickDelta() - cam.z;
        double nextY = (target.getY() - target.getVelocity().y) + (target.getY() - (target.getY() - target.getVelocity().y)) * getTickDelta() - cam.y + sinAnim * target.getHeight();
        stack.push();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        float cos;
        float sin;
        for (int i = 0; i <= 30; i++) {
            cos = (float) (x + Math.cos(i * 6.28 / 30) * target.getWidth() * 0.8);
            sin = (float) (z + Math.sin(i * 6.28 / 30) * target.getWidth() * 0.8);
            bufferBuilder.vertex(stack.peek().getPositionMatrix(), cos, (float) nextY, sin).color(Render2DEngine.injectAlpha(HudEditor.getColor(i), 170).getRGB());
            bufferBuilder.vertex(stack.peek().getPositionMatrix(), cos, (float) y, sin).color(Render2DEngine.injectAlpha(HudEditor.getColor(i), 0).getRGB());
        }
        flush(bufferBuilder, THRenderLayers.worldColored(VertexFormat.DrawMode.TRIANGLE_STRIP));
        stack.pop();
    }

    // Kalry не пасть
    // anti yg protection
    public static void renderGhosts(int espLength, int factor, float shaking, float amplitude, Entity target) {
        Camera camera = mc.net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera();

        double tPosX = Render2DEngine.interpolate((target.getX() - target.getVelocity().x), target.getX(), Render3DEngine.getTickDelta(false)) - camera.getCameraPos().x;
        double tPosY = Render2DEngine.interpolate((target.getY() - target.getVelocity().y), target.getY(), Render3DEngine.getTickDelta(false)) - camera.getCameraPos().y;
        double tPosZ = Render2DEngine.interpolate((target.getZ() - target.getVelocity().z), target.getZ(), Render3DEngine.getTickDelta(false)) - camera.getCameraPos().z;
        float iAge = (float) Render2DEngine.interpolate(target.age - 1, target.age, Render3DEngine.getTickDelta(false));

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i <= espLength; i++) {
                double radians = Math.toRadians((((float) i / 1.5f + iAge) * factor + (j * 120)) % (factor * 360));
                double sinQuad = Math.sin(Math.toRadians(iAge * 2.5f + i * (j + 1)) * amplitude) / shaking;

                float offset = ((float) i / espLength);
                PoseStack matrices = new PoseStack();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                matrices.translate(tPosX + Math.cos(radians) * target.getWidth(), (tPosY + 1 + sinQuad), tPosZ + Math.sin(radians) * target.getWidth());
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                Matrix4f matrix = matrices.peek().getPositionMatrix();
                int color = Render2DEngine.applyOpacity(HudEditor.getColor((int) (180 * offset)), offset).getRGB();
                float scale = Math.max(0.24f * (offset), 0.2f);
                buffer.vertex(matrix, -scale, scale, 0).texture(0f, 1f).color(color);
                buffer.vertex(matrix, scale, scale, 0).texture(1f, 1f).color(color);
                buffer.vertex(matrix, scale, -scale, 0).texture(1f, 0).color(color);
                buffer.vertex(matrix, -scale, -scale, 0).texture(0, 0).color(color);
            }
        }

        flush(buffer, THRenderLayers.worldTextured(VertexFormat.DrawMode.QUADS, TextureStorage.firefly, false));
    }

    public static void updateTargetESP() {
        prevCircleStep = circleStep;
        circleStep += 0.15f;
    }

    public static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        Vec3d cam = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        double x = prevposX + ((posX - prevposX) * getTickDelta()) - cam.x;
        double y = prevposY + ((posY - prevposY) * getTickDelta()) - cam.y;
        double z = prevposZ + ((posZ - prevposZ) * getTickDelta()) - cam.z;
        return new Vec3d(x, y, z);
    }

    public static void drawLineDebug(Vec3d start, Vec3d end, Color color) {
        DEBUG_LINE_QUEUE.add(new DebugLineAction(start, end, color));
    }

public static float getTickDelta(boolean secondsPerTick) {
        return getTickDelta();
    }

    public static float getTickDelta() {
        return mc.getRenderTickCounter().getTickDelta(false);
    }

    public record FillAction(Box box, Color color) {
    }

    public record OutlineAction(Box box, Color color, float lineWidth) {
    }

    public record FadeAction(Box box, Color color, Color color2) {
    }

    public record FillSideAction(Box box, Color color, Direction side) {
    }

    public record OutlineSideAction(Box box, Color color, float lineWidth, Direction side) {
    }

    public record DebugLineAction(Vec3d start, Vec3d end, Color color) {
    }

    public record LineAction(Vec3d start, Vec3d end, Color color) {
    }
}
