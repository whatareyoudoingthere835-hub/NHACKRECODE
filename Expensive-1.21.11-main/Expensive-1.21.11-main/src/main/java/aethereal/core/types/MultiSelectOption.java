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

public final class MultiSelectOption<T> {
    public final T value;
    public final Translation translation;
    public final String directLabel;

    public MultiSelectOption(T t, Translation translation) {
        this.value = t;
        this.translation = translation;
        this.directLabel = null;
    }

    public MultiSelectOption(T t, String str) {
        this.value = t;
        this.translation = null;
        this.directLabel = str;
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "value=" + this.value + ", " + "label=" + label() + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.value, label());
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MultiSelectOption)) return false;
        MultiSelectOption o= (MultiSelectOption) obj;
        return java.util.Objects.equals(this.value, o.value) && java.util.Objects.equals(label(), o.label());
    }

    public T value() {
        return this.value;
    }

    public String label() {
        return this.translation != null ? this.translation.effective() : (this.directLabel != null ? this.directLabel : "");
    }
}
