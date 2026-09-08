package thunder.hack.utility.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 1.21.11 replacement for the old {@code RenderLayer}/{@code RenderSystem} juggling:
 * every world-space overlay geometry is flushed through a registered {@link RenderLayer}
 * (name + {@link RenderSetup}) instead of manually toggling GL state around a
 * {@code drawWithGlobalProgram} call. Layers are built lazily and cached.
 */
public final class THRenderLayers {

    /* ---------------- gui pipelines (used by Draw2D) ---------------- */

    public static final RenderPipeline COLORED_QUADS = RenderPipelines.GUI;
    public static final RenderPipeline TEXTURED_QUADS = RenderPipelines.GUI_TEXTURED;

    public static final RenderPipeline COLORED_TRIANGLE_FAN = register("gui_colored_fan",
            RenderPipelines.GUI_SNIPPET, VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_FAN);
    public static final RenderPipeline COLORED_TRIANGLE_STRIP = register("gui_colored_strip",
            RenderPipelines.GUI_SNIPPET, VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP);
    public static final RenderPipeline COLORED_LINES = register("gui_colored_lines",
            RenderPipelines.GUI_SNIPPET, VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES);
    public static final RenderPipeline COLORED_LINE_STRIP = register("gui_colored_line_strip",
            RenderPipelines.GUI_SNIPPET, VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINE_STRIP);

    /* ---------------- world layer cache ---------------- */

    private static final Map<Object, RenderLayer> WORLD_CACHE = new HashMap<>();

    private THRenderLayers() {
    }

    private static RenderPipeline register(String path, RenderPipeline.Snippet snippet, VertexFormat format, DrawMode mode) {
        return RenderPipelines.register(RenderPipeline.builder(snippet)
                .withLocation(Identifier.of("thunderhack", "pipeline/" + path))
                .withVertexFormat(format, mode)
                .build());
    }

    private static RenderPipeline worldPipeline(String path, RenderPipeline.Snippet snippet, VertexFormat format, DrawMode mode, boolean blendAdd) {
        return RenderPipelines.register(RenderPipeline.builder(snippet)
                .withLocation(Identifier.of("thunderhack", "pipeline/" + path))
                .withVertexFormat(format, mode)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withDepthWrite(false)
                .withBlend(blendAdd ? BlendFunction.ADDITIVE : BlendFunction.TRANSLUCENT)
                .withCull(false)
                .build());
    }

    private static RenderLayer layer(String path, RenderPipeline pipeline, Identifier texture) {
        RenderSetup.Builder builder = RenderSetup.builder(pipeline);
        if (texture != null) builder.texture("Sampler0", texture);
        return RenderLayer.of("thunderhack." + path, builder.build());
    }

    /** plain POSITION_COLOR geometry (boxes, circles, lines drawn as quads etc.) */
    public static RenderLayer worldColored(DrawMode mode) {
        return WORLD_CACHE.computeIfAbsent("colored" + mode, k ->
                layer(k, worldPipeline(k, RenderPipelines.POSITION_COLOR_SNIPPET, VertexFormats.POSITION_COLOR, mode, false), null));
    }

    /** LINES-style layer (debug lines, outlines) */
    public static RenderLayer worldLines(DrawMode mode) {
        return WORLD_CACHE.computeIfAbsent("lines" + mode, k ->
                layer(k, worldPipeline(k, RenderPipelines.RENDERTYPE_LINES_SNIPPET, VertexFormats.LINES, mode, false), null));
    }

    /** textured + colored geometry bound to {@code texture}; {@code additive} for glow style */
    public static RenderLayer worldTextured(DrawMode mode, Identifier texture, boolean additive) {
        return WORLD_CACHE.computeIfAbsent(texture + "t" + mode + additive, k ->
                layer("tex_" + texture.getPath() + "_" + mode + (additive ? "_add" : ""),
                        worldPipeline("tex_" + texture.getPath() + "_" + mode + (additive ? "_add" : ""),
                                RenderPipelines.POSITION_TEX_COLOR_SNIPPET, VertexFormats.POSITION_TEXTURE_COLOR, mode, additive),
                        texture));
    }

    /** Flush a built BufferBuilder batch through the layer, always releasing it. */
    public static void drawWorld(RenderLayer layer, BuiltBuffer buffer) {
        if (buffer == null) return;
        try {
            layer.draw(buffer);
        } finally {
            buffer.close();
        }
    }

    public static TextureSetup guiTextureSetup(Identifier texture) {
        if (texture == null) return TextureSetup.empty();
        AbstractTexture abstractTexture = MinecraftClient.getInstance().getTextureManager().getTexture(texture);
        if (abstractTexture == null) return TextureSetup.empty();
        return TextureSetup.of(abstractTexture.getGlTextureView(), abstractTexture.getSampler());
    }
}
