package aethereal.features.modules.misc;
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

public class OpenWallsModule extends Module {
    public NumberSetting maxDistance;

    public OpenWallsModule() {
        super(ModuleTab.MISC, "Open Walls");
        this.maxDistance = new NumberSetting(Lang.ATTACKAURA_MAX_DISTANCE).currentValue(4.5f).range(4.5f, 9.0f);
        addSettings(this.maxDistance);
        register(CrosshairRenderEvent.class, class247Var -> {
        });
    }
}
