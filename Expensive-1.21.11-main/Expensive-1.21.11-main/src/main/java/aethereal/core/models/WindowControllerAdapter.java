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

public class WindowControllerAdapter {
    public final WindowController controller;
    public PixelPoint mousePosition = new PixelPoint(0, 0);

    public void preBlitFramebufferToBackbuffer(long j) throws MatchException {
        this.controller.draw(j);
        PixelPoint newPos= this.controller.window().determineMousePosition();
        if (newPos != null && !newPos.equals(this.mousePosition)) {
            handleInput(new CursorMoveInput(newPos));
        }
    }

    public void handleResize(int i, int i2) {
        this.controller.handleResize(i, i2);
    }

    public boolean handleInput(InputEvent class691Var) throws MatchException {
        if (class691Var instanceof CursorMoveInput) {
            try {
                this.mousePosition = ((CursorMoveInput) class691Var).mousePosition();
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        return this.controller.handleInput(new InputEventContext(class691Var, this.mousePosition, this.controller.dpiScaleFactor()));
    }

    public boolean interceptKeyboard() {
        return this.controller.interceptKeyboardIfScreenPresent() || this.controller.interceptKeyboard();
    }

    public boolean interceptMouse() {
        return this.controller.interceptCursorIfScreenNotPresent();
    }

    public WindowControllerAdapter(WindowController class686Var) {
        this.controller = class686Var;
    }
}
