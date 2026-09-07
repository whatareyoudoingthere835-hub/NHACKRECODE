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

import java.nio.ByteBuffer;

public class AuthResponsePacket extends SocketPacket {
    public String hwid;
    public String json;
    public long value;

    public AuthResponsePacket(String str) {
        this.hwid = str;
    }

    public AuthResponsePacket() {
    }

    @Override
    public void read(ByteBuffer byteBuffer) {
        if (byteBuffer.capacity() == 1) {
            return;
        }
        this.value = byteBuffer.getLong();
        if (byteBuffer.remaining() > 0) {
            byte[] bArr= new byte[byteBuffer.remaining()];
            byteBuffer.get(bArr);
            this.json = new String(bArr);
        }
    }

    @Override
    public ByteBuffer write() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte packetID() {
        return (byte) 2;
    }
}
