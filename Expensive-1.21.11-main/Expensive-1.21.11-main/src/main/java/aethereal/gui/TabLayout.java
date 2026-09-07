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

public interface TabLayout {
    default void initialize(MenuTabElement class732Var) {
    }

    public void render(DrawCtx class699Var);

    default void renderOverlays(DrawCtx class699Var) {
    }

    public void layout(LayoutScaleContext class698Var);

    public void animation(WeightedEngine class141Var);

    public boolean handleInput(InputEventContext class688Var, boolean z);

    public float getContentHeight();

    public void positionFrames();

    public float width();

    public float height();
}
