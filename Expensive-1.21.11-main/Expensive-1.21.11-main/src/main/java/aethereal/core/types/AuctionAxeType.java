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

public enum AuctionAxeType implements DisplayNamed {
    EFFICIENCY(Lang.AUCTION_HELPER_AXES_EFFICIENCY),
    FORTUNE(Lang.AUCTION_HELPER_AXES_FORTUNE),
    MENDING(Lang.AUCTION_HELPER_AXES_MENDING),
    PINGER(Lang.AUCTION_HELPER_AXES_PINGER),
    MAGNET(Lang.AUCTION_HELPER_AXES_MAGNET),
    LUMBERJACK(Lang.AUCTION_HELPER_AXES_LUMBERJACK),
    BULLDOZING(Lang.AUCTION_HELPER_SHOVELS_BULLDOZING),
    UNBREAKING(Lang.AUCTION_HELPER_AXES_UNBREAKING);

    final Translation displayName;

    AuctionAxeType(Translation class254Var) {
        this.displayName = class254Var;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }
}
