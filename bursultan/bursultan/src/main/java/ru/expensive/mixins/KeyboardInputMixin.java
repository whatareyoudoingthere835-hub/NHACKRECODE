package ru.expensive.mixins;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.common.util.player.MovingUtil;
import ru.expensive.implement.events.player.MovementInputEvent;
import ru.expensive.mixins.accessors.InputAccessor;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        InputAccessor accessor = (InputAccessor) this;
        PlayerInput current = accessor.getPlayerInput();

        MovingUtil.DirectionalInput directional = new MovingUtil.DirectionalInput(
                current.forward(),
                current.backward(),
                current.left(),
                current.right());
        MovementInputEvent event = new MovementInputEvent(directional, current.jump(), current.sneak());
        EventManager.callEvent(event);

        MovingUtil.DirectionalInput dir = event.getDirectionalInput();
        accessor.setPlayerInput(new PlayerInput(
                dir.isForwards(),
                dir.isBackwards(),
                dir.isLeft(),
                dir.isRight(),
                event.isJumping(),
                event.isSneaking(),
                current.sprint()));
        float forward = MovingUtil.DirectionalInput.getMovementMultiplier(dir.isForwards(), dir.isBackwards());
        float sideways = MovingUtil.DirectionalInput.getMovementMultiplier(dir.isLeft(), dir.isRight());
        accessor.setMovementVector(new Vec2f(sideways, forward).normalize());
    }
}
