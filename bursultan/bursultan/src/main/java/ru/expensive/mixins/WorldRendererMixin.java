package ru.expensive.mixins;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.implement.features.modules.render.ClearRenderModule;
import ru.expensive.core.Extra;

@Mixin(EntityRenderManager.class)
public class WorldRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void hideBoats(E entity, Frustum frustum, double x, double y, double z,
                                             CallbackInfoReturnable<Boolean> cir) {
        ClearRenderModule clearRenderModule = (ClearRenderModule) Extra.getInstance().getModuleProvider().module("ClearRender");
        if (clearRenderModule.isState() && clearRenderModule.getClearRenderSettings().isSelected("Boat")
                && entity instanceof BoatEntity) {
            cir.setReturnValue(false);
        }
    }
}
