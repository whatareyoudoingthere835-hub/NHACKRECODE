package ru.expensive.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.event.EventHandler;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.common.util.math.Counter;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServerRPSpooferModule extends Module {
    @NonFinal
    @Getter
    @Setter
    ResourcePackAction currentAction = ResourcePackAction.WAIT;
    @Getter
    Counter counter = Counter.create();
    @NonFinal
    private UUID packId;
    public ServerRPSpooferModule() {
        super("ServerRPSpoof", "Server RP Spoof", ModuleCategory.MISC);
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacket() instanceof ResourcePackSendS2CPacket sendPacket) {
            // Отвечать нужно ТЕМ ЖЕ id, иначе сервер не сможет сопоставить ответ
            // и будет бесконечно слать пак / гонять в Reconfiguring.
            packId = sendPacket.id();
            currentAction = ResourcePackAction.ACCEPT;
            packetEvent.cancel();
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler == null || packId == null) {
            return;
        }
        if (currentAction == ResourcePackAction.ACCEPT) {
            networkHandler.sendPacket(new ResourcePackStatusC2SPacket(packId, ResourcePackStatusC2SPacket.Status.ACCEPTED));
            currentAction = ResourcePackAction.SEND;
            counter.resetCounter();
        } else if (currentAction == ResourcePackAction.SEND && counter.isReached(300L)) {
            networkHandler.sendPacket(new ResourcePackStatusC2SPacket(packId, ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            currentAction = ResourcePackAction.WAIT;
            packId = null;
        }
    }

    @Override
    public void deactivate() {
        currentAction = ResourcePackAction.WAIT;
        packId = null;
        super.deactivate();
    }

    public enum ResourcePackAction {
        ACCEPT, SEND, WAIT;
    }
}
