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

public enum SettingUnit {
    UNITS(Lang.UNIT_UNITS),
    BLOCKS(Lang.UNIT_BLOCKS),
    TICKS(Lang.UNIT_TICKS),
    MILLISECONDS(Lang.UNIT_MILLISECONDS),
    SECONDS(Lang.UNIT_SECONDS),
    HITPOINTS(Lang.UNIT_HITPOINTS),
    PERCENTS(Lang.UNIT_PERCENTS),
    DEGREES(Lang.UNIT_DEGREES),
    PIXELS(Lang.UNIT_PIXELS);

    public final Translation unitName;

    public String format(float f) {
        return this.unitName.effective();
    }

    public Translation getUnitName() {
        return this.unitName;
    }

    SettingUnit(Translation class254Var) {
        this.unitName = class254Var;
    }
}
