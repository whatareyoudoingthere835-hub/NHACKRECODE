package aethereal.system.events;
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

import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class InteractBlockEvent extends CancellableEvent {
    public final Hand hand;

    public final BlockHitResult result;

    public final ActionResult actionResult;

    public InteractBlockEvent(Hand hand, BlockHitResult blockHitResult, ActionResult actionResult) {
        this.hand = hand;
        this.result = blockHitResult;
        this.actionResult = actionResult;
    }

    public Hand getHand() {
        return this.hand;
    }

    public BlockHitResult getResult() {
        return this.result;
    }

    public ActionResult getActionResult() {
        return this.actionResult;
    }
}
