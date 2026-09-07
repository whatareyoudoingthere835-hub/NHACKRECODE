package aethereal.graphics;
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

public class AnimationStack2 {
    public final TweenAnimationStack stack = new TweenAnimationStack();

    public void begin() {
        this.stack.begin();
    }

    public float animationMultiplier() {
        return this.stack.animation();
    }

    public void push() {
        this.stack.push();
    }

    public void animation(ToggleAnimator class323Var) {
        this.stack.animation(class323Var.smoothAnimation());
    }

    public void pop() {
        this.stack.pop();
    }

    public void end() {
        this.stack.end();
    }
}
