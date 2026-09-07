package aethereal.features.modules.player;
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

@Aliases(aliases = {"No Push", "Anti Push", "No Collision", "Anti Collision"})
public class NoPushModule extends Module {
    public final MultiSelectSetting<NoPushTarget> targetSetting;

    public NoPushModule() {
        super(ModuleTab.PLAYER, "No Push");
        this.targetSetting = new MultiSelectSetting(Lang.PLAYER_NOPUSH_TARGET).values(NoPushTarget.class);
        addSettings(this.targetSetting);
        register(PushEvent.class, class231Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                PushType type= class231Var.getType();
                if ((type.isPlayers() && this.targetSetting.isSelected(NoPushTarget.PLAYERS)) || ((type.isWater() && this.targetSetting.isSelected(NoPushTarget.WATER)) || (type.isBlocks() && this.targetSetting.isSelected(NoPushTarget.BLOCKS)))) {
                    class231Var.cancel();
                }
            }
        });
    }
}
