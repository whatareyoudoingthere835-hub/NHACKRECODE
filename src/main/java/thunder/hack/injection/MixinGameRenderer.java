package thunder.hack.injection;

import net.minecraft.client.render.RenderTickCounter;
import thunder.hack.core.Managers;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import thunder.hack.utility.render.PoseStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.player.NoEntityTrace;
import thunder.hack.utility.math.FrameRateCounter;
import thunder.hack.utility.render.BlockAnimationUtility;
import thunder.hack.utility.render.Render3DEngine;

import static thunder.hack.features.modules.Module.mc;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract float getViewDistanceBlocks();

    @Inject(at = @At(value = "TAIL"), method = "render")
    void postHudRenderHook(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        FrameRateCounter.INSTANCE.recordFrame();
    }

    @Inject(at = @At(value = "TAIL"), method = "renderWorld")
    void render3dHook(RenderTickCounter tickCounter, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;

        Camera camera = mc.net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera();
        float tickDelta = mc.getRenderTickCounter().getTickDelta(false);

        Matrix4f rotations = new Matrix4f()
                .rotateX(camera.getPitch() * MathHelper.RADIANS_PER_DEGREE)
                .rotateY(camera.getYaw() * MathHelper.RADIANS_PER_DEGREE + (float) Math.PI);

        Render3DEngine.lastProjMat.set(((thunder.hack.injection.accesors.IGameRenderer) mc.gameRenderer).th$projectionMatrix());
        Render3DEngine.lastModMat.set(rotations);
        Render3DEngine.lastWorldSpaceMatrix.set(rotations);

        PoseStack matrixStack = new PoseStack();
        Managers.MODULE.onRender3D(matrixStack);
        BlockAnimationUtility.onRender(matrixStack);
        Render3DEngine.onRender3D(matrixStack); // <- не двигать
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float renderWorldHook(float delta, float first, float second) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.nausea.getValue()) return 0;
        return MathHelper.lerp(delta, first, second);
    }

    @Inject(method = "updateCrosshairTarget", at = @At(value = "HEAD"), cancellable = true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (Module.fullNullCheck()) return;

        if (ModuleManager.freeCam.isEnabled()) {
            info.cancel();
            mc.crosshairTarget = Managers.PLAYER.getRtxTarget(ModuleManager.freeCam.getFakeYaw(), ModuleManager.freeCam.getFakePitch(), ModuleManager.freeCam.getFakeX(), ModuleManager.freeCam.getFakeY(), ModuleManager.freeCam.getFakeZ());
        }
    }

    // vanilla "findCrosshairTarget" is gone - the no-entity-trace override is applied on top of the
    // result the same way: plain player raycast clamped to the block interaction range
    @Inject(method = "updateCrosshairTarget", at = @At(value = "TAIL"))
    private void findCrosshairTargetHook(float tickDelta, CallbackInfo ci) {
        if (mc.player == null) return;
        ItemStack mainHand = mc.player.getMainHandStack();
        if (ModuleManager.noEntityTrace.isEnabled() && (mainHand.isIn(ItemTags.PICKAXES) || !NoEntityTrace.ponly.getValue())) {
            if (mainHand.isIn(ItemTags.SWORDS) && NoEntityTrace.noSword.getValue()) return;
            double d = Math.max(mc.player.getBlockInteractionRange(), 4.5);
            Vec3d vec3d = mc.player.getCameraPosVec(tickDelta);
            HitResult hitResult = mc.player.raycast(d, tickDelta, false);
            mc.crosshairTarget = ensureTargetInRangeCustom(hitResult, vec3d, mc.player.getBlockInteractionRange());
        }
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrixHook(float fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (ModuleManager.aspectRatio.isEnabled()) {
            cir.setReturnValue(new Matrix4f().setPerspective((float) (fov * 0.01745329238474369), ModuleManager.aspectRatio.ratio.getValue(), 0.05f, getViewDistanceBlocks() * 4.0f));
        }
    }

    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F", at = @At("TAIL"), cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cb) {
        if (ModuleManager.fov.isEnabled()) {
            if (cb.getReturnValue() == 70f && !ModuleManager.fov.itemFov.getValue() && mc.options.getPerspective() != Perspective.FIRST_PERSON)
                return;

            else if (ModuleManager.fov.itemFov.getValue() && cb.getReturnValue() == 70f) {
                cb.setReturnValue(ModuleManager.fov.itemFovModifier.getValue().floatValue());
                return;
            }

            if (mc.player.isSubmergedInWater())
                return;

            cb.setReturnValue(ModuleManager.fov.fovModifier.getValue().floatValue());
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobViewHook(PoseStack matrices, float tickDelta, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if (ModuleManager.noBob.isEnabled()) {
            ModuleManager.noBob.bobView(matrices, tickDelta);
            ci.cancel();
            return;
        }
        if (ClientSettings.customBob.getValue()) {
            ThunderHack.core.bobView(matrices, tickDelta);
            ci.cancel();
        }
    }

    @Unique
    private HitResult ensureTargetInRangeCustom(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void showFloatingItemHook(ItemStack floatingItem, CallbackInfo info) {
        if (ModuleManager.totemAnimation.isEnabled()) {
            ModuleManager.totemAnimation.showFloatingItem(floatingItem);
            info.cancel();
        }
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void tiltViewWhenHurtHook(PoseStack matrices, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.hurtCam.getValue())
            ci.cancel();
    }
}
