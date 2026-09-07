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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntity.class})
public class PlayerEntityMixin {
    @Unique
    private Vec3d expensive$velocityBeforeAttack;

    @Unique
    private boolean expensive$sprintingBeforeAttack;
    @Inject(method = {"isPushedByFluids"}, at = {@At("HEAD")}, cancellable = true)
    public void isPushedByFluids(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        PushEvent class231Var = new PushEvent(PushType.WATER);
        Expensive.INSTANCE.eventDispatcher().dispatch(class231Var);
        if ((((PlayerEntity) (Object) this) instanceof ClientPlayerEntity) && class231Var.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;")})
    private ItemStack injectSilentHotbar(ItemStack itemStack) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        return playerEntity instanceof ClientPlayerEntity ? (ItemStack) playerEntity.getInventory().getMainStacks().get(Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().getClientsideSlot()) : itemStack;
    }

    @Inject(method = {"attack"}, at = @At("HEAD"))
    private void captureVelocity(Entity target, CallbackInfo callbackInfo) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        this.expensive$velocityBeforeAttack = player.getVelocity();
        this.expensive$sprintingBeforeAttack = player.isSprinting();
    }

    @Inject(method = {"attack"}, at = @At("TAIL"))
    private void hookSlowVelocity(Entity target, CallbackInfo callbackInfo) {
        if ((Object) this == MinecraftClient.getInstance().player && this.expensive$sprintingBeforeAttack) {
            SprintStopEvent class337Var = new SprintStopEvent();
            Expensive.INSTANCE.eventDispatcher().dispatch(class337Var);
            if (class337Var.isCancelled() && this.expensive$velocityBeforeAttack != null) {
                PlayerEntity player = (PlayerEntity) (Object) this;
                Vec3d velocity = player.getVelocity();
                player.setVelocity(this.expensive$velocityBeforeAttack.x, velocity.y, this.expensive$velocityBeforeAttack.z);
            }
        }
    }
}
