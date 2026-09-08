package thunder.hack.utility;

import thunder.hack.ThunderHack;

import java.lang.reflect.Field;

/**
 * Reflection-based soft bridge to baritone (no compile-time dependency).
 * All methods are no-ops when baritone is not present at runtime.
 */
public final class BaritoneUtility {
    private BaritoneUtility() {
    }

    private static Class<?> api() {
        try {
            return Class.forName("baritone.api.BaritoneAPI");
        } catch (Throwable t) {
            return null;
        }
    }

    public static void executeCommand(String command) {
        if (!ThunderHack.baritone) return;
        try {
            Class<?> api = api();
            if (api == null) return;
            Object provider = api.getMethod("getProvider").invoke(null);
            Object baritone = provider.getClass().getMethod("getPrimaryBaritone").invoke(provider);
            Object commandManager = baritone.getClass().getMethod("getCommandManager").invoke(baritone);
            commandManager.getClass().getMethod("execute", String.class).invoke(commandManager, command);
        } catch (Throwable ignored) {
        }
    }

    public static void setSetting(String name, Object value) {
        if (!ThunderHack.baritone) return;
        try {
            Class<?> api = api();
            if (api == null) return;
            Object settings = api.getMethod("getSettings").invoke(null);
            Field settingField = settings.getClass().getField(name);
            Object setting = settingField.get(settings);
            if (setting == null) return;
            Field valueField = setting.getClass().getField("value");
            valueField.set(setting, value);
        } catch (Throwable ignored) {
        }
    }
}
