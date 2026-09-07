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

public class WidgetBounds {
    public float x;
    public float y;
    public float width;
    public float height;

    public boolean containsPhysical(float f, float f2, float f3) {
        return containsLogical(f / f3, f2 / f3);
    }

    public WidgetBounds withPosition(float f, float f2) {
        this.x = f;
        this.y = f2;
        return this;
    }

    public WidgetBounds withSize(float f, float f2) {
        this.width = f;
        this.height = f2;
        return this;
    }

    public boolean containsLogical(float f, float f2) {
        return f >= this.x && f2 >= this.y && f <= this.x + this.width && f2 <= this.y + this.height;
    }

    public float right() {
        return this.x + this.width;
    }

    public float bottom() {
        return this.y + this.height;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }

    public WidgetBounds x(float f) {
        this.x = f;
        return this;
    }

    public WidgetBounds y(float f) {
        this.y = f;
        return this;
    }

    public WidgetBounds width(float f) {
        this.width = f;
        return this;
    }

    public WidgetBounds height(float f) {
        this.height = f;
        return this;
    }

    public WidgetBounds(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.width = f3;
        this.height = f4;
    }
}
