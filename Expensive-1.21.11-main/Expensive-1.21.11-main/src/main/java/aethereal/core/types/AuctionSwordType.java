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

public enum AuctionSwordType implements DisplayNamed {
    SHARPNESS(Lang.AUCTION_HELPER_SWORDS_SHARPNESS),
    UNBREAKING(Lang.AUCTION_HELPER_SWORDS_UNBREAKING),
    POISON(Lang.AUCTION_HELPER_SWORDS_POISON),
    DETECTION(Lang.AUCTION_HELPER_SWORDS_DETECTION),
    OXIDATION(Lang.AUCTION_HELPER_SWORDS_OXIDATION),
    VAMPIRISM(Lang.AUCTION_HELPER_SWORDS_VAMPIRISM),
    WITHOUT_KNOCKBACK(Lang.AUCTION_HELPER_SWORDS_WITHOUT_KNOCKBACK);

    final Translation displayName;

    AuctionSwordType(Translation class254Var) {
        this.displayName = class254Var;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }
}
