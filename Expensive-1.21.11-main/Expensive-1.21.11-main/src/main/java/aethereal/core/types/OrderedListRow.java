package aethereal.core.types;
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

public class OrderedListRow<E> {
    public final E value;
    public final ToggleSwitch toggleSwitch;
    public final ToggleAnimator toggleAnimation;
    public final WidgetBounds rowRect = new WidgetBounds(0.0f, 0.0f, 0.0f, 0.0f);
    public final WidgetBounds grabRect = new WidgetBounds(0.0f, 0.0f, 0.0f, 0.0f);

    public E value() {
        return this.value;
    }

    public ToggleSwitch toggleSwitch() {
        return this.toggleSwitch;
    }

    public ToggleAnimator toggleAnimation() {
        return this.toggleAnimation;
    }

    public WidgetBounds rowRect() {
        return this.rowRect;
    }

    public WidgetBounds grabRect() {
        return this.grabRect;
    }

    public OrderedListRow(E e, ToggleSwitch class754Var, ToggleAnimator class323Var) {
        this.value = e;
        this.toggleSwitch = class754Var;
        this.toggleAnimation = class323Var;
    }
}
