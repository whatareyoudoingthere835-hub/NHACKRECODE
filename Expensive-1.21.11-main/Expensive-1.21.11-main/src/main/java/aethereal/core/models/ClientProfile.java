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


public final class ClientProfile {
    public final String version;
    public final String branch;
    public final String updated;

    public ClientProfile(String str, String str2, String str3) {
        this.version = str;
        this.branch = str2;
        this.updated = str3;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "version=" + this.version + ", " + "branch=" + this.branch + ", " + "updated=" + this.updated + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.version, this.branch, this.updated);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ClientProfile)) return false;
        ClientProfile o= (ClientProfile) obj;
        return java.util.Objects.equals(this.version, o.version) && java.util.Objects.equals(this.branch, o.branch) && java.util.Objects.equals(this.updated, o.updated);
    }
public String version() {
        return this.version;
    }

    public String branch() {
        return this.branch;
    }

    public String updated() {
        return this.updated;
    }
}
