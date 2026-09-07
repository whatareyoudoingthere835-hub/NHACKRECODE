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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<S extends LivingEntityRenderState> {
    @Shadow
    protected abstract @Nullable RenderLayer getRenderLayer(LivingEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline);

    @Redirect(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer modifyRenderLayer(LivingEntityRenderer<?, ?, ?> renderer, LivingEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        ChamsModule module = Expensive.INSTANCE.moduleRepository().get(ChamsModule.class);
        if (module.isState() && module.shouldRender(module.currentEntity())) {
            translucent = true;
        }
        return getRenderLayer(state, showBody, translucent, showOutline);
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;mix(II)I"))
    private int modifyModelColor(int original) {
        ChamsModule module = Expensive.INSTANCE.moduleRepository().get(ChamsModule.class);
        return module.isState() && module.shouldRender(module.currentEntity()) ? module.colorSetting().getColor() : original;
    }

    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F"))
    private float lerpAngleDegreesHook(float original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) float delta) {
        if (!entity.equals(MinecraftClient.getInstance().player)) {
            return original;
        }
        PlayerRotationManager manager = PlayerRotationManager.INSTANCE;
        return MathHelper.lerpAngleDegrees(delta, manager.getPreviousRotation().getYaw(), manager.getCurrentRotation().getYaw());
    }

    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getLerpedPitch(F)F"))
    private float getLerpedPitchHook(float original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) float delta) {
        if (!entity.equals(MinecraftClient.getInstance().player)) {
            return original;
        }
        PlayerRotationManager manager = PlayerRotationManager.INSTANCE;
        return MathHelper.lerp(delta, manager.getPreviousRotation().getPitch(), manager.getCurrentRotation().getPitch());
    }
}
