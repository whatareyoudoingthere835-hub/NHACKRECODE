package thunder.hack.injection;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.CommandManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.core.manager.client.ProxyManager;
import thunder.hack.events.impl.ClientClickEvent;
import thunder.hack.gui.misc.DialogScreen;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static thunder.hack.features.modules.Module.mc;
import static thunder.hack.features.modules.client.ClientSettings.isRu;

@Mixin(Screen.class)
public abstract class MixinScreen {
    @Inject(method = "handleClickEvent(Lnet/minecraft/text/ClickEvent;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void onRunCommand(ClickEvent clickEvent, net.minecraft.client.MinecraftClient client, Screen screenAfterRun, CallbackInfo ci) {
        if (clickEvent instanceof ClickEvent.RunCommand run && run.command().startsWith(Managers.COMMAND.getPrefix())) {
            try {
                CommandManager manager = Managers.COMMAND;
                manager.getDispatcher().execute(run.command().substring(Managers.COMMAND.getPrefix().length()), manager.getSource());
                ci.cancel();
            } catch (CommandSyntaxException ignored) {
            }
        }
    }

    @Inject(method = "renderPanoramaBackground", at = @At("HEAD"), cancellable = true)
    public void renderPanoramaBackgroundHook(DrawContext context, float delta, CallbackInfo ci) {
        if (ClientSettings.customPanorama.getValue() && mc.world == null) {
            ci.cancel();
            Render2DEngine.drawMainMenuShader(context.getMatrices(), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        }
    }

    @Inject(method = "renderInGameBackground", at = @At("HEAD"), cancellable = true)
    private void renderInGameBackground(CallbackInfo info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.disableGuiBackGround.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"), cancellable = true)
    public void onRenderBackground(DrawContext context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.disableGuiBackGround.getValue() && mc.world != null) {
            ci.cancel();
        }
    }
}
