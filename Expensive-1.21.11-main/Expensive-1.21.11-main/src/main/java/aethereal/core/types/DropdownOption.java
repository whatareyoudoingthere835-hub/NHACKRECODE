package aethereal.core.types;
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


public final class DropdownOption<T> {
    public final T value;
    public final Translation label;

    public DropdownOption(T t, Translation class254Var) {
        this.value = t;
        this.label = class254Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "value=" + this.value + ", " + "label=" + this.label + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.value, this.label);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DropdownOption)) return false;
        DropdownOption o= (DropdownOption) obj;
        return java.util.Objects.equals(this.value, o.value) && java.util.Objects.equals(this.label, o.label);
    }
public T value() {
        return this.value;
    }

    public Translation label() {
        return this.label;
    }
}
