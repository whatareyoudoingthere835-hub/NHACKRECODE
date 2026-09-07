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

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SoundManager.class})
public class SoundManagerMixin {
    @Inject(method = {"play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;"}, at = {@At("HEAD")}, cancellable = true)
    private void expensive$onPlayNoDelay(SoundInstance soundInstance, CallbackInfoReturnable<SoundSystem.PlayResult> callbackInfo) {
        if (soundInstance == null) {
            return;
        }
        SoundPlayEvent class017Var = new SoundPlayEvent(soundInstance);
        Expensive.INSTANCE.eventDispatcher().dispatch(class017Var);
        if (class017Var.isCancelled()) {
            callbackInfo.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
        } else {
            if (class017Var.getVolumeMultiplier() == 1.0f || !(soundInstance instanceof AbstractSoundInstance)) {
                return;
            }
            ((AbstractSoundInstance) soundInstance).volume *= class017Var.getVolumeMultiplier();
        }
    }

    @Inject(method = {"play(Lnet/minecraft/client/sound/SoundInstance;I)V"}, at = {@At("HEAD")}, cancellable = true)
    private void expensive$onPlayWithDelay(SoundInstance soundInstance, int i, CallbackInfo callbackInfo) {
        if (soundInstance == null) {
            return;
        }
        SoundPlayEvent class017Var = new SoundPlayEvent(soundInstance);
        Expensive.INSTANCE.eventDispatcher().dispatch(class017Var);
        if (class017Var.isCancelled()) {
            callbackInfo.cancel();
        } else {
            if (class017Var.getVolumeMultiplier() == 1.0f || !(soundInstance instanceof AbstractSoundInstance)) {
                return;
            }
            ((AbstractSoundInstance) soundInstance).volume *= class017Var.getVolumeMultiplier();
        }
    }
}
