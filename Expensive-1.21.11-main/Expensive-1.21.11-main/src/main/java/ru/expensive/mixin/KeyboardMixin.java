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

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class KeyboardMixin {
    @Inject(method = {"onKey"}, at = {@At("HEAD")})
    private void onKey(long j, int action, KeyInput input, CallbackInfo callbackInfo) {
        KeyPressState class050Var;
        int i = input.key();
        int i3 = action;
        Mc class815Var = Mc.INSTANCE;
        if (j == class815Var.getWindow().getHandle() && class815Var.getCurrentScreen() == null) {
            switch (i3) {
                case 0:
                    class050Var = KeyPressState.RELEASE;
                    break;
                case 1:
                    class050Var = KeyPressState.PRESS;
                    break;
                default:
                    class050Var = null;
                    break;
            }
            KeyPressState class050Var2 = class050Var;
            if (class050Var2 == null || i == -1) {
                return;
            }
            Expensive.INSTANCE.eventDispatcher().dispatch(new KeyInputEvent(class050Var2, i));
        }
    }
}
