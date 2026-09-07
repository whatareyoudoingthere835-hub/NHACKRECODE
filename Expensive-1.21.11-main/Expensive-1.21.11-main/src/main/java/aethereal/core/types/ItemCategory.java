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

public enum ItemCategory implements DisplayNamed {
    ARMOR(Lang.AUCTION_HELPER_EXCLUDES_ARMOR),
    ELYTRA(Lang.AUCTION_HELPER_EXCLUDES_ELYTRA),
    TOTEMS(Lang.AUCTION_HELPER_EXCLUDES_TOTEMS),
    PLAYER_HEADS(Lang.AUCTION_HELPER_EXCLUDES_PLAYER_HEADS),
    SWORDS(Lang.AUCTION_HELPER_EXCLUDES_SWORDS),
    PICKAXE(Lang.AUCTION_HELPER_EXCLUDES_PICKAXE),
    AXE(Lang.AUCTION_HELPER_EXCLUDES_AXE),
    SHOVEL(Lang.AUCTION_HELPER_EXCLUDES_SHOVEL),
    HOE(Lang.AUCTION_HELPER_EXCLUDES_HOE),
    TRIDENT(Lang.AUCTION_HELPER_EXCLUDES_TRIDENT),
    BOWS(Lang.AUCTION_HELPER_EXCLUDES_BOWS),
    CROSSBOWS(Lang.AUCTION_HELPER_EXCLUDES_CROSSBOWS),
    ARROWS(Lang.AUCTION_HELPER_EXCLUDES_ARROWS),
    FLINT_AND_STEEL(Lang.AUCTION_HELPER_EXCLUDES_FLINT_AND_STEEL),
    HORSE_ARMOR(Lang.AUCTION_HELPER_EXCLUDES_HORSE_ARMOR),
    POTIONS(Lang.AUCTION_HELPER_EXCLUDES_POTIONS),
    GLASS_BOTTLES(Lang.AUCTION_HELPER_EXCLUDES_GLASS_BOTTLES),
    DRAGON_BREATH(Lang.AUCTION_HELPER_EXCLUDES_DRAGON_BREATH);

    final Translation displayName;

    ItemCategory(Translation class254Var) {
        this.displayName = class254Var;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }
}
