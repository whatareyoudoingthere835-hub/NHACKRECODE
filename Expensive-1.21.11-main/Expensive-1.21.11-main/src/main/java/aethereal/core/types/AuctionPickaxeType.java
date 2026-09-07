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

public enum AuctionPickaxeType implements DisplayNamed {
    MENDING(Lang.AUCTION_HELPER_PICKAXES_MENDING),
    EFFICIENCY(Lang.AUCTION_HELPER_PICKAXES_EFFICIENCY),
    FORTUNE(Lang.AUCTION_HELPER_PICKAXES_FORTUNE),
    UNBREAKING(Lang.AUCTION_HELPER_PICKAXES_UNBREAKING),
    SILK_TOUCH(Lang.AUCTION_HELPER_PICKAXES_SILK_TOUCH),
    SMELTING(Lang.AUCTION_HELPER_PICKAXES_SMELTING),
    MAGNET(Lang.AUCTION_HELPER_PICKAXES_MAGNET),
    BULLDOZING(Lang.AUCTION_HELPER_PICKAXES_BULLDOZING),
    WITHOUT_HEAVY(Lang.AUCTION_HELPER_PICKAXES_WITHOUT_HEAVY);

    final Translation displayName;

    AuctionPickaxeType(Translation class254Var) {
        this.displayName = class254Var;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }
}
