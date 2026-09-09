package thunder.hack.injection;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.client.ModuleManager;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends net.minecraft.entity.Entity> {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(EntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue renderQueue, CameraRenderState camera, CallbackInfo ci) {
        if (state instanceof ArmorStandEntityRenderState && ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noArmorStands.getValue())
            ci.cancel();
        if (state instanceof PlayerEntityRenderState && ModuleManager.nameTags.isEnabled())
            ci.cancel();
    }
}
