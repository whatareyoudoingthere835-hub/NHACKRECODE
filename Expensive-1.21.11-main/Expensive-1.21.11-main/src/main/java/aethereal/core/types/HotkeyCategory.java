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

public enum HotkeyCategory implements DisplayNamed {
    COMBAT(Lang.COMBAT, ModuleTab.COMBAT),
    MOVEMENT(Lang.MOVEMENT, ModuleTab.MOVEMENT),
    PLAYER(Lang.PLAYER, ModuleTab.PLAYER),
    RENDER(Lang.RENDER, ModuleTab.RENDER),
    MISC(Lang.MISC, ModuleTab.MISC);

    public final Translation displayName;
    public final ModuleTab tab;

    public static HotkeyCategory fromTab(ModuleTab class847Var) {
        for (HotkeyCategory class641Var : values()) {
            if (class641Var.tab == class847Var) {
                return class641Var;
            }
        }
        return null;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public ModuleTab getTab() {
        return this.tab;
    }

    HotkeyCategory(Translation class254Var, ModuleTab class847Var) {
        this.displayName = class254Var;
        this.tab = class847Var;
    }
}
