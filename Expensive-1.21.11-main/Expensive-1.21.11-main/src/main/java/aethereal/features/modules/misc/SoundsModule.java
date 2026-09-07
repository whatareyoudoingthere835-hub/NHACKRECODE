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

@Aliases(aliases = {"Client Sounds", "Module Sounds", "Sound Effects", "Sounds", "Toggle Sounds"})
public class SoundsModule extends Module {
    public final EnumSetting<ClientSoundType> soundTypeSetting;
    public final NumberSetting volumeSetting;
    public boolean playEnableSound;

    public SoundsModule() {
        super(ModuleTab.MISC, "Sounds");
        this.soundTypeSetting = new EnumSetting(Lang.CLIENTSOUNDS_TYPE).values(ClientSoundType.class);
        this.volumeSetting = new NumberSetting(Lang.CLIENTSOUNDS_VOLUME).currentValue(70.0f).range(1.0f, 100.0f).step(1.0f).unit(SettingUnit.PERCENTS);
        this.playEnableSound = true;
        addSettings(this.soundTypeSetting, this.volumeSetting);
        this.soundTypeSetting.soundAction(this::playPreviewSound);
        register(ModuleStateEvent.class, class080Var -> {
            if (isState()) {
                WavSoundPlayer.INSTANCE.playSound(class080Var.moduleState() ? "module_enable_" + (this.soundTypeSetting.selectedIndex() + 1) : "module_disable_" + (this.soundTypeSetting.selectedIndex() + 1), this.volumeSetting.currentValue(), false);
            }
        });
    }

    public void playPreviewSound(ClientSoundType class489Var) {
        int iMethod002= indexOf(class489Var);
        WavSoundPlayer.INSTANCE.playSound(this.playEnableSound ? "module_enable_" + (iMethod002 + 1) : "module_disable_" + (iMethod002 + 1), this.volumeSetting.currentValue(), false);
        this.playEnableSound = !this.playEnableSound;
    }

    public int indexOf(ClientSoundType class489Var) {
        ClientSoundType[] class489VarArrValues= ClientSoundType.values();
        for (int i = 0; i < class489VarArrValues.length; i++) {
            if (class489VarArrValues[i] == class489Var) {
                return i;
            }
        }
        return 0;
    }
}
