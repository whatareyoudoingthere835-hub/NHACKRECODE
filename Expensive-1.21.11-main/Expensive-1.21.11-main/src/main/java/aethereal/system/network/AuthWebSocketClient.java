package aethereal.system.network;
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

import java.net.URI;
import java.nio.ByteBuffer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class AuthWebSocketClient extends WebSocketClient {
    public final WebSocketPacketReader packetReader;
    public final String authToken;

    public AuthWebSocketClient(URI uri, WebSocketPacketReader class715Var, String str) {
        super(uri);
        this.packetReader = class715Var;
        this.authToken = str;
    }

    public void onMessage(ByteBuffer byteBuffer) {
        try {
            this.packetReader.read(PacketCodec.fromBytes(byteBuffer.array()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onOpen(ServerHandshake serverHandshake) {
        sendPacket(new AuthHandshakePacket(this.authToken));
    }

    public void sendPacket(SocketPacket class716Var) {
        send(PacketCodec.sendData(class716Var));
    }

    public void onMessage(String str) {
    }

    public void onClose(int i, String str, boolean z) {
    }

    public void onError(Exception exc) {
    }
}
