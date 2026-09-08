package thunder.hack.injection;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.client.ModuleManager;

import static thunder.hack.features.modules.Module.mc;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    // keep the chunks around the real player loaded while the camera flies away (free cam)
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/Frustum;"), index = 2)
    private Vec3d freeCamFrustumPos(Vec3d pos) {
        if (ModuleManager.freeCam.isEnabled() && mc.player != null) return mc.player.getEyePos();
        return pos;
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void renderWeatherHook(LightmapTextureManager manager, GpuBufferSlice fog, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noWeather.getValue()) {
            ci.cancel();
        }
    }
}
