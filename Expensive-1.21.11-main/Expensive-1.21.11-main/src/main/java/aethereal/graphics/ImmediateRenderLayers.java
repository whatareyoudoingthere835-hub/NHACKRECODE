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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;

public final class ImmediateRenderLayers {
    private static final Map<String, RenderLayer> CACHE = new ConcurrentHashMap<>();

    private ImmediateRenderLayers() {
    }

    public static void draw(BuiltBuffer buffer, String name, VertexFormat format, VertexFormat.DrawMode mode, Identifier texture, boolean additive, boolean depthTest, boolean cull) {
        String key= name + "_" + format.hashCode() + "_" + mode.name() + "_" + texture + "_" + additive + "_" + depthTest + "_" + cull;
        RenderLayer layer= CACHE.computeIfAbsent(key, ignored -> create(key, format, mode, texture, additive, depthTest, cull));
        layer.draw(buffer);
    }

    private static RenderLayer create(String key, VertexFormat format, VertexFormat.DrawMode mode, Identifier texture, boolean additive, boolean depthTest, boolean cull) {
        RenderPipeline.Snippet snippet = texture == null ? RenderPipelines.POSITION_COLOR_SNIPPET : RenderPipelines.POSITION_TEX_COLOR_SNIPPET;
        RenderPipeline pipeline= RenderPipeline.builder(snippet)
            .withLocation(Identifier.of("expensive", "immediate/" + Integer.toUnsignedString(key.hashCode(), 16)))
            .withVertexFormat(format, mode)
            .withCull(cull)
            .withDepthWrite(depthTest)
            .withDepthTestFunction(depthTest ? DepthTestFunction.LEQUAL_DEPTH_TEST : DepthTestFunction.NO_DEPTH_TEST)
            .withBlend(additive ? BlendFunction.ADDITIVE : BlendFunction.TRANSLUCENT)
            .build();
        RenderSetup.Builder setup = RenderSetup.builder(pipeline).translucent();
        if (texture != null) {
            setup.texture("Sampler0", texture);
        }
        return RenderLayer.of("expensive_immediate_" + Integer.toUnsignedString(key.hashCode(), 16), setup.build());
    }
}
