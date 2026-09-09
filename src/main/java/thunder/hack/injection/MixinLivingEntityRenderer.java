package thunder.hack.injection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import thunder.hack.utility.render.PoseStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.injection.accesors.IClientPlayerEntity;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

import java.util.List;

import static thunder.hack.features.modules.Module.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<?>> {
    private LivingEntity lastEntity;
    private float lastTickDelta;

    private float originalHeadYaw, originalPrevHeadYaw, originalPrevHeadPitch, originalHeadPitch;

    @Shadow
    protected M model;

    @Shadow
    @Final
    protected List<FeatureRenderer<T, M>> features;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("HEAD"), require = 0)
    public void onUpdatePre(T livingEntity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
        lastEntity = livingEntity;
        lastTickDelta = tickDelta;
        if (Module.fullNullCheck()) return;
        if (mc.player != null && livingEntity == mc.player && mc.player.getControllingVehicle() == null && ClientSettings.renderRotations.getValue() && !ThunderHack.isFuturePresent()) {
            originalHeadYaw = livingEntity.getHeadYaw();
            originalPrevHeadYaw = livingEntity.getHeadYaw();
            originalPrevHeadPitch = livingEntity.getPitch();
            originalHeadPitch = livingEntity.getPitch();

            livingEntity.changeLookDirection(0, (Managers.PLAYER.lastPitch) - livingEntity.getPitch());
            livingEntity.setHeadYaw(Managers.PLAYER.lastYaw);
            livingEntity.setBodyYaw(Render2DEngine.interpolateFloat(Managers.PLAYER.prevBodyYaw, Managers.PLAYER.bodyYaw, Render3DEngine.getTickDelta(false)));
        }
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"), require = 0)
    public void onUpdatePost(T livingEntity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        postRender(livingEntity);
        if (livingEntity != mc.player && ModuleManager.freeCam.isEnabled() && ModuleManager.freeCam.track.getValue() && ModuleManager.freeCam.trackEntity != null && ModuleManager.freeCam.trackEntity == livingEntity) {
            state.invisible = true;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/PoseStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true, require = 0)
    public void onRenderPre(LivingEntityRenderState state, PoseStack matrixStack, OrderedRenderCommandQueue renderQueue, CameraRenderState camera, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if (!(lastEntity instanceof PlayerEntity pe)) return;

        if (ModuleManager.chams.isEnabled() && ModuleManager.chams.players.getValue()) {
            ModuleManager.chams.renderPlayer(pe, lastTickDelta, state, matrixStack, 15728880, model, ci, () -> {});
            if (!pe.isSpectator()) {
                matrixStack.push();
                for (FeatureRenderer<T, M> featureRenderer : features) {
                    //noinspection unchecked,rawtypes
                    ((FeatureRenderer) featureRenderer).render(matrixStack, renderQueue, 15728880, state, 0f, 0f);
                }
                matrixStack.pop();
            }
        }
    }

    @Unique
    public void postRender(T livingEntity) {
        if (Module.fullNullCheck()) return;
        if (mc.player != null && livingEntity == mc.player && mc.player.getControllingVehicle() == null && ClientSettings.renderRotations.getValue() && !ThunderHack.isFuturePresent()) {
            livingEntity.changeLookDirection(0, (originalHeadPitch) - livingEntity.getPitch());
            livingEntity.setHeadYaw(originalHeadYaw);
            livingEntity.setBodyYaw(Managers.PLAYER.bodyYaw);
        }
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/PoseStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/PoseStack;Lnet/minecraft/client/render/VertexConsumer;III)V", require = 0), require = 0)
    private void renderHook(Args args) {
        if (Module.fullNullCheck() || !(lastEntity instanceof PlayerEntity pl)) return;

        float alpha = -1f;

        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.antiPlayerCollision.getValue() && lastEntity != mc.player && !pl.isInvisible())
            alpha = MathUtility.clamp((float) (mc.player.squaredDistanceTo(new Vec3d(lastEntity.getX(), lastEntity.getY(), lastEntity.getZ())) / 3f) + 0.2f, 0f, 1f);

        if (lastEntity != mc.player && pl.isInvisible() && ModuleManager.serverHelper.isEnabled() && ModuleManager.serverHelper.trueSight.getValue())
            alpha = 0.3f;

        if (alpha != -1)
            args.set(5, Render2DEngine.applyOpacity(0x26FFFFFF, alpha));
    }
}
