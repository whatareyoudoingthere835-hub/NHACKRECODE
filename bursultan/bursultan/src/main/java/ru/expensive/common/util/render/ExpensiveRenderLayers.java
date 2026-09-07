package ru.expensive.common.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExpensiveRenderLayers {
    private ExpensiveRenderLayers() {
    }

    private static RenderPipeline coloredPipeline;
    private static RenderPipeline texturedPipeline;
    private static RenderLayer coloredGui;
    private static final Map<Identifier, RenderLayer> TEXTURED_GUI = new ConcurrentHashMap<>();
    private static ProjectionMatrix2 guiProjection;

    private record QueuedDraw(RenderLayer layer, BuiltBuffer buffer) {
    }

    private static final List<QueuedDraw> GUI_QUEUE = new ArrayList<>();
    private static int guiFrameDepth = 0;

    public static RenderPipeline coloredPipeline() {
        if (coloredPipeline == null) {
            coloredPipeline = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of("expensive", "gui_color"))
                    .withCull(false)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build());
        }
        return coloredPipeline;
    }

    public static RenderPipeline texturedPipeline() {
        if (texturedPipeline == null) {
            texturedPipeline = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
                    .withLocation(Identifier.of("expensive", "gui_textured"))
                    .withCull(false)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build());
        }
        return texturedPipeline;
    }

    public static RenderLayer coloredGui() {
        if (coloredGui == null) {
            coloredGui = RenderLayer.of("expensive_gui_color",
                    RenderSetup.builder(coloredPipeline()).build());
        }
        return coloredGui;
    }

    public static RenderLayer texturedGui(Identifier texture) {
        return TEXTURED_GUI.computeIfAbsent(texture, id -> RenderLayer.of(
                "expensive_gui_textured/" + id.getNamespace() + "/" + id.getPath().replace('/', '_'),
                RenderSetup.builder(texturedPipeline()).texture("Sampler0", id).build()));
    }

    public static void applyGuiProjection() {
        if (guiProjection == null) {
            guiProjection = new ProjectionMatrix2("expensive_gui", -20000.0F, 20000.0F, true);
        }
        Window window = MinecraftClient.getInstance().getWindow();
        float scale = (float) window.getScaleFactor();
        RenderSystem.setProjectionMatrix(
                guiProjection.set((float) window.getFramebufferWidth() / scale,
                                  (float) window.getFramebufferHeight() / scale),
                ProjectionType.ORTHOGRAPHIC);
    }

    public static void beginGuiFrame() {
        if (guiFrameDepth == 0) {
            RenderSystem.backupProjectionMatrix();
            RenderSystem.getModelViewStack().pushMatrix();
            RenderSystem.getModelViewStack().identity();
            applyGuiProjection();
        }
        guiFrameDepth++;
    }

    public static void endGuiFrame() {
        if (guiFrameDepth > 0) {
            guiFrameDepth--;
            if (guiFrameDepth == 0) {
                flushGuiQueue();
                RenderSystem.getModelViewStack().popMatrix();
                RenderSystem.restoreProjectionMatrix();
            }
        }
    }

    public static void submitGuiOrDraw(RenderLayer layer, BuiltBuffer buffer) {
        if (guiFrameDepth == 0) {
            RenderSystem.backupProjectionMatrix();
            RenderSystem.getModelViewStack().pushMatrix();
            RenderSystem.getModelViewStack().identity();
            try {
                applyGuiProjection();
                layer.draw(buffer);
            } finally {
                RenderSystem.getModelViewStack().popMatrix();
                RenderSystem.restoreProjectionMatrix();
            }
        } else {
            layer.draw(buffer);
        }
    }

    public static void flushGuiQueue() {
        if (GUI_QUEUE.isEmpty()) {
            return;
        }
        for (QueuedDraw draw : GUI_QUEUE) {
            draw.layer().draw(draw.buffer());
        }
        GUI_QUEUE.clear();
    }
}
