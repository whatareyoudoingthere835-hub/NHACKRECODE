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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

public class PacketBuffer {
    public final ByteBuf buffer;

    public PacketBuffer(ByteBuf byteBuf) {
        this.buffer = byteBuf;
    }

    public static PacketBuffer create() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static PacketBuffer wrap(byte[] bArr, int i) {
        return new PacketBuffer(Unpooled.wrappedBuffer(bArr, i, bArr.length - i));
    }

    public PacketBuffer writeByte(int i) {
        this.buffer.writeByte(i);
        return this;
    }

    public PacketBuffer writeInt(int i) {
        this.buffer.writeInt(i);
        return this;
    }

    public PacketBuffer writeString(String str) {
        byte[] bytes= str.getBytes(StandardCharsets.UTF_8);
        this.buffer.writeInt(bytes.length);
        this.buffer.writeBytes(bytes);
        return this;
    }

    public PacketBuffer writeByteArray(byte[] bArr) {
        this.buffer.writeInt(bArr.length);
        this.buffer.writeBytes(bArr);
        return this;
    }

    public int readByte() {
        return this.buffer.readByte() & 255;
    }

    public int readInt() {
        return this.buffer.readInt();
    }

    public String readString() {
        byte[] bArr= new byte[this.buffer.readInt()];
        this.buffer.readBytes(bArr);
        return new String(bArr, StandardCharsets.UTF_8);
    }

    public byte[] readByteArray() {
        byte[] bArr= new byte[this.buffer.readInt()];
        this.buffer.readBytes(bArr);
        return bArr;
    }

    public int readableBytes() {
        return this.buffer.readableBytes();
    }

    public byte[] toArray() {
        byte[] bArr= new byte[this.buffer.readableBytes()];
        this.buffer.readBytes(bArr);
        this.buffer.release();
        return bArr;
    }
}
