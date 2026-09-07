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

public class MovementUpdateEvent extends CancellableEvent {
    public DirectionalInput directionalInput;
    public boolean sprint;
    public MovementUpdateSource source;

    public MovementUpdateEvent(DirectionalInput class041Var, boolean z, MovementUpdateSource class309Var) {
        this.directionalInput = class041Var;
        this.sprint = z;
        this.source = class309Var;
    }

    public DirectionalInput getDirectionalInput() {
        return this.directionalInput;
    }

    public boolean isSprint() {
        return this.sprint;
    }

    public MovementUpdateSource getSource() {
        return this.source;
    }

    public void setSprint(boolean z) {
        this.sprint = z;
    }
}
