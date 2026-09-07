package ru.expensive.api.system.shader.implement;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.Camera;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import ru.expensive.common.QuickImports;
import ru.expensive.mixins.accessors.GameRendererAccessor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Рисует анимированное небо полноэкранным треугольником поверх мира.
 * Маскирование по небу делает depth-тест (LEQUAL на дальнюю плоскость):
 * проходят только пиксели с глубиной ~1.0, т.е. небо.
 * Анимация (время, режим, поворот камеры) передаётся через UBO SkyData,
 * т.к. скалярные uniform'ы в пайплайнах 1.21.11 per-frame не задать.
 */
public class SkyShaderRenderer implements QuickImports {
    private static final int UBO_SIZE = 64;

    private RenderPipeline pipeline;
    private GpuBuffer triangleBuffer;
    private GpuBuffer uboBuffer;
    private boolean pipelineFailed;
    private boolean failureReported;

    public void render(float[] tintRgba, float strength, float speed, float mode) {
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) {
            return;
        }
        // На FABULOUS мир собирается из нескольких таргетов — глубина
        // главного таргета не соответствует картинке, небо ляжет криво.
        if (mc.options.getPreset().getValue() == GraphicsMode.FABULOUS) {
            return;
        }

        Framebuffer framebuffer = mc.getFramebuffer();
        if (framebuffer == null || framebuffer.getColorAttachmentView() == null
                || framebuffer.getDepthAttachmentView() == null) {
            return;
        }

        ensurePipeline();
        ensureBuffers();
        if (pipeline == null || triangleBuffer == null || uboBuffer == null) {
            if (pipelineFailed && !failureReported && mc.player != null) {
                failureReported = true;
                logDirect("SkyShader: не удалось создать пайплайн, небо отключено", net.minecraft.util.Formatting.RED);
            }
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        Quaternionf rotation = camera.getRotation();
        float fov = ((GameRendererAccessor) mc.gameRenderer)
                .invokeGetFov(camera, mc.getRenderTickCounter().getTickProgress(true), true);
        float aspect = (float) mc.getWindow().getFramebufferWidth()
                / (float) Math.max(1, mc.getWindow().getFramebufferHeight());

        ByteBuffer data = ByteBuffer.allocateDirect(UBO_SIZE).order(ByteOrder.nativeOrder());
        data.putFloat(tintRgba[0]).putFloat(tintRgba[1]).putFloat(tintRgba[2]).putFloat(tintRgba[3]);
        data.putFloat((System.currentTimeMillis() % 3600000L) / 1000.0F);
        data.putFloat(strength).putFloat(speed).putFloat(mode);
        data.putFloat(rotation.x).putFloat(rotation.y).putFloat(rotation.z).putFloat(rotation.w);
        data.putFloat((float) Math.tan(Math.toRadians(fov / 2.0F))).putFloat(aspect);
        data.putFloat(0.0F).putFloat(0.0F);
        data.flip();

        GpuBufferSlice uboSlice = uboBuffer.slice();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(uboSlice, data);

        // Глубина в этот момент ещё цела (событие стреляет в хвосте
        // WorldRenderer.render, до ванильной чистки под руку).
        RenderPass pass = encoder.createRenderPass(() -> "expensive sky",
                framebuffer.getColorAttachmentView(), OptionalInt.empty(),
                framebuffer.getDepthAttachmentView(), OptionalDouble.empty());
        pass.setPipeline(pipeline);
        pass.setUniform("SkyData", uboSlice);
        pass.setVertexBuffer(0, triangleBuffer);
        pass.draw(0, 3);
        pass.close();
    }

    private void ensurePipeline() {
        if (pipeline != null || pipelineFailed) {
            return;
        }
        try {
            pipeline = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
                    .withLocation(Identifier.of("expensive", "sky"))
                    .withVertexShader(Identifier.of("minecraft", "core/sky/sky_shader"))
                    .withFragmentShader(Identifier.of("minecraft", "core/sky/sky_shader"))
                    .withUniform("SkyData", UniformType.UNIFORM_BUFFER)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
                    .build());
        } catch (Exception e) {
            pipelineFailed = true;
        }
    }

    private void ensureBuffers() {
        if (triangleBuffer != null && uboBuffer != null) {
            return;
        }
        try {
            // POSITION_TEXTURE_COLOR: xyz + uv + rgba(ubyte4). Цвет/uv не используются.
            ByteBuffer triangle = ByteBuffer.allocateDirect(3 * 24).order(ByteOrder.nativeOrder());
            putVertex(triangle, -1.0F, -1.0F);
            putVertex(triangle, 3.0F, -1.0F);
            putVertex(triangle, -1.0F, 3.0F);
            triangle.flip();
            triangleBuffer = RenderSystem.getDevice().createBuffer(() -> "expensive sky triangle",
                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, triangle);
            uboBuffer = RenderSystem.getDevice().createBuffer(() -> "expensive sky ubo",
                    GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, UBO_SIZE);
        } catch (Exception e) {
            triangleBuffer = null;
            uboBuffer = null;
        }
    }

    private static void putVertex(ByteBuffer buffer, float x, float y) {
        buffer.putFloat(x).putFloat(y).putFloat(0.0F);
        buffer.putFloat(0.0F).putFloat(0.0F);
        buffer.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) 255);
    }
}
