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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemRenderer.class})
public class HeldItemRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private void applySwingOffset(MatrixStack matrixStack, Arm arm, float f) {
    }

    @ModifyConstant(method = {"applyEquipOffset"}, constant = {@Constant(floatValue = -0.6f)})
    private float modifyEquipOffsetConstant(float f) {
        EquipAnimationEvent class176Var = new EquipAnimationEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class176Var);
        if (class176Var.isCancelled()) {
            return 0.0f;
        }
        return f;
    }

    @Redirect(method = {"swingArm"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0))
    private void cancelSpecificTranslate(MatrixStack matrixStack, float f, float f2, float f3) {
        EquipAnimationEvent2 class287Var = new EquipAnimationEvent2();
        Expensive.INSTANCE.eventDispatcher().dispatch(class287Var);
        if (class287Var.isCancelled()) {
            return;
        }
        matrixStack.translate(f, f2, f3);
    }

    @Inject(method = {"renderFirstPersonItem"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER)})
    private void renderFirstPersonItemHook(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float f2, Hand hand, float f3, ItemStack itemStack, float f4, MatrixStack matrixStack, OrderedRenderCommandQueue commandQueue, int i, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new HeldItemRenderEvent(hand, matrixStack, itemStack));
    }

    @WrapOperation(method = {"renderFirstPersonItem"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V")})
    private void handAnimationHook(HeldItemRenderer heldItemRenderer, float f, MatrixStack matrixStack, int i, Arm arm, Operation<Void> operation, @Local(ordinal = 0, argsOnly = true) AbstractClientPlayerEntity abstractClientPlayerEntity, @Local(ordinal = 0, argsOnly = true) Hand hand) {
        ArmRenderEvent class360Var = new ArmRenderEvent(arm, matrixStack, f);
        Expensive.INSTANCE.eventDispatcher().dispatch(class360Var);
        if (!class360Var.isCancelled() || arm == Arm.LEFT) {
            operation.call(new Object[]{heldItemRenderer, Float.valueOf(f), matrixStack, Integer.valueOf(i), arm});
        }
    }

    @ModifyExpressionValue(method = {"updateHeldItems"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;")})
    private ItemStack injectSilentHotbar(ItemStack itemStack) {
        return this.client.player != null ? (ItemStack) (Object) this.client.player.getInventory().getMainStacks().get(Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().getClientsideSlot()) : itemStack;
    }
}
