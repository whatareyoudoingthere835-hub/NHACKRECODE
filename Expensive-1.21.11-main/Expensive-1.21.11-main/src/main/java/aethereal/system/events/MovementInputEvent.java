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

public class MovementInputEvent extends CancellableEvent {
    public DirectionalInput input;
    public boolean jumping;
    public boolean sneaking;
    public boolean sprinting;

    public DirectionalInput getInput() {
        return this.input;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public void setInput(DirectionalInput class041Var) {
        this.input = class041Var;
    }

    public void setJumping(boolean z) {
        this.jumping = z;
    }

    public void setSneaking(boolean z) {
        this.sneaking = z;
    }

    public void setSprinting(boolean z) {
        this.sprinting = z;
    }

    public MovementInputEvent(DirectionalInput class041Var, boolean z, boolean z2, boolean z3) {
        this.input = class041Var;
        this.jumping = z;
        this.sneaking = z2;
        this.sprinting = z3;
    }
}
