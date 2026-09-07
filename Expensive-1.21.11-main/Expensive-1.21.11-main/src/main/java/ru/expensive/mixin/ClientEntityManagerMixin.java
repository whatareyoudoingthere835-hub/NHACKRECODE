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

import net.minecraft.entity.Entity;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientEntityManager.Listener.class})
public class ClientEntityManagerMixin<T extends EntityLike> {

    @Shadow
    @Final
    private T entity;

    @Inject(method = {"<init>"}, at = {@At("TAIL")})
    protected void init(CallbackInfo callbackInfo) {
        Entity entity = (Entity) (this.entity);
        if (entity instanceof Entity) {
            Expensive.INSTANCE.eventDispatcher().dispatch(new EntityLifecycleEvent(entity, EntityLifecycleAction.ADD));
        }
    }

    @Inject(method = {"remove"}, at = {@At("HEAD")})
    public void removeEntityHook(Entity.RemovalReason removalReason, CallbackInfo callbackInfo) {
        Entity entity = (Entity) (this.entity);
        if (entity instanceof Entity) {
            Expensive.INSTANCE.eventDispatcher().dispatch(new EntityLifecycleEvent(entity, EntityLifecycleAction.REMOVE));
        }
    }
}
