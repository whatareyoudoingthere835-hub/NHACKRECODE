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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({PlayerPositionLookS2CPacket.class})
public class PlayerPositionLookS2CPacketMixin {
    @WrapOperation(method = {"apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;onPlayerPositionLook(Lnet/minecraft/network/packet/s2c/play/PlayerPositionLookS2CPacket;)V")})
    public void applyHook(ClientPlayPacketListener clientPlayPacketListener, PlayerPositionLookS2CPacket playerPositionLookS2CPacket, Operation<Void> operation) {
        Rotation class007VarPlayerRotation = Rotation.playerRotation();
        operation.call(new Object[]{clientPlayPacketListener, playerPositionLookS2CPacket});
        ServerRotationEvent class079Var = new ServerRotationEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class079Var);
        if (class079Var.isCancelled() && Mc.INSTANCE.isWorldLoaded()) {
            Rotation class007VarRandom = class007VarPlayerRotation.random(0.001f);
            Mc.INSTANCE.getPlayer().setYaw(class007VarRandom.getYaw());
            Mc.INSTANCE.getPlayer().setPitch(class007VarRandom.getPitch());
        }
    }
}
