package ru.expensive.mixins;

import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.core.Extra;
import ru.expensive.api.feature.module.Module;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.api.event.EventManager;

@Mixin(Mouse.class)
public class MouseInputMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, MouseInput buttonInfo, int action, CallbackInfo ci) {
        int button = buttonInfo.button();
        EventManager.callEvent(new KeyEvent(button, action, true));
    }
}