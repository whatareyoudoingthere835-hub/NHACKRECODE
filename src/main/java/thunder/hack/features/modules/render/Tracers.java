package thunder.hack.features.modules.render;

import thunder.hack.utility.render.PoseStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render3DEngine;

import java.awt.*;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", Category.RENDER);
    }

    private final Setting<Float> height = new Setting<>("Height", 0f, 0f, 2f);

    private final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(new Color(0x93FF0000, true)));
    private final Setting<ColorSetting> friendColor = new Setting<>("Friends", new ColorSetting(new Color(0x9317DE5D, true)));

    public void onRender3D(PoseStack stack) {
        for (PlayerEntity player : Managers.ASYNC.getAsyncPlayers()) {
            if (player == mc.player)
                continue;

            Color color1 = color.getValue().getColorObject();

            if (Managers.FRIEND.isFriend(player))
                color1 = friendColor.getValue().getColorObject();

            double x1 = (mc.player.getX() - mc.player.getVelocity().x) + (mc.player.getX() - (mc.player.getX() - mc.player.getVelocity().x)) * Render3DEngine.getTickDelta(false);
            double y1 = mc.player.getEyeHeight(mc.player.getPose()) + (mc.player.getY() - mc.player.getVelocity().y) + (mc.player.getY() - (mc.player.getY() - mc.player.getVelocity().y)) * Render3DEngine.getTickDelta(false);
            double z1 = (mc.player.getZ() - mc.player.getVelocity().z) + (mc.player.getZ() - (mc.player.getZ() - mc.player.getVelocity().z)) * Render3DEngine.getTickDelta(false);

            Vec3d vec2 = new Vec3d(0, 0, 75)
                    .rotateX(-(float) Math.toRadians(mc.net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getPitch()))
                    .rotateY(-(float) Math.toRadians(mc.net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getYaw()))
                    .add(x1, y1, z1);

            double x = (player.getX() - player.getVelocity().x) + (player.getX() - (player.getX() - player.getVelocity().x)) * Render3DEngine.getTickDelta(false);
            double y = (player.getY() - player.getVelocity().y) + (player.getY() - (player.getY() - player.getVelocity().y)) * Render3DEngine.getTickDelta(false);
            double z = (player.getZ() - player.getVelocity().z) + (player.getZ() - (player.getZ() - player.getVelocity().z)) * Render3DEngine.getTickDelta(false);

            Render3DEngine.drawLineDebug(vec2, new Vec3d(x, y + height.getValue(), z), color1);
        }
    }
}
