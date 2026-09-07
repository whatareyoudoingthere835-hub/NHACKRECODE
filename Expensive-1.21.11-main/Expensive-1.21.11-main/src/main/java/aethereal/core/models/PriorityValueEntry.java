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

public class PriorityValueEntry<T> {
    public int expiresIn;
    public final int priority;
    public final Object provider;
    public final T value;

    public int getExpiresIn() {
        return this.expiresIn;
    }

    public int getPriority() {
        return this.priority;
    }

    public Object getProvider() {
        return this.provider;
    }

    public T getValue() {
        return this.value;
    }

    public void setExpiresIn(int i) {
        this.expiresIn = i;
    }

    public PriorityValueEntry(int i, int i2, Object obj, T t) {
        this.expiresIn = i;
        this.priority = i2;
        this.provider = obj;
        this.value = t;
    }
}
