package aethereal.features.modules.movement;
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

@Aliases(aliases = {"Auto Jump", "Auto Hop", "Jump Assist", "Jump Bot", "Automatic Jump", "Movement Jump"})
public class AutoJumpModule extends Module {
    final BooleanSetting onlyWithAura;

    public AutoJumpModule() {
        super(ModuleTab.MOVEMENT, "Auto Jump");
        this.onlyWithAura = new BooleanSetting(Lang.ARM_TWEAKS_SWING_ANIMATION_ONLY_AURA);
        addSettings(this.onlyWithAura);
        register(MovementInputEvent.class, class040Var -> {
            Mc class815Var= Mc.INSTANCE;
            if (isState() && class815Var.isWorldLoaded()) {
                if (class815Var.getPlayer().isOnGround() || (class815Var.getPlayer().isTouchingWater() && !class815Var.getPlayer().isSubmergedInWater())) {
                    AttackAuraModule class878Var= (AttackAuraModule) Expensive.INSTANCE.moduleRepository().get(AttackAuraModule.class);
                    if (!this.onlyWithAura.isValue() || (class878Var.isState() && class878Var.target() != null)) {
                        class040Var.setJumping(true);
                    }
                }
            }
        }, EventPriority.LOW);
    }
}
