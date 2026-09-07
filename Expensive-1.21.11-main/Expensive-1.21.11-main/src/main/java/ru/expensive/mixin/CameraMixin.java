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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public abstract class CameraMixin {

    @Shadow
    @Final
    private BlockPos.Mutable blockPos;

    @Shadow
    private float yaw;

    @Shadow
    private float pitch;

    @Shadow
    private boolean thirdPerson;

    @Unique
    private float pitchAnim;

    @Shadow
    public void setRotation(float f, float f2) {
    }

    @Shadow
    protected void moveBy(float f, float f2, float f3) {
    }

    @Shadow
    protected abstract float clipToSpace(float f);

    @Inject(method = {"update"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER)}, cancellable = true)
    private void updateHook(World world, Entity entity, boolean z, boolean z2, float f, CallbackInfo callbackInfo) {
        CameraDistanceEvent class140Var = new CameraDistanceEvent(z2 ? -this.pitch : this.pitch);
        Expensive.INSTANCE.eventDispatcher().dispatch(class140Var);
        if (class140Var.isCancelled() && (entity instanceof ClientPlayerEntity)) {
            setRotation(this.yaw + (180.0f * class140Var.getFrontAnim().smoothAnimation()), class140Var.getPitch());
            moveBy(-clipToSpace(class140Var.getDistance()), 0.0f, 0.0f);
            if (((LivingEntity) entity).isSleeping() && !z) {
                Direction sleepingDirection = ((LivingEntity) entity).getSleepingDirection();
                setRotation(sleepingDirection != null ? sleepingDirection.getPositiveHorizontalDegrees() - 180.0f : 0.0f, 0.0f);
                moveBy(0.0f, 0.3f, 0.0f);
            }
            callbackInfo.cancel();
        }
    }

    @Redirect(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1))
    private void update(Camera camera, float f, float f2) {
        CameraRotationEvent class152Var = new CameraRotationEvent(f, f2);
        Expensive.INSTANCE.eventDispatcher().dispatch(class152Var);
        PlayerRotationManager class398Var = PlayerRotationManager.INSTANCE;
        ScheduledRotation currentStrategy = class398Var.getCurrentStrategy();
        if (currentStrategy == null || !currentStrategy.clientRotation()) {
            if (class152Var.isCancelled()) {
                camera.setRotation(class152Var.getYaw(), class152Var.getPitch());
                return;
            } else {
                camera.setRotation(f, f2);
                return;
            }
        }
        Rotation previousRotation = class398Var.getPreviousRotation();
        Rotation currentRotation = class398Var.getCurrentRotation();
        if (previousRotation != null && currentRotation != null) {
            float tickDelta = Mc.INSTANCE.getTickDelta();
            camera.setRotation(MathHelper.lerp(tickDelta, previousRotation.getYaw(), currentRotation.getYaw()), MathHelper.lerp(tickDelta, previousRotation.getPitch(), currentRotation.getPitch()));
        } else if (class152Var.isCancelled()) {
            camera.setRotation(class152Var.getYaw(), class152Var.getPitch());
        } else {
            camera.setRotation(f, f2);
        }
    }

    @Inject(method = {"update"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER)})
    private void update(World world, Entity entity, boolean z, boolean z2, float f, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new EntityRenderEvent3(entity, f));
    }

    @Inject(method = {"clipToSpace"}, at = {@At("HEAD")}, cancellable = true)
    private void clipToSpace(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
        CameraClipEvent class209Var = new CameraClipEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class209Var);
        if (class209Var.isCancelled()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(f));
            callbackInfoReturnable.cancel();
        }
    }
}
