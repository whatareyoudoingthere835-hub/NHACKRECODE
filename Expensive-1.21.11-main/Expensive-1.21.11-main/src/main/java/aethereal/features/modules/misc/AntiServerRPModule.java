package aethereal.features.modules.misc;
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

import java.util.UUID;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

@Aliases(aliases = {"Anti Server RP", "No Resource Pack", "Block Server RP", "No Server RP", "Anti RP", "Disable Server Pack"})
public class AntiServerRPModule extends Module {
    public final Mc mc;

    public AntiServerRPModule() {
        super(ModuleTab.MISC, "Anti Server RP");
        this.mc = Mc.INSTANCE;
        register(PacketSendEvent.class, class037Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                if ((class037Var.getPacket()) instanceof ResourcePackStatusC2SPacket packet ) {
                    ResourcePackStatusC2SPacket.Status status = packet.status();
                    if (status == ResourcePackStatusC2SPacket.Status.DECLINED || status == ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD) {
                        class037Var.cancel();
                    }
                }
            }
        });
        register(PacketReceiveEvent.class, class051Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                if (class051Var.getPacket() instanceof ResourcePackSendS2CPacket) {
                    class051Var.cancel();
                    UUID uuid= player.getUuid();
                    PacketSender.sendPacket(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.ACCEPTED));
                    PacketSender.sendPacket(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                }
            }
        });
    }
}
