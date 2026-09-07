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

public enum ModuleTab {
    COMBAT(Lang.COMBAT, "combat"),
    MOVEMENT(Lang.MOVEMENT, "movement"),
    PLAYER(Lang.PLAYER, "player"),
    RENDER(Lang.RENDER, "render"),
    MISC(Lang.MISC, "misc"),
    EARNINGS(Lang.EARNINGS, "earnings"),
    AUTOBUY(Lang.AUTOBUY, "autobuy"),
    CONFIGS(Lang.CONFIGS, "cloud");

    public final Translation name;
    public final String iconName;

    public Translation getName() {
        return this.name;
    }

    public String getIconName() {
        return this.iconName;
    }

    ModuleTab(Translation class254Var, String str) {
        this.name = class254Var;
        this.iconName = str;
    }
}
