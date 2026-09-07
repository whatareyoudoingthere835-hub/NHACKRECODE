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

import java.nio.charset.StandardCharsets;

public class ServerMessagePacket implements IncomingPacket {
    public String text;

    @Override
    public void decode(PacketBuffer class621Var) {
        byte[] bArr= new byte[class621Var.readableBytes()];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) class621Var.readByte();
        }
        this.text = new String(bArr, StandardCharsets.UTF_8);
    }

    @Override
    public void handle() {
    }
}
