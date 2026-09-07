package aethereal.core.models;
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

public class ElementDimmingController {
    public final AnimatedFloat dimAnimation = new AnimatedFloat(400, Easings.EASE_IN_OUT_CUBIC);
    public ModuleFrame focusedElement = null;
    public long focusStartTime = -1;
    public long focusDuration = 2000;

    public ElementDimmingController() {
        this.dimAnimation.set(0.0f);
        this.dimAnimation.destination(0.0f);
    }

    public void focusElement(ModuleFrame class758Var) {
        if (class758Var == null) {
            return;
        }
        this.focusedElement = class758Var;
        this.focusStartTime = System.currentTimeMillis();
        this.dimAnimation.destination(1.0f);
    }

    public void clearFocus() {
        this.focusedElement = null;
        this.focusStartTime = -1L;
        this.dimAnimation.destination(0.0f);
    }

    public void animate(WeightedEngine class141Var) {
        if (this.focusedElement != null && this.focusStartTime > 0 && System.currentTimeMillis() - this.focusStartTime >= this.focusDuration) {
            clearFocus();
        }
        this.dimAnimation.animate(class141Var);
    }

    public float getDimmingForElement(ModuleFrame class758Var) {
        if (this.focusedElement == null || class758Var == this.focusedElement) {
            return 0.0f;
        }
        return this.dimAnimation.animatedValue();
    }

    public boolean isDimming() {
        return this.dimAnimation.animatedValue() > 0.01f;
    }

    public boolean hasFocus() {
        return this.focusedElement != null;
    }

    public ModuleFrame focusedElement() {
        return this.focusedElement;
    }

    public ElementDimmingController dimmingDuration(long j) {
        this.focusDuration = j;
        return this;
    }
}
