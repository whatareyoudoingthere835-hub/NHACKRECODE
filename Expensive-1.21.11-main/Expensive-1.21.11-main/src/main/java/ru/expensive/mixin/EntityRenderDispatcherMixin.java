package ru.expensive.mixin;
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

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin({EntityRenderManager.class})
public class EntityRenderDispatcherMixin {
    @Unique
    private final Map<EntityRenderState, Entity> expensive$entities = new IdentityHashMap<>();

    @Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
    private void expensive$hidePlayerLabel(Entity entity, float tickProgress, CallbackInfoReturnable<EntityRenderState> callbackInfo) {
        EntityRenderState entityRenderState = callbackInfo.getReturnValue();
        this.expensive$entities.put(entityRenderState, entity);
        EntityRenderEvent class147Var = new EntityRenderEvent(entity);
        Expensive.INSTANCE.eventDispatcher().dispatch(class147Var);
        if (class147Var.isCancelled()) {
            entityRenderState.displayName = null;
            entityRenderState.nameLabelPos = null;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void expensive$setCurrentEntity(EntityRenderState state, CameraRenderState cameraState, double x, double y, double z, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, CallbackInfo callbackInfo) {
        Entity entity = this.expensive$entities.get(state);
        if (entity != null) {
            Expensive.INSTANCE.moduleRepository().get(ChamsModule.class).currentEntity(entity);
        }
    }
}
