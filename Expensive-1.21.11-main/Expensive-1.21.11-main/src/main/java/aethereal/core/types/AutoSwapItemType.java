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

import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum AutoSwapItemType implements DisplayNamed {
    SHIELD(Lang.COMBAT_AUTOSWAP_VALUE_SHIELD, Items.SHIELD, itemStack -> {
        return itemStack.getItem() == Items.SHIELD;
    }),
    GAPPLE(Lang.COMBAT_AUTOSWAP_VALUE_GAPPLE, Items.GOLDEN_APPLE, itemStack2 -> {
        return itemStack2.getItem() == Items.GOLDEN_APPLE || itemStack2.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }),
    SPHERE(Lang.COMBAT_AUTOSWAP_VALUE_SPHERE, Items.PLAYER_HEAD, itemStack3 -> {
        return itemStack3.getItem() == Items.PLAYER_HEAD;
    }),
    FIREWORK(Lang.COMBAT_AUTOSWAP_VALUE_FIREWORK, Items.FIREWORK_ROCKET, itemStack4 -> {
        return itemStack4.getItem() == Items.FIREWORK_ROCKET;
    }),
    TOTEM(Lang.COMBAT_AUTOSWAP_VALUE_TOTEM, Items.TOTEM_OF_UNDYING, itemStack5 -> {
        return itemStack5.getItem() == Items.TOTEM_OF_UNDYING;
    }),
    FOOD(Lang.COMBAT_AUTOSWAP_VALUE_FOOD, Items.COOKED_BEEF, itemStack6 -> {
        return itemStack6.get(DataComponentTypes.FOOD) != null;
    });

    public final Translation displayName;
    public final Item displayItem;
    public final Predicate<ItemStack> filter;

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public Translation displayName() {
        return this.displayName;
    }

    public Item displayItem() {
        return this.displayItem;
    }

    public Predicate<ItemStack> filter() {
        return this.filter;
    }

    AutoSwapItemType(Translation class254Var, Item item, Predicate<ItemStack> predicate) {
        this.displayName = class254Var;
        this.displayItem = item;
        this.filter = predicate;
    }
}
