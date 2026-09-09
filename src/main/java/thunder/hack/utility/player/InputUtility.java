package thunder.hack.utility.player;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import thunder.hack.features.modules.Module;

/**
 * 1.21.9+ replacement for the removed Input.movementForward/Sideways/jumping/sneaking fields.
 * Reads/writes go through the PlayerInput record on ClientPlayerEntity#input.
 */
public final class InputUtility {
    private InputUtility() {
    }

    private static ClientPlayerEntity player() {
        return Module.mc.player;
    }

    private static PlayerInput input() {
        return player().input.playerInput;
    }

    public static float forward() {
        PlayerInput i = input();
        return (i.forward() ? 1f : 0f) - (i.back() ? 1f : 0f);
    }

    public static float strafe() {
        PlayerInput i = input();
        return (i.right() ? 1f : 0f) - (i.left() ? 1f : 0f);
    }

    public static boolean jump() {
        return input().jump();
    }

    public static boolean sneak() {
        return input().sneak();
    }

    public static boolean sprint() {
        return input().sprint();
    }

    public static void setForward(boolean value) {
        PlayerInput i = input();
        player().input.playerInput = new PlayerInput(value, i.back(), i.left(), i.right(), i.jump(), i.sneak(), i.sprint());
    }

    public static void setBack(boolean value) {
        PlayerInput i = input();
        player().input.playerInput = new PlayerInput(i.forward(), value, i.left(), i.right(), i.jump(), i.sneak(), i.sprint());
    }

    public static void setStrafe(boolean value) {
        // value > 0 style calls handled by callers; boolean form: true = right
        PlayerInput i = input();
        player().input.playerInput = new PlayerInput(i.forward(), i.back(), !value, value, i.jump(), i.sneak(), i.sprint());
    }

    public static void setJump(boolean value) {
        PlayerInput i = input();
        player().input.playerInput = new PlayerInput(i.forward(), i.back(), i.left(), i.right(), value, i.sneak(), i.sprint());
    }

    public static void setSneak(boolean value) {
        PlayerInput i = input();
        player().input.playerInput = new PlayerInput(i.forward(), i.back(), i.left(), i.right(), i.jump(), value, i.sprint());
    }

    public static void setSprint(boolean value) {
        PlayerInput i = input();
        player().input.playerInput = new PlayerInput(i.forward(), i.back(), i.left(), i.right(), i.jump(), i.sneak(), value);
    }

    /** scale form used by old {@code movementForward *= k} call sites (k==0 clears input) */
    public static void scaleForward(float k) {
        setForward(k != 0f && forward() > 0f);
    }

    public static void scaleStrafe(float k) {
        if (k == 0f) {
            PlayerInput i = input();
            player().input.playerInput = new PlayerInput(i.forward(), i.back(), false, false, i.jump(), i.sneak(), i.sprint());
        }
    }
}
