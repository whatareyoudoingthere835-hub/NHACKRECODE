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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

@Aliases(aliases = {"Blink", "Packet Delay", "Packet Blink", "Packet Queue", "Blink Teleport"})
public class BlinkModule extends Module {
    public final Mc mc;
    public final List<Packet<?>> packetQueue;
    public final Stopwatch stopWatch;
    public Box boundingBox;

    public BlinkModule() {
        super(ModuleTab.MISC, "Blink");
        this.mc = Mc.INSTANCE;
        this.packetQueue = new CopyOnWriteArrayList();
        this.stopWatch = new Stopwatch();
        this.boundingBox = new Box(BlockPos.ORIGIN);
        register(WorldRenderEvent.class, class016Var -> {
            if (!isState() || !this.mc.isWorldLoaded() || isSingleplayerBlocked() || this.mc.getGameOptions().getPerspective() == Perspective.FIRST_PERSON) {
                return;
            }
            ShapeRenderer.INSTANCE.addOutline(class016Var.matrixStack().peek().getPositionMatrix(), this.boundingBox, -1, 1.0f);
        });
        register(PlayerInitEvent.class, class125Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                setState(false);
            }
        });
        register(PacketSendEvent.class, class037Var -> {
            if (isState() && this.mc.isWorldLoaded() && !isSingleplayerBlocked()) {
                sendPacket(class037Var);
            }
        });
        register(PacketReceiveEvent.class, class051Var -> {
            if (isState() && this.mc.isWorldLoaded() && !isSingleplayerBlocked()) {
                receivePacket(class051Var);
            }
        });
    }

    public void sendPacket(PacketSendEvent class037Var) {
        net.minecraft.network.packet.Packet<?> packet = class037Var.getPacket();
        Objects.requireNonNull(packet);
        if (packet instanceof HandshakeC2SPacket) {
            setState(false);
            return;
        }
        if (packet instanceof ClientStatusC2SPacket && ((ClientStatusC2SPacket) packet).getMode().equals(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN)) {
            setState(false);
            return;
        }
        if (this.stopWatch.hasElapsed(0L)) {
            this.packetQueue.add(class037Var.getPacket());
            class037Var.cancel();
        }
    }

    public void receivePacket(PacketReceiveEvent class051Var) {
        net.minecraft.network.packet.Packet<?> packet = class051Var.getPacket();
        Objects.requireNonNull(packet);
        if (packet instanceof PlayerRespawnS2CPacket) {
            setState(false);
        } else if (packet instanceof GameJoinS2CPacket) {
            setState(false);
        } else if (packet instanceof PlayerPositionLookS2CPacket) {
            this.stopWatch.setElapsedTime(-200L, TimeUnit.MILLISECONDS);
            deactivateSilent();
        }
    }

    @Override
    public void activate() {
        if (this.mc.isWorldLoaded() && this.mc.isSingleplayer()) {
            isSingleplayerBlocked();
        } else {
            activateSilent();
            super.activate();
        }
    }

    @Override
    public void deactivate() {
        deactivateSilent();
        super.deactivate();
    }

    public boolean isSingleplayerBlocked() {
        if (!this.mc.isSingleplayer() || !isState()) {
            return false;
        }
        ChatUtil.addChatMessage("Blink не работает в одиночной игре");
        switchState();
        return true;
    }

    public void activateSilent() {
        if (this.mc.isWorldLoaded()) {
            this.boundingBox = this.mc.getPlayer().getBoundingBox();
        }
    }

    public void deactivateSilent() {
        if (Mc.INSTANCE.isWorldLoaded()) {
            this.packetQueue.forEach(PacketSender::sendPacket);
            this.packetQueue.clear();
        }
    }

    public Stopwatch getStopWatch() {
        return this.stopWatch;
    }
}
