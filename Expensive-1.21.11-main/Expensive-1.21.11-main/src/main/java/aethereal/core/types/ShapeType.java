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

public enum ShapeType {
    COLOR(0),
    TEXTURE(1),
    ROUNDED_RECTANGLE(2),
    ROUNDED_TEXTURE(3),
    BLUR(4),
    CHECKER(5),
    CIRCLE(6),
    OUTER_MASK(7),
    ALPHA_MASK(8),
    MSDF_FONT(9),
    RADIAL_ROUNDED_RECTANGLE(10);

    public final int mode;

    public int mode() {
        return this.mode;
    }

    ShapeType(int i) {
        this.mode = i;
    }
}
