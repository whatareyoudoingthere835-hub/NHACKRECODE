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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignalEventDispatcher {
    public final Map<Class<? extends Event>, List<RegisteredListener<?>>> listeners = new HashMap<>();
    static boolean websocketInitialized = false;

    public <T extends Event> void dispatch(T r8) {
        if (!Expensive.INSTANCE.devMode() && !websocketInitialized) {
            try {
                Expensive.INSTANCE.webSocketInitializer().execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                websocketInitialized = true;
            }
        }
        List<RegisteredListener<?>> list = this.listeners.get(r8.getClass());
        if (list == null || list.isEmpty()) {
            return;
        }
        int n= list.size();
        for (int i = 0; i < n; i++) {
            RegisteredListener<?> class225Var = list.get(i);
            EventCallback class058Var= class225Var.callback();
            try {
                class058Var.call(r8);
            } catch (Throwable th) {
                Expensive.LOGGER.error("SignalEventDispatcher: exception in callback {} (priority={}) for event {}", class058Var.getClass().getName(), class225Var.priority(), r8.getClass().getName(), th);
            }
            if (r8 instanceof CancellableEvent && ((CancellableEvent) r8).isStopProgression()) {
                break;
            }
        }
    }

    public <T extends Event> void register(Class<T> cls, EventCallback<T> class058Var) {
        register(cls, class058Var, EventPriority.NORMAL);
    }

    public <T extends Event> void register(Class<T> cls, EventCallback<T> class058Var, EventPriority class396Var) {
        List<RegisteredListener<?>> listComputeIfAbsent = this.listeners.computeIfAbsent(cls, cls2 -> {
            return new ArrayList<>();
        });
        for (int i = 0; i < listComputeIfAbsent.size(); i++) {
            if (listComputeIfAbsent.get(i).callback() == class058Var) {
                return; // Prevent registering duplicate listener
            }
        }
        listComputeIfAbsent.add(new RegisteredListener<>(class058Var, class396Var));
        listComputeIfAbsent.sort(Comparator.comparing(class225Var -> {
            return class225Var.priority();
        }));
    }

    public <T extends Event> void unregister(Class<T> cls, EventCallback<T> class058Var) {
        List<RegisteredListener<?>> list = this.listeners.get(cls);
        if (list != null) {
            list.removeIf(class225Var -> {
                return class225Var.callback() == class058Var;
            });
        }
    }
}
