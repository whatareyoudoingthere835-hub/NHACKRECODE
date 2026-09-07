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


public final class LoadedConfig {
    public final CloudConfigDto details;
    public final ConfigFile bundle;

    public LoadedConfig(CloudConfigDto class376Var, ConfigFile class145Var) {
        this.details = class376Var;
        this.bundle = class145Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "details=" + this.details + ", " + "bundle=" + this.bundle + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.details, this.bundle);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LoadedConfig)) return false;
        LoadedConfig o= (LoadedConfig) obj;
        return java.util.Objects.equals(this.details, o.details) && java.util.Objects.equals(this.bundle, o.bundle);
    }
public CloudConfigDto details() {
        return this.details;
    }

    public ConfigFile bundle() {
        return this.bundle;
    }
}
