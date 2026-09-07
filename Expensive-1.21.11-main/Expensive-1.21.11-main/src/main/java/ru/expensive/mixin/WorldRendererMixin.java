package ru.expensive.mixin;
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

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class WorldRendererMixin {

    @Unique
    private Frustum expensive$frustum;

    @Redirect(method = "fillEntityRenderStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"))
    private boolean hookRenderEntityFromAllPerspectives(LivingEntity livingEntity) {
        EntityRenderEvent2 class377Var = new EntityRenderEvent2(livingEntity);
        Expensive.INSTANCE.eventDispatcher().dispatch(class377Var);
        if (class377Var.isCancelled()) {
            return true;
        }
        return livingEntity.isSleeping();
    }

    @Inject(method = "renderMain", at = @At("HEAD"))
    private void captureFrustum(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Matrix4f matrix, GpuBufferSlice fogBuffer, boolean renderBlockOutline, WorldRenderState state, RenderTickCounter renderTickCounter, Profiler profiler, CallbackInfo callbackInfo) {
        this.expensive$frustum = frustum;
    }

    @Inject(method = {"render"}, at = {@At("TAIL")}, require = 0)
    private void onRender(ObjectAllocator objectAllocator, RenderTickCounter renderTickCounter, boolean z, Camera camera, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, GpuBufferSlice fogBuffer, Vector4f vector4f, boolean z2, CallbackInfo callbackInfo) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiplyPositionMatrix(matrix4f);
        Expensive.INSTANCE.eventDispatcher().dispatch(new WorldRenderEvent(matrixStack, matrix4f, matrix4f2, renderTickCounter, this.expensive$frustum));
    }
}
