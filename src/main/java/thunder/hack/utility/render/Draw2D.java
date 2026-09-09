package thunder.hack.utility.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import thunder.hack.injection.accesors.IDrawContextState;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 1.21.11 immediate-mode bridge: geometry is recorded into {@link Batch} objects and
 * submitted as {@code SimpleGuiElementRenderState} into the active DrawContext's
 * GuiRenderState, so vanilla's gui pass uploads/batches it correctly (scissor, layering,
 * projection). All ThunderHack 2d primitives funnel through here.
 */
public final class Draw2D {

    /** GuiRenderState queues are built per DrawContext; set by MixinDrawContext on construction. */
    public static DrawContext CURRENT;

    private static final Deque<ScreenRect> SCISSOR = new ArrayDeque<>();

    private Draw2D() {
    }

    // ---------------- scissor ----------------

    public static void pushScissor(float x0, float y0, float x1, float y1) {
        int ix0 = Math.round(x0), iy0 = Math.round(y0);
        int w = Math.max(0, Math.round(x1) - ix0), h = Math.max(0, Math.round(y1) - iy0);
        ScreenRect rect = new ScreenRect(ix0, iy0, w, h);
        ScreenRect clipped = rect;
        ScreenRect top = SCISSOR.peekLast();
        if (top != null) {
            ScreenRect is = top.intersection(rect);
            if (is == null) return; // fully clipped — still push so pop matches
            clipped = is;
        }
        SCISSOR.addLast(clipped);
        if (CURRENT != null) CURRENT.enableScissor(clipped.getLeft(), clipped.getTop(), clipped.getRight(), clipped.getBottom());
    }

    public static void popScissor() {
        SCISSOR.pollLast();
        if (CURRENT != null) CURRENT.disableScissor();
    }

    public static void clearScissor() {
        SCISSOR.clear();
    }

    @Nullable
    public static ScreenRect currentScissor() {
        return SCISSOR.peekLast();
    }

    // ---------------- batches ----------------

    /** One draw call = one GuiRenderState element (vanilla batches all elements internally). */
    public static final class Batch implements SimpleGuiElementRenderState {
        private final Matrix3x2f pose;
        private final ScreenRect scissor;
        private float[] data = new float[128];
        private int size;
        private float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        final int stride;
        RenderPipeline pipeline;
        Identifier texture;

        Batch(Matrix3x2fc pose, int stride) {
            this.pose = new Matrix3x2f(pose);
            this.stride = stride;
            this.scissor = SCISSOR.peekLast();
        }

        public Batch vertex(float x, float y, int argb) {
            grow(3);
            data[size++] = x;
            data[size++] = y;
            data[size++] = Float.intBitsToFloat(argb);
            bounds(x, y);
            return this;
        }

        public Batch vertex(float x, float y, float u, float v, int argb) {
            grow(5);
            data[size++] = x;
            data[size++] = y;
            data[size++] = u;
            data[size++] = v;
            data[size++] = Float.intBitsToFloat(argb);
            bounds(x, y);
            return this;
        }

        // aliases used by the ThunderHack engine
        public Batch batch(float x, float y, int argb) {
            return vertex(x, y, argb);
        }

        public Batch batch(float x, float y, float u, float v, int argb) {
            return vertex(x, y, u, v, argb);
        }

        private void bounds(float x, float y) {
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        private void grow(int need) {
            if (size + need > data.length) {
                float[] nd = new float[Math.max(data.length * 2, size + need)];
                System.arraycopy(data, 0, nd, 0, size);
                data = nd;
            }
        }

        @Override
        public void setupVertices(VertexConsumer vertices) {
            if (stride == 3) {
                for (int i = 0; i < size; i += 3)
                    vertices.vertex(pose, data[i], data[i + 1]).color(Float.floatToIntBits(data[i + 2]));
            } else {
                for (int i = 0; i < size; i += 5)
                    vertices.vertex(pose, data[i], data[i + 1]).texture(data[i + 2], data[i + 3]).color(Float.floatToIntBits(data[i + 4]));
            }
        }

        @Override
        public RenderPipeline pipeline() {
            return pipeline != null ? pipeline : (stride == 3 ? RenderPipelines.GUI : RenderPipelines.GUI_TEXTURED);
        }

        @Override
        public net.minecraft.client.texture.TextureSetup textureSetup() {
            return pipeline == null && stride == 3 ? net.minecraft.client.texture.TextureSetup.empty() : THRenderLayers.guiTextureSetup(texture);
        }

        @Nullable
        @Override
        public ScreenRect scissorArea() {
            return scissor;
        }

        @Nullable
        @Override
        public ScreenRect bounds() {
            if (size == 0) return ScreenRect.EMPTY;
            return new ScreenRect(Math.round(minX), Math.round(minY), Math.round(maxX - minX), Math.round(maxY - minY));
        }

        /** Submits to the render state. Call exactly once. */
        public void submit() {
            if (size == 0) return;
            DrawContext ctx = CURRENT;
            if (ctx == null) return;
            GuiRenderState state = ((IDrawContextState) ctx).getState();
            state.addSimpleElement(this);
            size = 0;
        }
    }

    /** colored quads (most common) */
    public static Batch quads(Matrix3x2fc pose) {
        return new Batch(pose, 3);
    }

    public static Batch of(Matrix3x2fc pose, VertexFormat.DrawMode mode, @Nullable Identifier texture) {
        Batch b = new Batch(pose, texture == null ? 3 : 5);
        b.texture = texture;
        b.pipeline = resolvePipeline(mode, texture);
        return b;
    }

    /** colored batch with an explicit draw mode (fan/strip/lines). */
    public static Batch of(Matrix3x2fc pose, VertexFormat.DrawMode mode) {
        Batch b = new Batch(pose, 3);
        b.pipeline = resolvePipeline(mode, null);
        return b;
    }

    private static RenderPipeline resolvePipeline(VertexFormat.DrawMode mode, @Nullable Identifier texture) {
        if (texture != null) return THRenderLayers.TEXTURED_QUADS;
        return switch (mode) {
            case TRIANGLE_FAN -> THRenderLayers.COLORED_TRIANGLE_FAN;
            case TRIANGLE_STRIP -> THRenderLayers.COLORED_TRIANGLE_STRIP;
            case DEBUG_LINES -> THRenderLayers.COLORED_LINES;
            case DEBUG_LINE_STRIP -> THRenderLayers.COLORED_LINE_STRIP;
            default -> THRenderLayers.COLORED_QUADS;
        };
    }

    /** convenience: submit the batch with a specific mode override */
    public static void submit(Batch batch) {
        batch.submit();
    }
}
