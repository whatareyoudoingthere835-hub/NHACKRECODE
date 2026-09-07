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

public class RotatedMovementInputEvent implements Event {
    public float forward;
    public float sideways;

    public RotatedMovementInputEvent(float f, float f2) {
        this.forward = f;
        this.sideways = f2;
    }

    public float getForward() {
        return this.forward;
    }

    public float getSideways() {
        return this.sideways;
    }

    public void setForward(float f) {
        this.forward = f;
    }

    public void setSideways(float f) {
        this.sideways = f;
    }

    public String toString() {
        return "RotatedMovementInputEvent(forward=" + getForward() + ", sideways=" + getSideways() + ")";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RotatedMovementInputEvent)) {
            return false;
        }
        RotatedMovementInputEvent class230Var= (RotatedMovementInputEvent) obj;
        return class230Var.canEqual(this) && Float.compare(getForward(), class230Var.getForward()) == 0 && Float.compare(getSideways(), class230Var.getSideways()) == 0;
    }

    public boolean canEqual(Object obj) {
        return obj instanceof RotatedMovementInputEvent;
    }

    public int hashCode() {
        return (((1 * 59) + Float.floatToIntBits(getForward())) * 59) + Float.floatToIntBits(getSideways());
    }
}
