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


public final class ConfigReference {
    public final String id;
    public final String name;

    public ConfigReference(String str, String str2) {
        this.id = str;
        this.name = str2;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "id=" + this.id + ", " + "name=" + this.name + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.id, this.name);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConfigReference)) return false;
        ConfigReference o= (ConfigReference) obj;
        return java.util.Objects.equals(this.id, o.id) && java.util.Objects.equals(this.name, o.name);
    }
public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }
}
