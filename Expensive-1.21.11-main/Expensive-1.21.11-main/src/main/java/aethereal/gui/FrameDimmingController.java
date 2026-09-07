package aethereal.gui;
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

public class FrameDimmingController {
    public final AnimatedFloat dimAnimation = new AnimatedFloat(400, Easings.EASE_IN_OUT_CUBIC);
    public AbstractFrame focusedFrame = null;
    public long focusStartTime = -1;
    public static final long FOCUS_DURATION = 2000;

    public void focusFrame(AbstractFrame class757Var) {
        if (class757Var == null) {
            return;
        }
        this.focusedFrame = class757Var;
        this.focusStartTime = System.currentTimeMillis();
        this.dimAnimation.destination(1.0f);
    }

    public void clearFocus() {
        this.focusedFrame = null;
        this.focusStartTime = -1L;
        this.dimAnimation.destination(0.0f);
    }

    public void animate(WeightedEngine class141Var) {
        if (this.focusedFrame != null && this.focusStartTime > 0 && System.currentTimeMillis() - this.focusStartTime >= FOCUS_DURATION) {
            clearFocus();
        }
        this.dimAnimation.animate(class141Var);
    }

    public float getDimmingForFrame(AbstractFrame class757Var) {
        if (this.focusedFrame == null || class757Var == this.focusedFrame) {
            return 0.0f;
        }
        return this.dimAnimation.animatedValue();
    }

    public boolean isDimming() {
        return this.dimAnimation.animatedValue() > 0.01f;
    }

    public AbstractFrame focusedFrame() {
        return this.focusedFrame;
    }
}
