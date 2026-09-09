package thunder.hack.features.modules.render;

import thunder.hack.utility.render.PoseStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class NoBob extends Module {
    public NoBob() {
        super("NoBob", Category.RENDER);
    }

    public static Setting<Mode> mode = new Setting<>("Mode", Mode.Sexy);

    public void bobView(PoseStack matrices, float tickDelta) {
        if (!(mc.getCameraEntity() instanceof PlayerEntity))
            return;

        float g = -((Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z)) + ((Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z)) - mc.Math.hypot(player.getVelocity().x, player.getVelocity().z)) * tickDelta);
        float h = MathHelper.lerp(tickDelta, 0.0F, 0.0F);
        matrices.translate(0, -Math.abs(g * h * (mode.is(Mode.Sexy) ? 0.00035 : 0.)), 0);
    }

    public enum Mode {
        Sexy,
        Off
    }
}