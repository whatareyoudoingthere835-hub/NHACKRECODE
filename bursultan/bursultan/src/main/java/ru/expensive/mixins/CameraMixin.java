package ru.expensive.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.system.animation.implement.FastAnimation;
import ru.expensive.core.Extra;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationPlan;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;
import ru.expensive.implement.features.modules.render.CustomCameraModule;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Invoker("clipToSpace")
    abstract float invokeClipToSpace(float desiredCameraDistance);

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"))
    private float hookCameraDistance(Camera instance, float originalDistance) {
        if (Extra.getInstance() != null) {
            CustomCameraModule customCamera = (CustomCameraModule) Extra.getInstance()
                    .getModuleProvider().module("CustomCamera");
            if (customCamera != null && customCamera.isState()) {
                float distance = customCamera.getAnimatedDistance();
                if (customCamera.getNoClip().isValue()) {
                    return distance;
                }
                return invokeClipToSpace(distance);
            }
        }
        return invokeClipToSpace(originalDistance);
    }
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    private void injectQuickPerspectiveSwap(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        Camera self = (Camera) (Object) this;
        RotationController rotationController = RotationController.INSTANCE;
        RotationPlan rotationPlan = rotationController.getCurrentRotationPlan();
        Angle previousAngle = rotationController.getPreviousAngle();
        Angle currentAngle = rotationController.getCurrentAngle();

        boolean shouldModifyRotation = rotationPlan != null && rotationPlan.isChangeLook();

        if (currentAngle == null || previousAngle == null || !shouldModifyRotation) {
            return;
        }

        try {
            var setRotationMethod = Camera.class.getDeclaredMethod("setRotation", float.class, float.class);
            setRotationMethod.setAccessible(true);
            setRotationMethod.invoke(self,
                    MathHelper.lerp(tickDelta, previousAngle.getYaw(), currentAngle.getYaw()),
                    MathHelper.lerp(tickDelta, previousAngle.getPitch(), currentAngle.getPitch())
            );
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void updateAnimation(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        CustomCameraModule customCamera = (CustomCameraModule) Extra.getInstance()
                .getModuleProvider().module("CustomCamera");

        if (customCamera != null && customCamera.isState()) {
            FastAnimation animation = customCamera.getCameraAnimation();
            if (thirdPerson != (animation.getCurrent() > 0.5f)) {
                animation.setDirection(FastAnimation.getDirection(thirdPerson));
            }
        }
    }
}
