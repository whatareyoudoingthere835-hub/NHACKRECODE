package aethereal.system.events;
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

import net.minecraft.client.sound.SoundInstance;

public class SoundPlayEvent extends CancellableEvent {
    public final SoundInstance soundInstance;
    public float volumeMultiplier = 1.0f;

    public SoundInstance getSoundInstance() {
        return this.soundInstance;
    }

    public float getVolumeMultiplier() {
        return this.volumeMultiplier;
    }

    public void setVolumeMultiplier(float f) {
        this.volumeMultiplier = f;
    }

    public SoundPlayEvent(SoundInstance soundInstance) {
        this.soundInstance = soundInstance;
    }
}
