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

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerInteractItemC2SPacket.class})
public class PlayerInteractItemC2SPacketMixin {

    @Mutable
    @Shadow
    @Final
    private float yaw;

    @Mutable
    @Shadow
    @Final
    private float pitch;

    @Inject(method = {"<init>(Lnet/minecraft/util/Hand;IFF)V"}, at = {@At("RETURN")})
    private void modifyRotation(Hand hand, int i, float f, float f2, CallbackInfo callbackInfo) {
        Rotation currentRotation = PlayerRotationManager.INSTANCE.getCurrentRotation();
        if (currentRotation == null) {
            return;
        }
        this.yaw = currentRotation.getYaw();
        this.pitch = currentRotation.getPitch();
    }
}
