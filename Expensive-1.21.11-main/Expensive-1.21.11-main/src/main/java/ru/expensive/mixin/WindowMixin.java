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

import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Window.class})
public class WindowMixin {
    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    public void init(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, @Nullable String str, String str2, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new WindowResizeEvent(windowSettings.width(), windowSettings.height()));
        Expensive.INSTANCE.windowControllerAdapter().handleResize(windowSettings.width(), windowSettings.height());
    }

    @Inject(method = {"onWindowSizeChanged"}, at = {@At("HEAD")})
    public void onResize(long j, int i, int i2, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.windowControllerAdapter().handleResize(i, i2);
        Expensive.INSTANCE.eventDispatcher().dispatch(new WindowResizeEvent(i, i2));
    }
}
