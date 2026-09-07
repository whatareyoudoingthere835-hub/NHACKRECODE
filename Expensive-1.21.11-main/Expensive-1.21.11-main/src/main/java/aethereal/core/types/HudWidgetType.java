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

public enum HudWidgetType implements DisplayNamed {
    WATERMARK(Translation.clearText("Watermark")),
    POTION_LIST(Translation.clearText("Potion List")),
    TARGET_HUD(Translation.clearText("Target Hud")),
    HOTKEYS(Translation.clearText("Hotkeys")),
    COORDS(Translation.clearText("Coords")),
    ITEM_BIND(Translation.clearText("Item Bind")),
    STAFF_LIST(Translation.clearText("Staff List")),
    NOTIFICATION(Translation.clearText("Notifications")),
    TRAP_TIMER(Translation.clearText("Trap Timer")),
    MEDIA_PLAYER(Translation.clearText("Music Player"));

    public final Translation displayName;

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public Translation displayName() {
        return this.displayName;
    }

    HudWidgetType(Translation class254Var) {
        this.displayName = class254Var;
    }
}
