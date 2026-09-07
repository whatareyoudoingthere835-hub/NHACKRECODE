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

public class SpringAnimator {
    public final EasingFunction easing;
    public final float startValue;
    public final float endValue;
    public final float stiffness;
    public final float damping;
    public final float mass;
    public final float timeStep;

    public static final int defaultDuration = 300;
    public float value;
    public float targetValue;
    public float velocity;

    public SpringAnimator(float f, float f2, EasingFunction class356Var) {
        this(f, f2, 32.0f, 5.5f, 1.0f, defaultDuration, class356Var);
    }

    public SpringAnimator(float f, float f2, float f3, float f4, float f5, int i, EasingFunction class356Var) {
        this.easing = class356Var;
        this.startValue = f;
        this.endValue = f2;
        this.stiffness = f3;
        this.damping = f4;
        this.mass = f5;
        this.targetValue = f;
        this.value = f;
        this.timeStep = 1000.0f / i;
    }

    public void animate(WeightedEngine class141Var) {
        float fEase= this.easing.ease(class141Var.weight()) * this.timeStep * class141Var.engine().animationMultiplier();
        float f= this.targetValue - this.value;
        this.velocity += (((this.stiffness * f) - (this.damping * this.velocity)) / this.mass) * fEase;
        this.value += this.velocity * fEase;
        if (Math.abs(f) >= 0.001f || Math.abs(this.velocity) >= 0.001f) {
            return;
        }
        this.value = this.targetValue;
        this.velocity = 0.0f;
    }

    public void invert() {
        target(Math.abs(this.targetValue - this.endValue) < Math.abs(this.targetValue - this.startValue) ? this.startValue : this.endValue);
    }

    public void target(float f) {
        this.targetValue = FastMathUtils.clamp(f, Math.min(this.startValue, this.endValue), Math.max(this.startValue, this.endValue));
    }

    public float clampedValue() {
        return FastMathUtils.clamp(this.value, Math.min(this.startValue, this.endValue), Math.max(this.startValue, this.endValue));
    }

    public float value() {
        return this.value;
    }
}
