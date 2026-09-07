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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyExpressionValue(method = {"tick()V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSelectedStack()Lnet/minecraft/item/ItemStack;")})
    private ItemStack injectSilent(ItemStack itemStack) {
        return this.client.player != null ? (ItemStack) (Object) this.client.player.getInventory().getMainStacks().get(Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().getClientsideSlot()) : itemStack;
    }

    @Shadow
    protected abstract void renderScoreboardSidebar(DrawContext drawContext, ScoreboardObjective scoreboardObjective);

    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void preRender(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new Render2DEvent(new MatrixStack(), Render2DStage.PRE, renderTickCounter, drawContext));
    }

    @Inject(method = {"renderCrosshair"}, at = {@At("HEAD")}, cancellable = true)
    private void crosshair(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        CrosshairRenderEvent class247Var = new CrosshairRenderEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class247Var);
        if (class247Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }



    @ModifyExpressionValue(method = {"renderCrosshair"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;")})
    private Perspective hookPerspectiveEventOnCrosshair(Perspective perspective) {
        PerspectiveEvent class160Var = new PerspectiveEvent(perspective);
        Expensive.INSTANCE.eventDispatcher().dispatch(class160Var);
        return class160Var.perspective();
    }

    @ModifyExpressionValue(method = {"renderMiscOverlays"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;")})
    private Perspective hookPerspectiveEventOnMiscOverlays(Perspective perspective) {
        PerspectiveEvent class160Var = new PerspectiveEvent(perspective);
        Expensive.INSTANCE.eventDispatcher().dispatch(class160Var);
        return class160Var.perspective();
    }

    @Inject(method = {"renderStatusEffectOverlay"}, at = {@At("HEAD")}, cancellable = true)
    private void renderStatusEffectOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        StatusEffectOverlayEvent class315Var = new StatusEffectOverlayEvent();
        Expensive.INSTANCE.eventDispatcher().dispatch(class315Var);
        if (class315Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"))
    private void renderScoreboard(InGameHud inGameHud, DrawContext drawContext, ScoreboardObjective scoreboardObjective) {
        RenderOverlayEvent class252Var = new RenderOverlayEvent(RenderOverlayType.SCOREBOARD);
        Expensive.INSTANCE.eventDispatcher().dispatch(class252Var);
        if (class252Var.isCancelled()) {
            return;
        }
        renderScoreboardSidebar(drawContext, scoreboardObjective);
    }
}
