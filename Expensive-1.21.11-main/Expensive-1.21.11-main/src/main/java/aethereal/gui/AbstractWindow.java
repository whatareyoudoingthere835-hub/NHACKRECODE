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

public abstract class AbstractWindow extends WidgetContainer {
    public WindowVisibilityListener windowVisibilityListener;

    public void visibilityListener(WindowVisibilityListener class685Var) {
        this.windowVisibilityListener = class685Var;
    }

    public void onVisibilityChanged(boolean z) {
        if (this.windowVisibilityListener != null) {
            this.windowVisibilityListener.onWindowVisibilityChanged(this, z);
        }
    }

    public void open() {
        visible(true);
        onVisibilityChanged(true);
    }

    public void close() {
        visible(false);
        onVisibilityChanged(false);
        handleClose();
    }

    public void toggle() {
        if (this.visible) {
            close();
        } else {
            open();
        }
    }

    public AbstractWindow(float f, float f2) {
        setSize(f, f2);
    }

    public void center(LayoutScaleContext class698Var) {
        setPosition(class698Var.alignToScale((class698Var.logicalWidth() - width()) / 2.0f), class698Var.alignToScale((class698Var.logicalHeight() - height()) / 2.0f));
    }
}
