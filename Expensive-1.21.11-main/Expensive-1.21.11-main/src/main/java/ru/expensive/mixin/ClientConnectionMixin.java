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

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public class ClientConnectionMixin {
    @Inject(method = {"handlePacket"}, at = {@At("HEAD")}, cancellable = true)
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener packetListener, CallbackInfo callbackInfo) {
        PacketReceiveEvent class051Var = new PacketReceiveEvent(packet);
        Expensive.INSTANCE.eventDispatcher().dispatch(class051Var);
        if (class051Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"send(Lnet/minecraft/network/packet/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketSendEvent class037Var = new PacketSendEvent(packet);
        Expensive.INSTANCE.eventDispatcher().dispatch(class037Var);
        if (class037Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
