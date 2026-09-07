package ru.expensive.api.feature.module;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;
import ru.expensive.api.feature.module.exception.ModuleException;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.event.EventHandler;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.api.system.logger.implement.ConsoleLogger;
import ru.expensive.common.QuickLogger;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleSwitcher implements QuickLogger {
    List<Module> modules;

    public ModuleSwitcher(List<Module> modules, EventManager eventManager) {
        this.modules = modules;
        eventManager.register(this);
    }

    @EventHandler
    public void onKey(KeyEvent event) {
        for (Module module : modules) {
            if (event.getKey() == module.getKey()) {
                try {
                    handleModuleState(module, event.getAction());
                } catch (Exception e) {
                    handleException(module.getName(), e);
                }
                break;
            }
        }
    }

    private void handleModuleState(Module module, int action) {
        if (module.getType() == 1) {
            if (action == 1) {
                module.switchState();
            }
        } else if (module.getType() == 0) {
            if (action == 1) {
                module.setState(true);
            } else if (action == 0) {
                module.setState(false);
            }
        }
    }

    private void handleException(String moduleName, Exception e) {
        final ConsoleLogger consoleLogger = new ConsoleLogger();

        if (e instanceof ModuleException) {
            logDirect("[" + moduleName + "] " + Formatting.RED + e.getMessage());
        } else {
            consoleLogger.log("Error in module " + moduleName + ": " + e.getMessage());
        }
    }
}
