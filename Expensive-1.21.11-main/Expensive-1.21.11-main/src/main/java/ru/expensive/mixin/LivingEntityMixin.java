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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {

    @Unique
    private JumpEvent lastJumpEvent;

    @Shadow
    public float bodyYaw;

    @Shadow
    public abstract void setSprinting(boolean z);

    @Shadow
    protected abstract float getMaxRelativeHeadRotation();

    @Inject(method = {"jump"}, at = {@At("HEAD")})
    private void jump(CallbackInfo callbackInfo) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof ClientPlayerEntity) {
            JumpEvent class237Var = new JumpEvent(livingEntity.getYaw());
            Expensive.INSTANCE.eventDispatcher().dispatch(class237Var);
            this.lastJumpEvent = class237Var;
        }
    }

    @Inject(method = {"getHandSwingDuration"}, at = {@At("HEAD")}, cancellable = true)
    private void getHandSwingDuration(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        HandSwingEvent class087Var = new HandSwingEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class087Var);
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (class087Var.isCancelled() && (livingEntity instanceof ClientPlayerEntity)) {
            callbackInfoReturnable.setReturnValue(Integer.valueOf((int) class087Var.swingSpeed()));
        }
    }

    @Inject(method = {"isGliding"}, at = {@At("RETURN")})
    public void onTickGliding(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (((LivingEntity) (Object) this) instanceof ClientPlayerEntity) {
            Expensive.INSTANCE.eventDispatcher().dispatch(new GlideStateEvent(((Boolean) callbackInfoReturnable.getReturnValue()).booleanValue()));
        }
    }

    @Inject(method = {"isPushable"}, at = {@At("HEAD")}, cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        PushEvent class231Var = new PushEvent(PushType.PLAYERS);
        Expensive.INSTANCE.eventDispatcher().dispatch(class231Var);
        if ((((LivingEntity) (Object) this) instanceof PlayerEntity) && class231Var.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = {"calcGlidingVelocity"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F")})
    private float hookModifyFallFlyingPitch(float f) {
        if (!(((LivingEntity) (Object) this) instanceof ClientPlayerEntity)) {
            return f;
        }
        Rotation currentRotation = PlayerRotationManager.INSTANCE.getCurrentRotation();
        return (currentRotation == null || PlayerRotationManager.INSTANCE.getCurrentStrategy() == null || !PlayerRotationManager.INSTANCE.getCurrentStrategy().moveCorrection()) ? f : currentRotation.getPitch();
    }

    @ModifyExpressionValue(method = {"calcGlidingVelocity"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;")})
    private Vec3d hookModifyFallFlyingRotationVector(Vec3d vec3d) {
        if (!(((LivingEntity) (Object) this) instanceof ClientPlayerEntity)) {
            return vec3d;
        }
        Rotation currentRotation = PlayerRotationManager.INSTANCE.getCurrentRotation();
        return (currentRotation == null || PlayerRotationManager.INSTANCE.getCurrentStrategy() == null || !PlayerRotationManager.INSTANCE.getCurrentStrategy().moveCorrection()) ? vec3d : currentRotation.getDirectionVector();
    }

    @ModifyExpressionValue(method = {"jump"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F")})
    private float getYaw(float f) {
        return (this.lastJumpEvent == null || !(((LivingEntity) (Object) this) instanceof ClientPlayerEntity)) ? f : this.lastJumpEvent.getYaw();
    }

    @Inject(method = {"hasStatusEffect"}, at = {@At("HEAD")}, cancellable = true)
    public void hasStatusEffect(RegistryEntry<StatusEffect> registryEntry, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        StatusEffectEvent class046Var = new StatusEffectEvent((StatusEffect) registryEntry.value());
        Expensive.INSTANCE.eventDispatcher().dispatch(class046Var);
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (class046Var.isCancelled() && (livingEntity instanceof ClientPlayerEntity)) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = {"turnHead"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;wrapDegrees(F)F", ordinal = 1)})
    private float turnHeadHook(float original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original;
        }
        Rotation rotation = PlayerRotationManager.INSTANCE.getCurrentRotation();
        return rotation == null ? original : MathHelper.wrapDegrees(rotation.getYaw() - this.bodyYaw);
    }
}
