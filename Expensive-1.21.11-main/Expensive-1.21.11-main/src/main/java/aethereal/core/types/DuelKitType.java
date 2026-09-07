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

public enum DuelKitType implements DisplayNamed {
    SHIELD(Translation.clearText("Щит")),
    THORNS(Translation.clearText("Шипы 3")),
    BOW(Translation.clearText("Лук")),
    TOTEMS(Translation.clearText("Тотемы")),
    NO_DEBUFF(Translation.clearText("nodebaff")),
    BALLS(Translation.clearText("shari")),
    CLASSIC(Translation.clearText("classic")),
    CHEATER_PARADISE(Translation.clearText("chiterskii rai")),
    NETHERITE(Translation.clearText("nezerka"));

    public final Translation displayName;

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public Translation displayName() {
        return this.displayName;
    }

    DuelKitType(Translation class254Var) {
        this.displayName = class254Var;
    }
}
