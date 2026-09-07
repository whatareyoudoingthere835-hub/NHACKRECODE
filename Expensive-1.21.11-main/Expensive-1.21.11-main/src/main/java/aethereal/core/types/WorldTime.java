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

public enum WorldTime implements DisplayNamed {
    DAY(Lang.WORLD_TWEAKS_DAY, 1000),
    NIGHT(Lang.WORLD_TWEAKS_NIGHT, 18000),
    SUNSET(Lang.WORLD_TWEAKS_SUNSET, 12000),
    MIDNIGHT(Lang.WORLD_TWEAKS_MIDNIGHT, 13000),
    SUNRISE(Lang.WORLD_TWEAKS_SUNRISE, 23000);

    public final Translation displayName;
    public final int ticks;

    WorldTime(Translation class254Var, int i) {
        this.displayName = class254Var;
        this.ticks = i;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public int ticks() {
        return this.ticks;
    }
}
