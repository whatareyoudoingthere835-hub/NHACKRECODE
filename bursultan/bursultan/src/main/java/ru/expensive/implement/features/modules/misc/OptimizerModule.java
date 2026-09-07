package ru.expensive.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptimizerModule extends Module {

    static final int BACKGROUND_FPS = 10;
    int originalFpsLimit;

    public OptimizerModule() {
        super("Optimizer", "Optimizer", ModuleCategory.MISC);
    }

    @Override
    public void activate() {
        if (mc.options != null) {
            originalFpsLimit = mc.options.getMaxFps().getValue();
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        if (mc.options != null && getMaxFps() > 0) {
            mc.options.getMaxFps().setValue(getMaxFps());
        }
        super.deactivate();
    }

    public int getMaxFps() {
        return mc.options != null ? mc.options.getMaxFps().getValue() : 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!isState() || mc.options == null) return;

        if (!mc.isWindowFocused() || mc.isPaused()) {
            mc.options.getMaxFps().setValue(BACKGROUND_FPS);
        } else if (originalFpsLimit > 0) {
            mc.options.getMaxFps().setValue(originalFpsLimit);
        }
    }
}