package thunder.hack.features.modules.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class TotemAnimation extends Module {
    public TotemAnimation() {
        super("TotemAnimation", Category.RENDER);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.FadeOut);
    private final Setting<Integer> speed = new Setting<>("Speed", 40, 1, 100);

    private ItemStack floatingItem = null;
    private int floatingItemTimeLeft;

    public void showFloatingItem(ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        floatingItemTimeLeft = getTime();
    }

    @Override
    public void onUpdate() {
        if (floatingItemTimeLeft > 0) {
            --floatingItemTimeLeft;
            if (floatingItemTimeLeft == 0) {
                floatingItem = null;
            }
        }
    }

    public void renderFloatingItem(net.minecraft.client.gui.DrawContext context, float tickDelta) {
        if (floatingItem == null || floatingItemTimeLeft <= 0 || mode.is(Mode.Off)) return;

        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();

        int elapsedTime = getTime() - floatingItemTimeLeft;
        float animationProgress = ((float) elapsedTime + tickDelta) / (float) getTime();
        float progressSquared = animationProgress * animationProgress;
        float progressCubed = animationProgress * progressSquared;
        float oscillationFactor = 10.25F * progressCubed * progressSquared - 24.95F * progressSquared * progressSquared + 25.5F * progressCubed - 13.8F * progressSquared + 4.0F * animationProgress;
        float oscillationRadians = oscillationFactor * 3.1415927F;

        org.joml.Matrix3x2fStack m = context.getMatrices();
        m.pushMatrix();
        float adjustedProgress = ((float) elapsedTime + tickDelta);
        float scale = 50.0F + 175.0F * MathHelper.sin(oscillationRadians);
        float cx = (float) (scaledWidth / 2);
        float cy = (float) (scaledHeight / 2);

        switch (mode.getValue()) {
            case FadeOut -> {
                final float x2 = (float) (Math.sin(((adjustedProgress * 112) / 180f)) * 100);
                final float y2 = (float) (Math.cos(((adjustedProgress * 112) / 180f)) * 50);
                m.translate((float) (cx + x2), (float) (cy + y2));
                m.scale(scale / 50.0F);
            }

            case Size -> {
                m.translate((float) (cx), (float) (cy));
                m.scale(scale / 50.0F);
            }

            case Otkisuli -> {
                m.translate((float) (cx), (float) (cy));
                m.rotate(adjustedProgress * 2f);
                m.scale(1.5f + adjustedProgress * 0.05f);
            }

            case Insert -> {
                m.translate((float) (cx), (float) (cy));
                m.rotate(adjustedProgress * 3f);
                m.scale(Math.max(0.25f, 2.5f - adjustedProgress * 0.15f));
            }

            case Fall -> {
                float downFactor = (float) (Math.pow(adjustedProgress, 3) * 40f);
                m.translate((float) (cx), (float) (cy + downFactor));
                m.rotate(adjustedProgress * 5f);
                m.scale(2f);
            }

            case Rocket -> {
                float downFactor = (float) (Math.pow(adjustedProgress, 3) * 40f) - 20;
                m.translate((float) (cx), (float) (cy - downFactor));
                m.rotate(adjustedProgress * floatingItemTimeLeft * 0.2f);
                m.scale(2f);
            }

            case Roll -> {
                float rightFactor = (float) (Math.pow(adjustedProgress, 2) * 20f);
                m.translate((float) (cx + rightFactor), (float) (cy));
                m.rotate(adjustedProgress * 40f);
                m.scale(2f);
            }
        }

        context.drawItem(floatingItem, -8, -8);
        m.popMatrix();
    }

    private int getTime() {
        int invertedSpeed = 101 - speed.getValue();

        if (mode.is(Mode.FadeOut))
            return invertedSpeed / 4;

        if (mode.is(Mode.Insert))
            return invertedSpeed / 2;

        return invertedSpeed;
    }

    private enum Mode {
        FadeOut, Size, Otkisuli, Insert, Fall, Rocket, Roll, Off
    }
}
