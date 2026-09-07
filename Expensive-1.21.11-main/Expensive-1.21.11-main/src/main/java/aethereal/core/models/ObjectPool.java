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

import java.util.ArrayDeque;
import java.util.Queue;

public class ObjectPool<T> {
    public final Queue<T> pool = new ArrayDeque();

    public final ObjectFactory<T> factory;

    public synchronized T get() {
        return !this.pool.isEmpty() ? this.pool.poll() : (T) this.factory.create();
    }

    public synchronized void free(T t) {
        this.pool.offer(t);
    }

    public ObjectPool(ObjectFactory<T> class379Var) {
        this.factory = class379Var;
    }
}
