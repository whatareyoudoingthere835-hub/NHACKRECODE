package aethereal.features.modules.movement;
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

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import java.util.stream.Stream;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

@Aliases(aliases = {"No Slow", "No Slow Down"})
public class NoSlowModule extends Module {
    public final ModeSetting<NoSlowMode> modeSetting;
    public final Mc mc;
    public boolean interacting;
    public final Stopwatch stopwatch;
    public ActionScheduler scheduler;

    public NoSlowModule() {
        super(ModuleTab.MOVEMENT, "No Slow");
        this.modeSetting = new ModeSetting(Lang.MODE).values(NoSlowMode.class);
        this.mc = Mc.INSTANCE;
        this.stopwatch = new Stopwatch();
        this.scheduler = new ActionScheduler();
        addSettings(this.modeSetting);
        register(PacketSendEvent.class, class037Var -> {
            if (isState() && this.mc.isWorldLoaded() && (class037Var.getPacket() instanceof PlayerInteractItemC2SPacket)) {
                this.interacting = true;
                if (ServerUtil.isConnectedToServer("holyworld") && !this.stopwatch.hasElapsed(100L)) {
                    class037Var.cancel();
                } else if (this.modeSetting.isSelected(NoSlowMode.HOLYWORLD)) {
                    this.mc.getPlayer().networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket((this.mc.getPlayer().getInventory().getSelectedSlot() % 8) + 1));
                    this.mc.getPlayer().networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.mc.getPlayer().getInventory().getSelectedSlot()));
                }
            }
        });
        register(PacketReceiveEvent.class, class051Var -> {
            if (isState() && this.mc.isWorldLoaded() && this.modeSetting.isSelected(NoSlowMode.HOLYWORLD)) {
                if ((class051Var.getPacket()) instanceof ScreenHandlerSlotUpdateS2CPacket packet ) {
                    ScreenHandlerSlotUpdateS2CPacket screenHandlerSlotUpdateS2CPacket= packet;
                    if (this.mc.getPlayer() != null) {
                        if ((screenHandlerSlotUpdateS2CPacket.getSlot() - 36 == this.mc.getPlayer().getInventory().getSelectedSlot() || screenHandlerSlotUpdateS2CPacket.getSlot() == 45) && this.modeSetting.isSelected(NoSlowMode.HOLYWORLD) && !this.stopwatch.hasElapsed(500L)) {
                            this.interacting = false;
                            class051Var.cancel();
                        }
                    }
                }
            }
        });
        register(PlayerTickEvent.class, class130Var -> {
            if (class130Var.isPre()) {
                this.scheduler.update().cleanupIfFinished();
            }
        });
        register(MovementInputEvent3.class, class289Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                boolean zIsCancelled= class289Var.isCancelled();
                int itemUseTime= this.mc.getPlayer().getItemUseTime();
                switch (((NoSlowMode) this.modeSetting.currentValue()).ordinal()) {
                    case 0:
                        if (this.mc.getPlayer().isUsingItem() && !this.mc.getPlayer().hasVehicle()) {
                            class289Var.cancel();
                        }
                        break;
                    case 1:
                        handleMatrixMode(class289Var);
                        break;
                    case 2:
                        handleHolyWorldMode(class289Var);
                        break;
                    case 3:
                        if (this.mc.getPlayer().isUsingItem() && !this.mc.getPlayer().hasVehicle() && (this.mc.getPlayer().getActiveItem().getItem() instanceof CrossbowItem)) {
                            class289Var.cancel();
                        }
                        break;
                    case 4:
                        if (this.mc.getPlayer().isUsingItem() && !this.mc.getPlayer().hasVehicle() && itemUseTime > 1 && ((!SimulatedPlayer.simulateLocalPlayer(1).onGround || !this.mc.getGameOptions().jumpKey.isPressed()) && this.stopwatch.hasElapsed(100L))) {
                            class289Var.cancel();
                            this.stopwatch.reset();
                        }
                        break;
                    case 5:
                        handleGrimMode(class289Var);
                        break;
                }
                if (!class289Var.isCancelled() || zIsCancelled) {
                    return;
                }
                this.mc.getPlayer().setSprinting(true);
            }
        });
    }

    public void handleMatrixMode(MovementInputEvent3 class289Var) {
        ClientPlayerEntity player= this.mc.getPlayer();
        if (!player.isUsingItem() || player.hasVehicle()) {
            return;
        }
        class289Var.cancel();
        boolean z= ((double) player.fallDistance) > 0.725d;
        if (!player.isOnGround() || player.input.playerInput.jump()) {
            if (z) {
                float f= (((double) player.fallDistance) > 1.4d ? 1 : (((double) player.fallDistance) == 1.4d ? 0 : -1)) > 0 ? 0.95f : 0.97f;
                player.setVelocity(player.getVelocity().multiply((double) f, 1.0d, (double) f));
                return;
            }
            return;
        }
        if (player.age % 2 == 0) {
            float f2= (player.input.getMovementInput().x > 0.0f ? 1 : (player.input.getMovementInput().x == 0.0f ? 0 : -1)) == 0 ? 0.5f : 0.4f;
            player.setVelocity(player.getVelocity().multiply((double) f2, 1.0d, (double) f2));
        }
    }

    public void handleGrimMode(MovementInputEvent3 class289Var) {
        ClientPlayerEntity player= this.mc.getPlayer();
        ClientPlayerInteractionManager interactionManager= this.mc.getInteractionManager();
        ClientPlayNetworkHandler networkHandler= this.mc.getNetworkHandler();
        if (!player.isUsingItem() || player.hasVehicle() || player.getItemUseTime() <= 3) {
            return;
        }
        boolean z= player.getActiveHand() == Hand.OFF_HAND;
        boolean z2= player.getActiveHand() == Hand.MAIN_HAND;
        interactionManager.syncSelectedSlot();
        if (z && !player.getItemCooldownManager().isCoolingDown(player.getOffHandStack())) {
            int i= player.getInventory().getSelectedSlot();
            networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i + 1 > 8 ? i - 1 : i + 1));
            networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(player.getInventory().getSelectedSlot()));
            class289Var.cancel();
        }
        if (z2 && !player.getItemCooldownManager().isCoolingDown(player.getMainHandStack())) {
            player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0, player.getYaw(), player.getPitch()));
            if (player.getOffHandStack().getUseAction().equals(UseAction.NONE)) {
                class289Var.cancel();
            }
        }
        interactionManager.syncSelectedSlot();
    }

    public void handleHolyWorldMode(MovementInputEvent3 class289Var) {
        int itemUseTime= this.mc.getPlayer().getItemUseTime();
        ClientPlayerEntity player= this.mc.getPlayer();
        if (player.isUsingItem()) {
            if (this.interacting && itemUseTime < 10) {
                this.scheduler.addTickStep(1, () -> {
                    Slot slotMainHandSlot= mainHandSlot();
                    Slot slotOffHandSlot= offHandSlot();
                    this.mc.getInteractionManager().clickSlot(0, slotMainHandSlot.id, 40, SlotActionType.SWAP, player);
                    this.mc.getInteractionManager().clickSlot(0, slotOffHandSlot.id, player.getInventory().getSelectedSlot(), SlotActionType.SWAP, player);
                    player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(player.currentScreenHandler.syncId));
                    this.stopwatch.reset();
                });
            }
            if (itemUseTime % 2 == 0 || !this.interacting) {
                class289Var.cancel();
            }
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    public Slot mainHandSlot() {
        long jCount= slots().count();
        return (Slot) getSlots().get(Math.toIntExact((jCount - ((long) (jCount == 46 ? 10 : 9))) + ((long) this.mc.getPlayer().getInventory().getSelectedSlot())));
    }

    public Slot offHandSlot() {
        return (Slot) getSlots().get(45);
    }

    public Stream<Slot> slots() {
        return getSlots().stream();
    }

    public DefaultedList<Slot> getSlots() {
        return this.mc.getPlayer().currentScreenHandler.slots;
    }
}
