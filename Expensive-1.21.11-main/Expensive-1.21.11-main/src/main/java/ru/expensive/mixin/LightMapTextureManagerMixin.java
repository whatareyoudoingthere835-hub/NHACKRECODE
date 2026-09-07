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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({LightmapTextureManager.class})
public class LightMapTextureManagerMixin {
    @ModifyExpressionValue(method = {"update(F)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 2)})
    private Object onBrightness(Object obj) {
        GammaEvent class336Var = new GammaEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class336Var);
        return class336Var.isCancelled() ? Double.valueOf(class336Var.getGamma()) : obj;
    }

    @Redirect(method = "update(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEffectFadeFactor(Lnet/minecraft/registry/entry/RegistryEntry;F)F"))
    private float injectAntiDarkness(ClientPlayerEntity clientPlayerEntity, RegistryEntry<StatusEffect> registryEntry, float tickProgress) {
        VisualEffectEvent class258Var = new VisualEffectEvent(VisualEffectType.DARKNESS);
        Expensive.INSTANCE.eventDispatcher().dispatch(class258Var);
        if (class258Var.isCancelled()) {
            return 0.0f;
        }
        return clientPlayerEntity.getEffectFadeFactor(registryEntry, tickProgress);
    }
}
