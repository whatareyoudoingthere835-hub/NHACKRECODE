package aethereal.utils;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public final class OptionalModBridge {
    private OptionalModBridge() {
    }

    public static boolean isBaritoneAvailable() {
        try {
            Class.forName("baritone.api.BaritoneAPI", false, OptionalModBridge.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isBaritonePathing() {
        try {
            Object behavior= baritoneBehavior();
            if (behavior == null) {
                return false;
            }
            Object pathing= invoke(behavior, "isPathing");
            if (Boolean.TRUE.equals(pathing)) {
                return true;
            }
            Object progress= invoke(behavior, "getInProgress");
            return progress instanceof Optional && ((Optional<?>) progress).isPresent();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void cancelBaritone() {
        try {
            Object behavior= baritoneBehavior();
            if (behavior != null) {
                invoke(behavior, "cancelEverything");
            }
        } catch (Throwable ignored) {
        }
    }

    public static void pathToBlock(net.minecraft.util.math.BlockPos pos) {
        try {
            Object behavior= baritoneBehavior();
            if (behavior != null) {
                Class<?> goalClass = Class.forName("baritone.api.pathing.goals.GoalBlock", false, OptionalModBridge.class.getClassLoader());
                Object goal= goalClass.getConstructor(net.minecraft.util.math.BlockPos.class).newInstance(pos);
                Method pathToMethod= behavior.getClass().getMethod("pathTo", Class.forName("baritone.api.pathing.goals.Goal", false, OptionalModBridge.class.getClassLoader()));
                pathToMethod.invoke(behavior, goal);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void applyBaritoneSafeDefaults() {
        try {
            Object settings= invokeStatic("baritone.api.BaritoneAPI", "getSettings");
            if (settings == null) return;
            setSetting(settings, "allowBreak", Boolean.FALSE);
            setSetting(settings, "allowPlace", Boolean.FALSE);
            setSetting(settings, "allowParkour", Boolean.FALSE);
            setSetting(settings, "allowParkourAscend", Boolean.FALSE);
            setSetting(settings, "allowVines", Boolean.FALSE);
            setSetting(settings, "assumeStep", Boolean.FALSE);
            setSetting(settings, "jumpPenalty", Double.valueOf(12.0d));
            setSetting(settings, "freeLook", Boolean.TRUE);
        } catch (Throwable ignored) {
        }
    }

    private static Object baritoneBehavior() throws Exception {
        Object provider= invokeStatic("baritone.api.BaritoneAPI", "getProvider");
        if (provider == null) {
            return null;
        }
        Object baritone= invoke(provider, "getPrimaryBaritone");
        return baritone == null ? null : invoke(baritone, "getPathingBehavior");
    }

    private static Object invokeStatic(String className, String method) throws Exception {
        return invoke(Class.forName(className, false, OptionalModBridge.class.getClassLoader()), null, method);
    }

    private static Object invoke(Object target, String method) throws Exception {
        return invoke(target, target == null ? null : target.getClass(), method);
    }

    private static Object invoke(Object target, Class<?> type, String method, Object... args) throws Exception {
        Class<?> owner = target instanceof Class<?> ? (Class<?>) target : type;
        if (owner == null) {
            throw new NullPointerException("optional integration target");
        }
        Method selected= null;
        for (Method candidate : owner.getMethods()) {
            if (candidate.getName().equals(method) && candidate.getParameterCount() == args.length) {
                selected = candidate;
                break;
            }
        }
        if (selected == null) {
            throw new NoSuchMethodException(method);
        }
        return selected.invoke(target instanceof Class<?> ? null : target, args);
    }

    private static void setSetting(Object settings, String name, Object value) throws Exception {
        Field option= settings.getClass().getField(name);
        Object wrapper= option.get(settings);
        if (wrapper != null) {
            wrapper.getClass().getField("value").set(wrapper, value);
        }
    }
}
