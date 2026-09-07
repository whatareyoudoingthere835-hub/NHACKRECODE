package aethereal.core.models;
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public final class PacketSender {
    public static void sendPacket(Packet<?> packet) {
        if (packet instanceof UpdateSelectedSlotC2SPacket) {
            UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket= (UpdateSelectedSlotC2SPacket) packet;
            if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                if (updateSelectedSlotC2SPacket.getSelectedSlot() == SlotSyncHandler.selectedSlot) {
                    return;
                } else {
                    SlotSyncHandler.selectedSlot = updateSelectedSlotC2SPacket.getSelectedSlot();
                }
            }
        }
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        if (player == null) {
            return;
        }
        player.networkHandler.getConnection().send(packet);
    }

    public static void sendSequencedNotSilentPacket(SequencedPacketCreator sequencedPacketCreator) {
        Mc.INSTANCE.getInteractionManager().sendSequencedPacket(Mc.INSTANCE.getWorld(), sequencedPacketCreator);
    }

    public static void sendSequencedPacket(SequencedPacketCreator sequencedPacketCreator) {
        Mc class815Var= Mc.INSTANCE;
        ClientWorld world= class815Var.getWorld();
        ClientPlayerEntity player= class815Var.getPlayer();
        if (world == null || player == null) {
            return;
        }
        try (PendingUpdateManager pendingUpdateManagerIncrementSequence = world.getPendingUpdateManager().incrementSequence()) {
            player.networkHandler.getConnection().send(sequencedPacketCreator.predict(pendingUpdateManagerIncrementSequence.getSequence()));
        }
    }

    public PacketSender() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
