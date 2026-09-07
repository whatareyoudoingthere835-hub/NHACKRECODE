package aethereal.utils;
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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class InboundPacketHandler extends SimpleChannelInboundHandler<byte[]> {
    public void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bArr) {
        if (bArr.length == 0) {
            return;
        }
        int i= bArr[0] & 255;
        IncomingPacket class618VarCreateIncoming= PacketRegistry.createIncoming(i);
        if (class618VarCreateIncoming == null) {
            System.out.println("[Client] Unknown packet: 0x" + Integer.toHexString(i));
        } else {
            class618VarCreateIncoming.decode(PacketBuffer.wrap(bArr, 1));
            class618VarCreateIncoming.handle();
        }
    }

    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable th) {
    }
}
