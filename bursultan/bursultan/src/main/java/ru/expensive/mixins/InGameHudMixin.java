package ru.expensive.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.core.Extra;
import ru.expensive.implement.events.render.Render2DEvent;
import ru.expensive.implement.features.modules.render.InterfaceModule;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        InterfaceModule interfaceModule = (InterfaceModule) Extra.getInstance().getModuleProvider().module("Interface");
        if (interfaceModule.isState() && interfaceModule.getInterfaceSettings().isSelected("HotBar")) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusBars(DrawContext context, CallbackInfo ci) {
        InterfaceModule interfaceModule = (InterfaceModule) Extra.getInstance().getModuleProvider().module("Interface");
        if (interfaceModule.isState() && interfaceModule.getInterfaceSettings().isSelected("HotBar")) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffects(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client.currentScreen != null) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client != null && client.getWindow() != null) {
            ru.expensive.common.util.render.ExpensiveRenderLayers.beginGuiFrame();
            try {
                EventManager.callEvent(new Render2DEvent(
                        new net.minecraft.client.util.math.MatrixStack(),
                        tickCounter.getTickProgress(false),
                        client.getWindow().getScaledWidth(),
                        client.getWindow().getScaledHeight()
                ));
            } finally {
                ru.expensive.common.util.render.ExpensiveRenderLayers.endGuiFrame();
            }
        }
    }
}
