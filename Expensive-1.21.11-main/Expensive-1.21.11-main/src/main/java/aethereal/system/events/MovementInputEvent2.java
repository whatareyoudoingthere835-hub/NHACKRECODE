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

public class MovementInputEvent2 implements Event {
    public float movementForward;
    public float movementSideways;

    public float getMovementForward() {
        return this.movementForward;
    }

    public float getMovementSideways() {
        return this.movementSideways;
    }

    public void setMovementForward(float f) {
        this.movementForward = f;
    }

    public void setMovementSideways(float f) {
        this.movementSideways = f;
    }

    public MovementInputEvent2(float f, float f2) {
        this.movementForward = f;
        this.movementSideways = f2;
    }
}
