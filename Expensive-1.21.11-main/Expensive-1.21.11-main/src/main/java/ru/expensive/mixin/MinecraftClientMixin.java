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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    private Overlay overlay;

    @Shadow
    @Final
    private Window window;

    @Unique
    private static boolean expensive$replacingScreen = false;

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void onTick(CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new ClientTickEvent());
    }

    @WrapOperation(method = {"doItemUse"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")})
    public ActionResult interactBlockHook(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity clientPlayerEntity, Hand hand, BlockHitResult blockHitResult, Operation<ActionResult> operation) {
        BlockInteractEvent class175Var = new BlockInteractEvent(blockHitResult, hand);
        Expensive.INSTANCE.eventDispatcher().dispatch(class175Var);
        return class175Var.isCancelled() ? ActionResult.FAIL : (ActionResult) operation.call(new Object[]{clientPlayerInteractionManager, clientPlayerEntity, hand, blockHitResult});
    }

    @ModifyExpressionValue(method = {"doItemUse"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")})
    public boolean doItemUseHook(boolean z) {
        UseItemEvent class059Var = new UseItemEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class059Var);
        if (class059Var.isCancelled()) {
            return true;
        }
        return z;
    }

    @Inject(method = {"setWorld"}, at = {@At("HEAD")})
    private void setWorld(ClientWorld clientWorld, boolean loading, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new WorldLoadEvent(clientWorld));
    }

    @Redirect(method = {"handleBlockBreaking"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean redirectHandleBlockBreaking(ClientPlayerEntity clientPlayerEntity) {
        StopUsingItemEvent class076Var = new StopUsingItemEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class076Var);
        if (class076Var.isCancelled()) {
            return false;
        }
        return clientPlayerEntity.isUsingItem();
    }

    @Inject(method = {"onResolutionChanged"}, at = {@At("RETURN")})
    public void onResolutionChanged(CallbackInfo callbackInfo) {
        StencilBufferUtil.initFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), FrameBufferUtils.getColorAttachmentId(Mc.INSTANCE.getFramebuffer()));
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;blitToScreen()V")})
    private void runTick(boolean z, CallbackInfo callbackInfo) throws MatchException {
        Expensive.INSTANCE.eventDispatcher().dispatch(new FrameRenderEvent());
        if (Mc.INSTANCE.getWindow() != null && !Mc.INSTANCE.getWindow().isMinimized()) {
            Expensive.INSTANCE.windowControllerAdapter().preBlitFramebufferToBackbuffer(Mc.INSTANCE.getWindow().getHandle());
        }
    }

    @ModifyExpressionValue(method = {"doItemUse"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z")})
    private boolean modifyDoItemUse(boolean z) {
        StopUsingItemEvent class076Var = new StopUsingItemEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class076Var);
        if (class076Var.isCancelled()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"setScreen"}, at = {@At("HEAD")}, cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo callbackInfo) {
        if (expensive$replacingScreen) {
            return;
        }
        expensive$replacingScreen = true;
        try {
            ScreenOpenEvent class135Var = new ScreenOpenEvent(screen);
            Expensive.INSTANCE.eventDispatcher().dispatch(class135Var);
            if (class135Var.isCancelled()) {
                callbackInfo.cancel();
            }
        } finally {
            expensive$replacingScreen = false;
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"stop"})
    private void stop(CallbackInfo callbackInfo) {
        Expensive.INSTANCE.discordManager().stopRPC();
        Expensive.INSTANCE.configManager().saveSessionNickname();
    }

    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    public void init(RunArgs runArgs, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new ClientInitEvent());
    }
}
