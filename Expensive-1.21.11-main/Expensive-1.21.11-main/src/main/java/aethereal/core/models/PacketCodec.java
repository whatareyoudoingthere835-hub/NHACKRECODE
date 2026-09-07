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

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class PacketCodec {
    public static final Map<Byte, Class<? extends SocketPacket>> packetTypes = new HashMap();

    public static SocketPacket fromBytes(byte[] bArr) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        ByteBuffer byteBufferMethod001= decrypt(ByteBuffer.wrap(bArr));
        byte b= byteBufferMethod001.get();
        Class<? extends SocketPacket> cls = packetTypes.get(Byte.valueOf(b));
        if (cls == null) {
            throw new IllegalArgumentException("Unknown packet ID: " + b);
        }
        SocketPacket class716VarNewInstance= cls.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        class716VarNewInstance.read(byteBufferMethod001);
        return class716VarNewInstance;
    }

    public static byte[] sendData(SocketPacket class716Var) {
        return encrypt(class716Var.write());
    }

    public static ByteBuffer decrypt(ByteBuffer byteBuffer) {
        for (int i = 0; i < byteBuffer.limit(); i++) {
            byteBuffer.put(i, (byte) ((byteBuffer.get(i) ^ (byteBuffer.limit() % 66)) ^ 35));
        }
        return byteBuffer;
    }

    public static byte[] encrypt(ByteBuffer byteBuffer) {
        for (int i = 0; i < byteBuffer.limit(); i++) {
            byteBuffer.put(i, (byte) ((byteBuffer.get(i) ^ (byteBuffer.limit() % 66)) ^ 35));
        }
        return byteBuffer.array();
    }

    static {
        packetTypes.put((byte) 1, AuthHandshakePacket.class);
        packetTypes.put((byte) 2, AuthResponsePacket.class);
    }
}
