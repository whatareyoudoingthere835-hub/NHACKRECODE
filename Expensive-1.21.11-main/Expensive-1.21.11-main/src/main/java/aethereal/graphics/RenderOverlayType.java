package aethereal.graphics;
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

public enum RenderOverlayType {
    CAMERA_HURT,
    FIRE_OVERLAY,
    GLOWING,
    LAVA_OVERLAY,
    SCOREBOARD,
    BOSS_BAR,
    TOTEM_POP,
    WITHER_HEARTS;

    public boolean isBossBar() {
        return this == BOSS_BAR;
    }

    public boolean isCameraHurt() {
        return this == CAMERA_HURT;
    }

    public boolean isFireOverlay() {
        return this == FIRE_OVERLAY;
    }

    public boolean isGlowing() {
        return this == GLOWING;
    }

    public boolean isLavaOverlay() {
        return this == LAVA_OVERLAY;
    }

    public boolean isScoreboard() {
        return this == SCOREBOARD;
    }

    public boolean isTotemPop() {
        return this == TOTEM_POP;
    }

    public boolean isWitherHearts() {
        return this == WITHER_HEARTS;
    }
}
