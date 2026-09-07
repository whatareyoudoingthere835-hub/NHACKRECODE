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
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {

    @Shadow
    @Final
    MinecraftClient client;

    @ModifyExpressionValue(method = {"renderWorld"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;")})
    private Perspective hookPerspectiveEventOnCamera(Perspective perspective) {
        PerspectiveEvent class160Var = new PerspectiveEvent(perspective);
        Expensive.INSTANCE.eventDispatcher().dispatch(class160Var);
        return class160Var.perspective();
    }

    @ModifyExpressionValue(method = {"renderHand"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;")})
    private Perspective hookPerspectiveEventOnHand(Perspective perspective) {
        PerspectiveEvent class160Var = new PerspectiveEvent(perspective);
        Expensive.INSTANCE.eventDispatcher().dispatch(class160Var);
        return class160Var.perspective();
    }

    @Inject(method = {"tiltViewWhenHurt"}, at = {@At("HEAD")}, cancellable = true)
    public void tiltViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo callbackInfo) {
        RenderOverlayEvent class252Var = new RenderOverlayEvent(RenderOverlayType.CAMERA_HURT);
        Expensive.INSTANCE.eventDispatcher().dispatch(class252Var);
        if (class252Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = {"renderWorld"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F")})
    private float hookNausea(float f) {
        VisualEffectEvent class258Var = new VisualEffectEvent(VisualEffectType.NAUSEA);
        Expensive.INSTANCE.eventDispatcher().dispatch(class258Var);
        if (class258Var.isCancelled()) {
            return 0.0f;
        }
        return f;
    }

    @ModifyExpressionValue(method = {"getFov"}, at = {@At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I", remap = false)})
    private int hookGetFov(int i) {
        FovEvent class146Var = new FovEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class146Var);
        return class146Var.isCancelled() ? class146Var.getFov() : i;
    }

    @ModifyExpressionValue(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;method_76762(FLnet/minecraft/entity/Entity;)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult hookCrosshairTarget(HitResult hitResult, float tickProgress) {
        Entity entity = this.client.getCameraEntity();
        if (entity == null || this.client.player == null) {
            return hitResult;
        }
        double range = Math.max(this.client.player.getBlockInteractionRange(), this.client.player.getEntityInteractionRange());
        EntityTraceEvent class342Var = new EntityTraceEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class342Var);
        if (class342Var.isCancelled() && hitResult.getType() == HitResult.Type.ENTITY) {
            return entity.raycast(range, tickProgress, false);
        }
        if (entity != MinecraftClient.getInstance().player) {
            return hitResult;
        }
        PlayerRotationManager class398Var = PlayerRotationManager.INSTANCE;
        if (class398Var.getCurrentRotation() == null && (Expensive.INSTANCE.moduleRepository() == null || !((FreeCameraModule) Expensive.INSTANCE.moduleRepository().get(FreeCameraModule.class)).isState())) {
            return hitResult;
        }
        Rotation serverRotation = class398Var.getCurrentRotation() != null ? class398Var.getCurrentRotation() : PlayerRotationManager.INSTANCE.getServerRotation();
        net.minecraft.util.hit.EntityHitResult entityHitResult = WorldRaycastUtils.raytraceEntity(range, serverRotation, e -> e.canHit());
        if (entityHitResult != null) {
            return entityHitResult;
        }
        return WorldRaycastUtils.raycast(range, serverRotation, false);
    }

    @Redirect(method = "getBasicProjectionMatrix", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;perspective(FFFF)Lorg/joml/Matrix4f;"))
    private Matrix4f getBasicProjectionMatrix(Matrix4f matrix, float fovy, float aspect, float zNear, float zFar) {
        if (!((AspectRatioModule) Expensive.INSTANCE.moduleRepository().get(AspectRatioModule.class)).isState() || !Mc.INSTANCE.isWorldLoaded()) {
            return matrix.perspective(fovy, aspect, zNear, zFar);
        }
        AspectRatioEvent class109Var = new AspectRatioEvent(aspect);
        Expensive.INSTANCE.eventDispatcher().dispatch(class109Var);
        return matrix.perspective(fovy, class109Var.getAspectRatio(), zNear, zFar);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(RenderTickCounter renderTickCounter, boolean renderLevel, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new Render2DEvent(new MatrixStack(), Render2DStage.POST, renderTickCounter, null));
    }
}
