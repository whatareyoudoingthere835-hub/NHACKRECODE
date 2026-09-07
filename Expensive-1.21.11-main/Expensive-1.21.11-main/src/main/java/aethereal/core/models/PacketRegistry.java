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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class PacketRegistry {
    public static final Map<Integer, Supplier<IncomingPacket>> incomingPackets = new HashMap();

    public PacketRegistry() {
    }

    public static void registerIncoming(int i, Supplier<IncomingPacket> supplier) {
        incomingPackets.put(Integer.valueOf(i), supplier);
    }

    public static IncomingPacket createIncoming(int i) {
        Supplier<IncomingPacket> supplier= incomingPackets.get(Integer.valueOf(i));
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    static {
        registerIncoming(1, ServerMessagePacket::new);
        registerIncoming(2, TriggerRoutinePacket::new);
        registerIncoming(3, ExitPacket::new);
    }
}
