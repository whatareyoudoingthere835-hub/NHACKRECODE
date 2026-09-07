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

public class InputEventContext {
    public final InputEvent inputEvent;
    public final float scaleFactor;
    public PixelPoint currentMousePosition;

    public InputEventContext(InputEvent class691Var, PixelPoint class708Var, float f) throws MatchException {
        this.inputEvent = class691Var;
        this.scaleFactor = f;
        this.currentMousePosition = class708Var;
        if (class691Var instanceof CursorMoveInput) {
            try {
                this.currentMousePosition = ((CursorMoveInput) class691Var).mousePosition();
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
    }

    public PixelPoint currentMousePosition() {
        return this.currentMousePosition;
    }

    public InputEventContext withMouseOffset(float f, float f2) {
        return new OffsetInputContext(this, f, f2);
    }

    public PixelPoint logicalMousePosition() {
        return toLogical(this.currentMousePosition, this.scaleFactor);
    }

    public boolean inArea(float f, float f2, float f3, float f4) {
        PixelPoint class708VarLogicalMousePosition= logicalMousePosition();
        return ((float) class708VarLogicalMousePosition.x()) >= f && ((float) class708VarLogicalMousePosition.y()) >= f2 && ((float) class708VarLogicalMousePosition.x()) <= f + f3 && ((float) class708VarLogicalMousePosition.y()) <= f2 + f4;
    }

    public boolean inArea(PixelPoint class708Var, float f, float f2, float f3, float f4) {
        PixelPoint logical= toLogical(class708Var, this.scaleFactor);
        return ((float) logical.x()) >= f && ((float) logical.y()) >= f2 && ((float) logical.x()) <= f + f3 && ((float) logical.y()) <= f2 + f4;
    }

    public boolean inPhysicalArea(PixelPoint class708Var, float f, float f2, float f3, float f4) {
        return ((float) class708Var.x()) >= f && ((float) class708Var.y()) >= f2 && ((float) class708Var.x()) <= f + f3 && ((float) class708Var.y()) <= f2 + f4;
    }

    public PixelPoint toLogical(PixelPoint class708Var, float f) {
        return (f <= 0.0f || f == 1.0f) ? class708Var : new PixelPoint(class708Var.x() / f, class708Var.y() / f);
    }

    public InputEvent inputEvent() {
        return this.inputEvent;
    }

    public float scaleFactor() {
        return this.scaleFactor;
    }
}
