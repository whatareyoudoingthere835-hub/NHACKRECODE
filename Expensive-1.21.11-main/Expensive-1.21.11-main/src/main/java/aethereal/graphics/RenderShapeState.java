package aethereal.graphics;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class RenderShapeState {
    public static boolean textureEnabled;
    public static final RenderShapeState POSITION_COLOR_TRIANGLES = new RenderShapeState(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR, false, false, false, false, 1.0f, null);
    public static final RenderShapeState POSITION_COLOR_QUADS = new RenderShapeState(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, false, false, false, false, 1.0f, null);
    public static final RenderShapeState POSITION_COLOR_LINES = new RenderShapeState(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR, false, false, false, false, 1.0f, null);

    private static final java.util.Map<String, RenderShapeState> TEXTURE_QUAD_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    public static RenderShapeState getTextureQuadState(Identifier texture, boolean depthTest, boolean blend, boolean additiveBlend) {
        String key= texture + "_" + depthTest + "_" + blend + "_" + additiveBlend;
        return TEXTURE_QUAD_CACHE.computeIfAbsent(key, k -> new RenderShapeState(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR, blend, additiveBlend, depthTest, false, 1.0f, texture));
    }

    private final VertexFormat.DrawMode mode;
    private final VertexFormat format;
    private final boolean blend;
    private final boolean additiveBlend;
    private final boolean depthTest;
    private final boolean cull;
    private final float lineWidth;
    private final Identifier texture;
    private final RenderLayer layer;

    public RenderShapeState() {
        this(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR, true, false, true, false, 1.0f, null);
    }

    public RenderShapeState(VertexFormat.DrawMode mode, VertexFormat format, boolean blend, boolean additiveBlend, boolean depthTest, boolean cull, float lineWidth, Identifier texture) {
        this.mode = mode != null ? mode : VertexFormat.DrawMode.TRIANGLES;
        this.format = format != null ? format : VertexFormats.POSITION_COLOR;
        this.blend = blend;
        this.additiveBlend = additiveBlend;
        this.depthTest = depthTest;
        this.cull = cull;
        this.lineWidth = lineWidth;
        this.texture = texture;
        RenderPipeline.Snippet snippet = texture == null ? RenderPipelines.POSITION_COLOR_SNIPPET : RenderPipelines.POSITION_TEX_COLOR_SNIPPET;
        String key= this.mode.name().toLowerCase() + "_" + Integer.toUnsignedString(hashCode(), 16);
        RenderPipeline.Builder builder = RenderPipeline.builder(snippet)
            .withLocation(Identifier.of("expensive", "shape/" + key))
            .withVertexFormat(this.format, this.mode)
            .withCull(this.cull)
            .withDepthWrite(!this.blend && this.depthTest)
            .withDepthTestFunction(this.depthTest ? DepthTestFunction.LEQUAL_DEPTH_TEST : DepthTestFunction.NO_DEPTH_TEST);
        if (this.blend) {
            builder.withBlend(this.additiveBlend ? BlendFunction.ADDITIVE : BlendFunction.TRANSLUCENT);
        } else {
            builder.withoutBlend();
        }
        RenderSetup.Builder setup = RenderSetup.builder(builder.build());
        if (texture != null) {
            setup.texture("Sampler0", texture);
        }
        this.layer = RenderLayer.of("expensive_shape_" + key, setup.build());
    }

    public boolean additiveBlend() { return additiveBlend; }
    public boolean blend() { return blend; }
    public boolean cull() { return cull; }
    public boolean depthTest() { return depthTest; }
    public VertexFormat format() { return format; }
    public float lineWidth() { return lineWidth; }
    public VertexFormat.DrawMode mode() { return mode; }
    public Identifier texture() { return texture; }
    public RenderLayer layer() { return layer; }
}
