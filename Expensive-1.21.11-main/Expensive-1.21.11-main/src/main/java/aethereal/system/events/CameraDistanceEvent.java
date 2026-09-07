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

public class CameraDistanceEvent extends CancellableEvent {
    public float pitch;
    public float distance;
    public ToggleAnimator frontAnim;
    public ToggleAnimator backAnim;

    public CameraDistanceEvent(float f) {
        this.pitch = f;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getDistance() {
        return this.distance;
    }

    public ToggleAnimator getFrontAnim() {
        return this.frontAnim;
    }

    public ToggleAnimator getBackAnim() {
        return this.backAnim;
    }

    public void setPitch(float f) {
        this.pitch = f;
    }

    public void setDistance(float f) {
        this.distance = f;
    }

    public void setFrontAnim(ToggleAnimator class323Var) {
        this.frontAnim = class323Var;
    }

    public void setBackAnim(ToggleAnimator class323Var) {
        this.backAnim = class323Var;
    }
}
