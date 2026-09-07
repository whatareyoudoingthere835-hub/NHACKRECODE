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

import net.minecraft.entity.effect.StatusEffects;

@Aliases(aliases = {"No Bad Effects", "No Effects", "Remove Effects", "Remove Bad Effects", "Anti Bad Effects"})
public class RemoveEffectsModule extends Module {
    public final MultiSelectSetting<RemovedEffectType> removedEffects;

    public RemoveEffectsModule() {
        super(ModuleTab.PLAYER, "Remove Effects");
        this.removedEffects = new MultiSelectSetting(Lang.REMOVEEFFECTS_POTIONS, Lang.REMOVEEFFECTS_POTIONS_DESC).values(RemovedEffectType.class);
        addSettings(this.removedEffects);
        register(VisualEffectEvent.class, class258Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                VisualEffectType type= class258Var.getType();
                if ((type.isBlindness() && this.removedEffects.isSelected(RemovedEffectType.BLINDNESS)) || ((type.isDarkness() && this.removedEffects.isSelected(RemovedEffectType.DARKNESS)) || (type.isNausea() && this.removedEffects.isSelected(RemovedEffectType.NAUSEA)))) {
                    class258Var.cancel();
                }
            }
        });
        register(StatusEffectEvent.class, class046Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded() && class046Var.getEffect() == StatusEffects.JUMP_BOOST && this.removedEffects.isSelected(RemovedEffectType.JUMP_BOOST)) {
                class046Var.cancel();
            }
        });
    }
}
