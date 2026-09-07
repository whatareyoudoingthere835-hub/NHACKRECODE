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

import net.minecraft.entity.ItemEntity;
import org.joml.Vector4f;

public final class EspItemProjection {
    public final ItemEntity item;

    public final Vector4f proj;

    public EspItemProjection(ItemEntity itemEntity, Vector4f vector4f) {
        this.item = itemEntity;
        this.proj = vector4f;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "item=" + this.item + ", " + "proj=" + this.proj + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.item, this.proj);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EspItemProjection)) return false;
        EspItemProjection o= (EspItemProjection) obj;
        return java.util.Objects.equals(this.item, o.item) && java.util.Objects.equals(this.proj, o.proj);
    }
public ItemEntity item() {
        return this.item;
    }

    public Vector4f proj() {
        return this.proj;
    }
}
