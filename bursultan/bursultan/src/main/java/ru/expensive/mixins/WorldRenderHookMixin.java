package ru.expensive.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.render.WorldRenderEvent;

/**
 * Стреляет в хвосте WorldRenderer.render — мир уже нарисован и глубина
 * ещё цела (ванилла чистит её позже, в GameRenderer.renderWorld перед рукой).
 * Старый хук на выходе renderWorld срабатывал уже после чистки.
 */
@Mixin(WorldRenderer.class)
public class WorldRenderHookMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(CallbackInfo ci) {
        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
        EventManager.callEvent(new WorldRenderEvent(new MatrixStack(), tickDelta));
    }
}
