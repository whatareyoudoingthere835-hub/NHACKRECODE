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

public class PlayerPositionEvent extends CancellableEvent {
    public PlayerPositionStage stage;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;

    public PlayerPositionStage stage() {
        return this.stage;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public boolean onGround() {
        return this.onGround;
    }

    public PlayerPositionEvent stage(PlayerPositionStage class317Var) {
        this.stage = class317Var;
        return this;
    }

    public PlayerPositionEvent x(double d) {
        this.x = d;
        return this;
    }

    public PlayerPositionEvent y(double d) {
        this.y = d;
        return this;
    }

    public PlayerPositionEvent z(double d) {
        this.z = d;
        return this;
    }

    public PlayerPositionEvent yaw(float f) {
        this.yaw = f;
        return this;
    }

    public PlayerPositionEvent pitch(float f) {
        this.pitch = f;
        return this;
    }

    public PlayerPositionEvent onGround(boolean z) {
        this.onGround = z;
        return this;
    }

    public PlayerPositionEvent(PlayerPositionStage class317Var, double d, double d2, double d3, float f, float f2, boolean z) {
        this.stage = class317Var;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.yaw = f;
        this.pitch = f2;
        this.onGround = z;
    }
}
