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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Aliases(aliases = {"No Interact", "Ghost Hand"})
public class NoInteractModule extends Module {
    public final Mc mc;

    public NoInteractModule() {
        super(ModuleTab.PLAYER, "No Interact");
        this.mc = Mc.INSTANCE;
        register(UseItemEvent.class, class059Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                for (Hand hand : Hand.values()) {
                    ClientPlayerEntity player= this.mc.getPlayer();
                    ItemStack stackInHand= player.getStackInHand(hand);
                    if (!player.getItemCooldownManager().isCoolingDown(stackInHand) && !stackInHand.isEmpty() && !(stackInHand.getItem() instanceof BucketItem)) {
                        ActionResult.Success successUse = (ActionResult.Success) (stackInHand.use(this.mc.getWorld(), player, hand));
                        if (successUse instanceof ActionResult.Success) {
                            ActionResult.Success success = successUse;
                            PlayerActionUtil.INSTANCE.interactItem(hand, PlayerRotationManager.INSTANCE.getCurrentRotation(), false);
                            if (success.swingSource().equals(ActionResult.SwingSource.CLIENT)) {
                                player.swingHand(hand);
                            }
                            class059Var.cancel();
                        }
                    }
                }
            }
        });
    }
}
