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

public class SeparatorElement extends ModuleFrame {
    @Override
    public void highlight() {
    }

    @Override
    public void clearHighlight() {
    }

    @Override
    public void draw(DrawCtx class699Var, float f, float f2) {
        class699Var.fillRect(x(), y(), f, 1.0f, class699Var.drawEngine().colorStack().computeColor(class699Var.theme().palette().surfaceOutline().tone(600).argb()));
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
    }

    @Override
    public float height() {
        return 1.0f;
    }
}
