package thunder.hack.injection;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import thunder.hack.utility.render.PoseStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.client.ModuleManager;

@Mixin(EndCrystalEntityRenderer.class)
public abstract class MixinEndCrystalEntityRenderer {
    @Shadow
    @Final
    private EndCrystalEntityModel model;

    private EndCrystalEntity lastCrystal;
    private float lastTickDelta;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/decoration/EndCrystalEntity;Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;F)V", at = @At("HEAD"), require = 0)
    public void onUpdateRenderState(EndCrystalEntity endCrystalEntity, EndCrystalEntityRenderState state, float tickDelta, CallbackInfo ci) {
        lastCrystal = endCrystalEntity;
        lastTickDelta = tickDelta;
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;Lnet/minecraft/client/util/math/PoseStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true, require = 0)
    public void render(EndCrystalEntityRenderState state, PoseStack matrixStack, OrderedRenderCommandQueue renderQueue, CameraRenderState camera, CallbackInfo ci) {
        if (ModuleManager.chams.isEnabled() && ModuleManager.chams.crystals.getValue() && lastCrystal != null) {
            ci.cancel();
            ModuleManager.chams.renderCrystal(lastCrystal, 0.0f, lastTickDelta, matrixStack, 15728880, model.innerGlass, model.cube);
        }
    }
}
