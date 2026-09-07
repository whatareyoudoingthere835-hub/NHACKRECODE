package aethereal.utils;
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

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class FtHelperItemRules {
    public final Map<KeybindSetting, ItemSearchRule> rules = new LinkedHashMap();

    public FtHelperItemRules addByName(KeybindSetting class663Var, String str, Item item) {
        this.rules.put(class663Var, new ItemSearchRule(true, str, item, false));
        return this;
    }

    public FtHelperItemRules addPotionByName(KeybindSetting class663Var, String str, boolean z) {
        this.rules.put(class663Var, new ItemSearchRule(true, str, Items.SPLASH_POTION, z));
        return this;
    }

    public FtHelperItemRules addByItemFlagged(KeybindSetting class663Var, Item item, boolean z) {
        this.rules.put(class663Var, new ItemSearchRule(false, null, item, z));
        return this;
    }

    public FtHelperItemRules addByItem(KeybindSetting class663Var, Item item) {
        this.rules.put(class663Var, new ItemSearchRule(false, null, item, false));
        return this;
    }
}
