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

import java.util.Objects;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemInteractionHelper {
    public ActionResult interactItem(PlayerEntity playerEntity, Hand hand, Rotation class007Var) {
        ClientPlayerInteractionManager interactionManager= Mc.INSTANCE.getInteractionManager();
        if (interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        interactionManager.syncSelectedSlot();
        MutableObject mutableObject= new MutableObject();
        interactionManager.sendSequencedPacket(Mc.INSTANCE.getWorld(), i -> {
            PlayerInteractItemC2SPacket playerInteractItemC2SPacket= new PlayerInteractItemC2SPacket(hand, i, class007Var.getYaw(), class007Var.getPitch());
            ItemStack stackInHand= playerEntity.getStackInHand(hand);
            if (playerEntity.getItemCooldownManager().isCoolingDown(stackInHand)) {
                mutableObject.setValue(ActionResult.PASS);
                return playerInteractItemC2SPacket;
            }
            ActionResult.Success successUse = (ActionResult.Success) (stackInHand.use(Mc.INSTANCE.getWorld(), playerEntity, hand));
            ItemStack stackInHand2= successUse instanceof ActionResult.Success ? (ItemStack) Objects.requireNonNullElseGet(successUse.getNewHandStack(), () -> {
                return playerEntity.getStackInHand(hand);
            }) : playerEntity.getStackInHand(hand);
            if (stackInHand2 != stackInHand) {
                playerEntity.setStackInHand(hand, stackInHand2);
            }
            mutableObject.setValue(successUse);
            return playerInteractItemC2SPacket;
        });
        return (ActionResult) mutableObject.getValue();
    }
}
