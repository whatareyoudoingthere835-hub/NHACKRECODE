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

public enum ClientSoundType implements DisplayNamed {
    TYPE_1(Lang.CLIENTSOUNDS_TYPE_1),
    TYPE_2(Lang.CLIENTSOUNDS_TYPE_2),
    TYPE_3(Lang.CLIENTSOUNDS_TYPE_3),
    TYPE_4(Lang.CLIENTSOUNDS_TYPE_4),
    TYPE_5(Lang.CLIENTSOUNDS_TYPE_5),
    TYPE_6(Lang.CLIENTSOUNDS_TYPE_6),
    TYPE_7(Lang.CLIENTSOUNDS_TYPE_7);

    public final Translation displayName;

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    ClientSoundType(Translation class254Var) {
        this.displayName = class254Var;
    }
}
