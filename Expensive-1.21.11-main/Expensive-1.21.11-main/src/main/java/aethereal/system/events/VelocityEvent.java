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

public class VelocityEvent extends CancellableEvent {
    public double horizontal;
    public double vertical;

    public double getHorizontal() {
        return this.horizontal;
    }

    public double getVertical() {
        return this.vertical;
    }

    public void setHorizontal(double d) {
        this.horizontal = d;
    }

    public void setVertical(double d) {
        this.vertical = d;
    }

    public VelocityEvent(double d, double d2) {
        this.horizontal = d;
        this.vertical = d2;
    }
}
