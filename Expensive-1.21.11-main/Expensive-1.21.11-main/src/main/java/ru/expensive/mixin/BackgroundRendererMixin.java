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

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.BlindnessEffectFogModifier;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class BackgroundRendererMixin {
    @Redirect(method = {"getFogColor", "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;shouldApply(Lnet/minecraft/block/enums/CameraSubmersionType;Lnet/minecraft/entity/Entity;)Z"), require = 0)
    private boolean modifyFogEffects(FogModifier modifier, CameraSubmersionType submersionType, Entity entity) {
        VisualEffectType type = null;
        if (modifier instanceof BlindnessEffectFogModifier) {
            type = VisualEffectType.BLINDNESS;
        } else if (modifier instanceof DarknessEffectFogModifier) {
            type = VisualEffectType.DARKNESS;
        }
        if (type != null) {
            VisualEffectEvent event = new VisualEffectEvent(type);
            Expensive.INSTANCE.eventDispatcher().dispatch(event);
            if (event.isCancelled()) {
                return false;
            }
        }
        return modifier.shouldApply(submersionType, entity);
    }

    @Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true)
    private void modifyFogColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> callbackInfoReturnable) {
        WorldTweaksModule module = Expensive.INSTANCE.moduleRepository().get(WorldTweaksModule.class);
        if (camera.getSubmersionType() == CameraSubmersionType.LAVA) {
            RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayType.LAVA_OVERLAY);
            Expensive.INSTANCE.eventDispatcher().dispatch(event);
            if (event.isCancelled()) {
                callbackInfoReturnable.setReturnValue(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
            }
            return;
        }
        if (camera.getSubmersionType() != CameraSubmersionType.POWDER_SNOW && module.isState() && module.changeFogColor().isValue()) {
            ColorSetting color = module.fogColor();
            callbackInfoReturnable.setReturnValue(new Vector4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha()));
        }
    }

    @org.spongepowered.asm.mixin.injection.ModifyVariable(method = "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuDevice;createCommandEncoder()Lcom/mojang/blaze3d/systems/CommandEncoder;"), ordinal = 0)
    private FogData modifyFogDistance(FogData fogData) {
        WorldTweaksModule module = Expensive.INSTANCE.moduleRepository().get(WorldTweaksModule.class);
        if (module.isState() && module.changeFogColor().isValue()) {
            float end = Math.max(1.0f, module.fogDistance().currentValue());
            float start = Math.max(0.0f, Math.min(end - 0.5f, end * 0.05f));
            fogData.environmentalStart = start;
            fogData.environmentalEnd = end;
            fogData.renderDistanceStart = start;
            fogData.renderDistanceEnd = end;
            fogData.skyEnd = end;
            fogData.cloudEnd = end;
        }
        return fogData;
    }
}
