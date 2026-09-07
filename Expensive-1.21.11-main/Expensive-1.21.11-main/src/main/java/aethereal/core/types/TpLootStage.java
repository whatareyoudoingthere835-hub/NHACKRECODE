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

public enum TpLootStage {
    APPROACH(Lang.CREEPERFARM_PHASE_APPROACH),
    LOADING_CHUNKS(Lang.CREEPERFARM_PHASE_LOADING_CHUNKS),
    LOOTING(Lang.CREEPERFARM_PHASE_LOOTING),
    UNLOADING(Lang.CREEPERFARM_PHASE_UNLOADING);

    public final Translation name;

    TpLootStage(Translation class254Var) {
        this.name = class254Var;
    }

    public Translation getName() {
        return this.name;
    }
}
