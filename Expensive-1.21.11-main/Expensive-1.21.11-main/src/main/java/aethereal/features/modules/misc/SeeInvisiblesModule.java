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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

@Aliases(aliases = {"Reveal Invisibles", "Invisible Detection", "See Hidden", "Invisible Vision", "Detect Invisibles", "Anti-Invisibility", "See Stealth", "See Invisibles", "Reveal Stealth Targets"})
public class SeeInvisiblesModule extends Module {
    public MultiSelectSetting<TargetSelectionType> targetsSetting;

    public SeeInvisiblesModule() {
        super(ModuleTab.MISC, "See Invisibles");
        this.targetsSetting = new MultiSelectSetting(Lang.SELECTTARGETS).values(TargetSelectionType.class);
        addSettings(this.targetsSetting);
        register(EntityInvisibilityEvent.class, class198Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                LivingEntity livingEntityEntity= (LivingEntity) (class198Var.entity());
                if ((livingEntityEntity instanceof LivingEntity) && livingEntityEntity.hasStatusEffect(StatusEffects.GLOWING)) {
                    return;
                }
                EntityFilter class095Var= new EntityFilter();
                if (this.targetsSetting.isSelected(TargetSelectionType.SELF)) {
                    class095Var.add(EntityCategory.SELF);
                }
                if (this.targetsSetting.isSelected(TargetSelectionType.PLAYERS)) {
                    class095Var.add(EntityCategory.PLAYER);
                }
                if (this.targetsSetting.isSelected(TargetSelectionType.FRIENDS)) {
                    class095Var.add(EntityCategory.FRIEND);
                }
                if (this.targetsSetting.isSelected(TargetSelectionType.MOBS)) {
                    class095Var.add(EntityCategory.MOB);
                }
                if (this.targetsSetting.isSelected(TargetSelectionType.ANIMALS)) {
                    class095Var.add(EntityCategory.ANIMAL);
                }
                if (class095Var.matches(class198Var.entity())) {
                    class198Var.cancel();
                }
            }
        });
    }
}
