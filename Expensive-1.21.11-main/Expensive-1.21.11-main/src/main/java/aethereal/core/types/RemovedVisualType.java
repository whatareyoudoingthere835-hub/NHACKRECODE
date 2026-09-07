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

public enum RemovedVisualType implements DisplayNamed {
    CAMERA_CLIP(Lang.REMOVALS_CAMERA_CLIP),
    CAMERA_HURT(Lang.REMOVALS_CAMERA_HURT),
    FIRE_OVERLAY(Lang.REMOVALS_FIRE_OVERLAY),
    LAVA_OVERLAY(Lang.REMOVALS_LAVA_OVERLAY),
    SCOREBOARD(Lang.REMOVALS_SCOREBOARD),
    BOSS_BAR(Lang.REMOVALS_BOSS_BAR),
    TOTEM_POP(Lang.REMOVALS_TOTEM_POP),
    GLOWING(Lang.REMOVALS_GLOWING),
    WITHER_HEARTS(Lang.REMOVALS_WITHER_HEARTS);

    final Translation displayName;

    RemovedVisualType(Translation class254Var) {
        this.displayName = class254Var;
    }

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }
}
