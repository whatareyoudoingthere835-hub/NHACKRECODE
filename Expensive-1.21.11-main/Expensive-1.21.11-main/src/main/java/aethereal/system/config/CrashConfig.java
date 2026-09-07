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


public final class CrashConfig {
    public final boolean enableCrashes;

    public CrashConfig(boolean z) {
        this.enableCrashes = z;
    }

    public void setup(AuthResponsePacket class718Var) {
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "enableCrashes=" + this.enableCrashes + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.enableCrashes);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CrashConfig)) return false;
        CrashConfig o= (CrashConfig) obj;
        return java.util.Objects.equals(this.enableCrashes, o.enableCrashes);
    }
public boolean enableCrashes() {
        return this.enableCrashes;
    }
}
