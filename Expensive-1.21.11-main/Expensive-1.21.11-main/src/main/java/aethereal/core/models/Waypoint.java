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

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3i;

public final class Waypoint {

    @SerializedName("name")
    public final String name;

    @SerializedName("vector")
    public final Vector3i vector;

    public Waypoint(String str, Vector3i vector3i) {
        this.name = str;
        this.vector = vector3i;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "name=" + this.name + ", " + "vector=" + this.vector + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.name, this.vector);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Waypoint)) return false;
        Waypoint o= (Waypoint) obj;
        return java.util.Objects.equals(this.name, o.name) && java.util.Objects.equals(this.vector, o.vector);
    }
@SerializedName("name")
    public String name() {
        return this.name;
    }

    @SerializedName("vector")
    public Vector3i vector() {
        return this.vector;
    }
}
