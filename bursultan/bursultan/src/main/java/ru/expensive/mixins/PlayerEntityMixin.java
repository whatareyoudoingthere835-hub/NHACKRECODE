package ru.expensive.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.core.Extra;
import ru.expensive.implement.events.block.PushWaterEvent;
import ru.expensive.implement.events.player.KeepSprintEvent;

@SuppressWarnings("all")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true)
    public void isPushedByFluids(CallbackInfoReturnable<Boolean> infoReturnable) {
        PushWaterEvent pushWaterEvent = new PushWaterEvent();
        EventManager.callEvent(pushWaterEvent);

        if ((Object) this instanceof ClientPlayerEntity && pushWaterEvent.isCancelled()) {
            infoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    public void attackHook(CallbackInfo callbackInfo) {
        EventManager.callEvent(new KeepSprintEvent());
    }
}
