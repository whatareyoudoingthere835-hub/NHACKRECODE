package aethereal.features.modules.render;
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

import net.minecraft.client.render.WorldRenderer;

@Aliases(aliases = {"No Render", "Anti Render", "Removals", "Remove", "No Overlay", "Hide Overlay", "No Hurt Cam", "No Fire", "No Scoreboard", "Anti Hurt Cam"})
public class RemovalsModule extends Module {
    public final MultiSelectSetting<RemovedVisualType> removedVisuals;
    public final MultiSelectSetting<RemovedSoundCategory> removedSounds;
    public final NumberSetting soundVolume;

    public RemovalsModule() {
        super(ModuleTab.RENDER, "Removals");
        this.removedVisuals = new MultiSelectSetting(Lang.REMOVALS_ELEMENTS).values(RemovedVisualType.class);
        this.removedSounds = new MultiSelectSetting(Lang.REMOVALS_ANNOYING_SOUNDS).values(RemovedSoundCategory.class);
        this.soundVolume = new NumberSetting(Lang.REMOVALS_SOUND_VOLUME).currentValue(0.0f).range(0.0f, 100.0f).step(1.0f).unit(SettingUnit.PERCENTS).visible(() -> {
            return Boolean.valueOf(!this.removedSounds.selectedValues().isEmpty());
        });
        addSettings(this.removedVisuals, this.removedSounds, this.soundVolume);
        register(RenderOverlayEvent.class, class252Var -> {
            if (Mc.INSTANCE.isWorldLoaded() && isState()) {
                RenderOverlayType type= class252Var.getType();
                if ((type.isCameraHurt() && this.removedVisuals.isSelected(RemovedVisualType.CAMERA_HURT)) || ((type.isFireOverlay() && this.removedVisuals.isSelected(RemovedVisualType.FIRE_OVERLAY)) || ((type.isLavaOverlay() && this.removedVisuals.isSelected(RemovedVisualType.LAVA_OVERLAY)) || ((type.isScoreboard() && this.removedVisuals.isSelected(RemovedVisualType.SCOREBOARD)) || ((type.isBossBar() && this.removedVisuals.isSelected(RemovedVisualType.BOSS_BAR)) || ((type.isTotemPop() && this.removedVisuals.isSelected(RemovedVisualType.TOTEM_POP)) || ((type.isGlowing() && this.removedVisuals.isSelected(RemovedVisualType.GLOWING)) || (type.isWitherHearts() && this.removedVisuals.isSelected(RemovedVisualType.WITHER_HEARTS))))))))) {
                    class252Var.cancel();
                }
            }
        });
        register(SoundPlayEvent.class, class017Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                String path= class017Var.getSoundInstance().getId().getPath();
                if (this.removedSounds.selectedValues().stream().anyMatch(class567Var -> {
                    return class567Var.matches(path);
                })) {
                    float fCurrentValue= this.soundVolume.currentValue();
                    if (fCurrentValue <= 0.0f) {
                        class017Var.cancel();
                    } else {
                        class017Var.setVolumeMultiplier(fCurrentValue / 100.0f);
                    }
                }
            }
        });
        register(CameraClipEvent.class, class209Var -> {
            if (Mc.INSTANCE.isWorldLoaded() && isState() && this.removedVisuals.isSelected(RemovedVisualType.CAMERA_CLIP)) {
                class209Var.cancel();
            }
        });
    }

    @Override
    public void activate() {
        reload();
        super.activate();
    }

    @Override
    public void deactivate() {
        reload();
        super.deactivate();
    }

    public void reload() {
        WorldRenderer worldRenderer;
        if (Mc.INSTANCE.isWorldLoaded() && (worldRenderer = Mc.INSTANCE.getMinecraft().worldRenderer) != null && isState()) {
            worldRenderer.reload();
        }
    }
}
