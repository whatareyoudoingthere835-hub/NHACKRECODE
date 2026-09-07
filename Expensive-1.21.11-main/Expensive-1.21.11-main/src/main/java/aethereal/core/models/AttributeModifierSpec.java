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


public final class AttributeModifierSpec {
    public final CharSequence id;
    public final double amount;
    public final CharSequence slot;

    public AttributeModifierSpec(CharSequence charSequence, double d, CharSequence charSequence2) {
        this.id = charSequence;
        this.amount = d;
        this.slot = charSequence2;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "id=" + this.id + ", " + "amount=" + this.amount + ", " + "slot=" + this.slot + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.id, this.amount, this.slot);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AttributeModifierSpec)) return false;
        AttributeModifierSpec o= (AttributeModifierSpec) obj;
        return java.util.Objects.equals(this.id, o.id) && java.util.Objects.equals(this.amount, o.amount) && java.util.Objects.equals(this.slot, o.slot);
    }
public CharSequence id() {
        return this.id;
    }

    public double amount() {
        return this.amount;
    }

    public CharSequence slot() {
        return this.slot;
    }
}
