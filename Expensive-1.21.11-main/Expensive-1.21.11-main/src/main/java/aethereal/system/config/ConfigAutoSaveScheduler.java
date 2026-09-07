package aethereal.system.config;
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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ConfigAutoSaveScheduler {
    public static final long autoSaveDelayMs = 1200;
    public static final Object lock = new Object();
    public static ScheduledFuture<?> pendingSave;

    public ConfigAutoSaveScheduler() {
    }

    public static void scheduleAutoSave() {
        CloudConfigService class357VarCloudConfigService= Expensive.INSTANCE.cloudConfigService();
        class357VarCloudConfigService.activeConfig().ifPresent(class304Var -> {
            if (Expensive.INSTANCE.configManager().menuStateConfig().isAutoSaveDisabled(class304Var.id())) {
                return;
            }
            synchronized (lock) {
                if (pendingSave != null && !pendingSave.isDone()) {
                    pendingSave.cancel(false);
                }
                pendingSave = Expensive.INSTANCE.executor().schedule(() -> {
                    class357VarCloudConfigService.saveConfig(class304Var.id(), Expensive.INSTANCE.moduleRepository(), Expensive.INSTANCE.widgetStack()).thenRun(() -> {
                        Expensive.LOGGER.info("Config auto-saved: {}", class304Var.id());
                    }).exceptionally(th -> {
                        Expensive.LOGGER.error("Failed to auto-save config", th);
                        return null;
                    });
                }, autoSaveDelayMs, TimeUnit.MILLISECONDS);
            }
        });
    }
}
