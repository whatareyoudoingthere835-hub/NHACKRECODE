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

public class DraggableContainer extends WidgetContainer {
    public boolean dragging;
    public float dragOffsetX;
    public float dragOffsetY;
    public ScreenResolution screenResolution;
    public float logicalWidth;
    public float logicalHeight;

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) {
        InputEvent class691VarInputEvent= class688Var.inputEvent();
        if (z && !this.dragging) {
            return false;
        }
        if (class691VarInputEvent instanceof CursorMoveInput) {
            boolean zInArea= class688Var.inArea(x(), y(), width(), height());
            if (!this.dragging) {
                return zInArea;
            }
            float fX= class688Var.logicalMousePosition().x() - this.dragOffsetX;
            float fY= class688Var.logicalMousePosition().y() - this.dragOffsetY;
            if (shouldClampPosition(this.screenResolution.width(), this.screenResolution.height())) {
                float fWidth= this.logicalWidth - this.screenResolution.width();
                float fHeight= this.logicalHeight - this.screenResolution.height();
                fX = FastMathUtils.clamp(fX, 0.0f, fWidth);
                fY = FastMathUtils.clamp(fY, 0.0f, fHeight);
            }
            this.screenResolution.setPosition(fX, fY);
            return true;
        }
        if (class691VarInputEvent instanceof MouseButtonInput) {
            MouseButtonInput class693Var= (MouseButtonInput) class691VarInputEvent;
            if (class693Var.button() == 0) {
                if (class693Var.action().press()) {
                    boolean zInArea2= class688Var.inArea(x(), y(), width(), height());
                    if (!z && zInArea2) {
                        this.dragging = true;
                        this.dragOffsetX = class688Var.logicalMousePosition().x() - this.screenResolution.x();
                        this.dragOffsetY = class688Var.logicalMousePosition().y() - this.screenResolution.y();
                        return true;
                    }
                }
                if (class693Var.action().release() && this.dragging) {
                    this.dragging = false;
                    return true;
                }
            }
        }
        return super.handleInput(class688Var, z);
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        this.screenResolution = class698Var.screenResolution();
        this.logicalWidth = class698Var.logicalWidth();
        this.logicalHeight = class698Var.logicalHeight();
        if (shouldClampPosition(this.screenResolution.width(), this.screenResolution.height())) {
            this.screenResolution.setPosition(FastMathUtils.clamp(this.screenResolution.x(), 0.0f, this.logicalWidth - this.screenResolution.width()), FastMathUtils.clamp(this.screenResolution.y(), 0.0f, this.logicalHeight - this.screenResolution.height()));
        }
        super.layout(class698Var);
    }

    public boolean shouldClampPosition(float f, float f2) {
        return this.screenResolution != null && this.logicalWidth > f && this.logicalHeight > f2;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean z) {
        this.dragging = z;
    }
}
