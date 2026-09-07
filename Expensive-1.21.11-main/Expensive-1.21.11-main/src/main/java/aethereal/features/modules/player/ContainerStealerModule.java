package aethereal.features.modules.player;
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

import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@Aliases(aliases = {"cheststealer", "containerstealer", "automyst"})
public class ContainerStealerModule extends Module {
    public final Mc mc;
    public final NumberSetting delaySetting;
    public final Stopwatch stopwatch;

    public ContainerStealerModule() {
        super(ModuleTab.PLAYER, "Container Stealer");
        this.mc = Mc.INSTANCE;
        this.delaySetting = new NumberSetting(Lang.CHESTSTEALER_DELAY).currentValue(50.0f).range(0.0f, 500.0f).step(5.0f).unit(SettingUnit.MILLISECONDS);
        this.stopwatch = new Stopwatch();
        addSettings(this.delaySetting);
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && this.mc.isWorldLoaded() && class130Var.isPre()) {
                if ((this.mc.getCurrentScreen()) instanceof GenericContainerScreen currentScreen ) {
                    GenericContainerScreenHandler screenHandler= currentScreen.getScreenHandler();
                    long jRound= Math.round(this.delaySetting.currentValue());
                    for (int i = 0; i < screenHandler.getInventory().size(); i++) {
                        if (jRound > 0 && !this.stopwatch.hasElapsed(jRound, TimeUnit.MILLISECONDS)) {
                            return;
                        }
                        Slot slot= screenHandler.getSlot(i);
                        if (slot.hasStack() && slot.getStack() != null) {
                            PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.QUICK_MOVE, i, 0);
                            if (jRound > 0) {
                                this.stopwatch.reset();
                            }
                        }
                    }
                }
            }
        });
        register(ScreenOpenEvent.class, class135Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                this.stopwatch.setElapsedTime((long) (this.delaySetting.currentValue() - 100.0f), TimeUnit.MILLISECONDS);
            }
        });
        register(CloseScreenEvent.class, class270Var -> {
            ClientPlayerEntity player= this.mc.getPlayer();
            if (isState() && this.mc.isWorldLoaded() && (player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                GrimDelayHandler.releaseKeys();
                GrimDelayHandler.addTask(() -> {
                    player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(player.currentScreenHandler.syncId));
                });
                Mc.INSTANCE.getPlayer().closeScreen();
                class270Var.cancel();
            }
        });
    }
}
