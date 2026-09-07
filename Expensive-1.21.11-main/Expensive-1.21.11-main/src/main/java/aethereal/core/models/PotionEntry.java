package aethereal.core.models;
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

import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public final class PotionEntry {
    public RegistryEntry<StatusEffect> effect;
    public int amplifier;
    public int duration;
    public int maxDuration;
    public int lastUpdateTick;
    public boolean infinite;
    public boolean hasBeenActive;
    public boolean expiryNotified;
    public boolean harmful;
    public final ToggleAnimator animator = ToggleAnimator.times(2, 80);
    public String translationKey = "";
    public String durationText = "";
    public int previousDuration = Integer.MAX_VALUE;

    public PotionEntry() {
    }

    public String getDisplayName() {
        String strTranslate= I18n.translate(this.translationKey, new Object[0]);
        return this.amplifier > 0 ? strTranslate + " " + (this.amplifier + 1) : strTranslate;
    }
}
