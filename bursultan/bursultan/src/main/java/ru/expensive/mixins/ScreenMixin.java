package ru.expensive.mixins;

import ru.expensive.core.Extra;
import ru.expensive.implement.features.modules.render.ClearRenderModule;
import ru.expensive.implement.screens.menu.MenuScreen;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.InfoWindow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.chat.ChatEvent;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "handleClickEvent", at = @At("HEAD"), cancellable = true)
    private static void handleCustomClickEvent(ClickEvent clickEvent, MinecraftClient client, Screen screen, CallbackInfo ci) {
        String value = null;
        if (clickEvent instanceof ClickEvent.RunCommand runCommand) {
            value = runCommand.command();
        } else if (clickEvent instanceof ClickEvent.SuggestCommand suggestCommand) {
            value = suggestCommand.command();
        } else {
            return;
        }
        EventManager.callEvent(new ChatEvent(value));
        ci.cancel();
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void disableBackgroundBlurAndDimmingForMenu(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen currentScreen = (Screen) (Object) this;
        ClearRenderModule clearRenderModule = (ClearRenderModule) Extra.getInstance().getModuleProvider().module("ClearRender");
        if (currentScreen instanceof MenuScreen || 
            (clearRenderModule != null && clearRenderModule.isState() && 
             clearRenderModule.getClearRenderSettings().isSelected("Container"))) {
            ci.cancel();
        }
    }
}