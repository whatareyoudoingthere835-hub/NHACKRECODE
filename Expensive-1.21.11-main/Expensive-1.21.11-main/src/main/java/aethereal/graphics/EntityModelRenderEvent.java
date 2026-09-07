package aethereal.graphics;
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

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public final class EntityModelRenderEvent implements Event {
    public final LivingEntity livingEntity;
    public final MatrixStack matrixStack;

    public final EntityModel<?> model;

    public EntityModelRenderEvent(LivingEntity livingEntity, MatrixStack matrixStack, EntityModel<?> entityModel) {
        this.livingEntity = livingEntity;
        this.matrixStack = matrixStack;
        this.model = entityModel;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "livingEntity=" + this.livingEntity + ", " + "matrixStack=" + this.matrixStack + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.livingEntity, this.matrixStack);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityModelRenderEvent)) return false;
        EntityModelRenderEvent o= (EntityModelRenderEvent) obj;
        return java.util.Objects.equals(this.livingEntity, o.livingEntity) && java.util.Objects.equals(this.matrixStack, o.matrixStack);
    }
public LivingEntity livingEntity() {
        return this.livingEntity;
    }

    public MatrixStack matrixStack() {
        return this.matrixStack;
    }

    public EntityModel<?> model() {
        return this.model;
    }
}
