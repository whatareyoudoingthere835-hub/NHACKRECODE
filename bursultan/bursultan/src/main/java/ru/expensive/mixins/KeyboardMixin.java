package ru.expensive.mixins;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.implement.screens.menu.MenuScreen;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long windowPointer, int action, KeyInput input, CallbackInfo ci) {
        int key = input.key();
        if (key != GLFW.GLFW_KEY_UNKNOWN && MinecraftClient.getInstance().currentScreen == null) {
            if (action == GLFW.GLFW_PRESS) {
                if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                    MinecraftClient.getInstance().setScreen(new MenuScreen());
                    ci.cancel();
                    return;
                }
            }

            EventManager.callEvent(new KeyEvent(key, action, false));
        }
    }
}