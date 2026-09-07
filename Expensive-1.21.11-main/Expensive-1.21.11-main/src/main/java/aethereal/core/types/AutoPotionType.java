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

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

public enum AutoPotionType implements DisplayNamed {
    FIRE_RESISTANCE(Lang.AUTOPOTION_POTIONS_FIRERESISTANCE, StatusEffects.FIRE_RESISTANCE),
    STRENGTH(Lang.AUTOPOTION_POTIONS_STRENGTH, StatusEffects.STRENGTH),
    SPEED(Lang.AUTOPOTION_POTIONS_SPEED, StatusEffects.SPEED);

    public final Translation displayName;
    public final RegistryEntry<StatusEffect> effect;

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public RegistryEntry<StatusEffect> getEffect() {
        return this.effect;
    }

    AutoPotionType(Translation class254Var, RegistryEntry registryEntry) {
        this.displayName = class254Var;
        this.effect = registryEntry;
    }
}
