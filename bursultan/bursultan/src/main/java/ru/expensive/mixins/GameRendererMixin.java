package ru.expensive.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.core.Extra;
import ru.expensive.implement.features.modules.render.AspectRatioModule;
import ru.expensive.implement.features.modules.render.ClearRenderModule;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    MinecraftClient client;

    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrixHook(float fov, CallbackInfoReturnable<Matrix4f> cir) {
        Extra extra = Extra.getInstance();
        if (extra == null) return;
        AspectRatioModule aspectRatioModule = (AspectRatioModule) extra.getModuleProvider().module("AspectRatio");
        if (aspectRatioModule != null && aspectRatioModule.isState()) {
            aspectRatioModule.updateAspectRatio();
            float farPlane = ((GameRenderer) (Object) this).getFarPlaneDistance();
            cir.setReturnValue(new Matrix4f()
                    .perspective((float) (fov * 0.01745329238474369), aspectRatioModule.getRatio(), 0.05f, farPlane));
        }
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void bobViewWhenHurt(MatrixStack matrixStack, float float_1, CallbackInfo ci) {
        Extra extra = Extra.getInstance();
        if (extra == null) return;
        ClearRenderModule clearRenderModule = (ClearRenderModule) extra.getModuleProvider().module("ClearRender");
        if (clearRenderModule != null && clearRenderModule.isState() && clearRenderModule.getClearRenderSettings().isSelected("HurtCam")) {
            ci.cancel();
        }
    }
}
