package ru.expensive.implement.features.modules.render.esp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import ru.expensive.api.feature.module.setting.implement.GroupSetting;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.ShapeRenderer;
import ru.expensive.common.QuickImports;

public class NameTags {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void render(MatrixStack matrixStack, GroupSetting settings) {
        MultiSelectSetting displaySetting = (MultiSelectSetting) settings.getSubSetting("Display");
        if (!displaySetting.isSelected("Name") || mc.world == null) return;

        boolean showHealth = displaySetting.isSelected("Health");
        boolean showPrefix = displaySetting.isSelected("Prefix");
        boolean showFriends = displaySetting.isSelected("Friends");

        Camera camera = mc.gameRenderer.getCamera();
        if (camera == null) return;

        setupRendering();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player && player.isAlive()) {
                renderNameTag(matrixStack, player, camera, showHealth, showPrefix, showFriends);
            }
        }

        resetRendering();
    }

    private static void setupRendering() {
    }

    private static void resetRendering() {
    }

    private static void renderNameTag(MatrixStack matrixStack, PlayerEntity player, Camera camera, boolean showHealth, boolean showPrefix, boolean showFriends) {
        Vec3d pos = player.getLerpedPos(mc.getRenderTickCounter().getTickProgress(true)).subtract(camera.getCameraPos());
        matrixStack.push();
        setupNameTagTransform(matrixStack, pos, player, camera);

        String displayName = player.getDisplayName().getString();
        String name = player.getName().getString();
        String prefix = showPrefix && displayName.indexOf(name) > 0 ? displayName.substring(0, displayName.indexOf(name)) : "";
        String healthText = showHealth ? String.format(" %.1f", player.getHealth()) : "";
        String friendTag = showFriends && FriendRepository.isFriend(name) ? "[❤] " : "";

        float width = calculateWidth(name, healthText, prefix, friendTag, showHealth);
        float height = 10;

        drawBackground(matrixStack, width, height);
        drawTags(matrixStack, friendTag, prefix, name, healthText, showHealth, width, height);

        matrixStack.pop();
    }

    private static float calculateWidth(String name, String healthText, String prefix, String friendTag, boolean showHealth) {
        float padding = 8;
        float spacing = 4;
        float friendWidth = !friendTag.isEmpty() ? Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(friendTag) : 0;
        float prefixWidth = !prefix.isEmpty() ? Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(prefix) : 0;
        float nameWidth = Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(name);
        float healthWidth = showHealth ? Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(healthText) : 0;
        float iconWidth = showHealth ? 4 : 0;

        return nameWidth + healthWidth + iconWidth + padding +
                (showHealth ? spacing : 0) +
                (!prefix.isEmpty() ? prefixWidth + spacing : 0) +
                (!friendTag.isEmpty() ? friendWidth + spacing : 0);
    }

    private static void drawTags(MatrixStack matrixStack, String friendTag, String prefix, String name, String healthText, boolean showHealth, float width, float height) {
        float centerY = -height / 2 + (height - 3) / 2;
        float startX = -width / 2 + 4;

        if (!friendTag.isEmpty()) {
            Fonts.getSize(13, Fonts.Type.DEFAULT).drawString(matrixStack, friendTag, startX, centerY, 0xFF00FF00);
            startX += Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(friendTag) + 4;
        }

        if (!prefix.isEmpty()) {
            Fonts.getSize(13, Fonts.Type.DEFAULT).drawString(matrixStack, prefix, startX, centerY, 0xFF00FF00);
            startX += Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(prefix) + 4;
        }

        Fonts.getSize(13, Fonts.Type.DEFAULT).drawString(matrixStack, name, startX, centerY, 0xFFFFFFFF);

        if (showHealth) {
            float healthX = startX + Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(name) + 4;
            Fonts.getSize(13, Fonts.Type.DEFAULT).drawString(matrixStack, healthText, healthX, centerY + 0.2f, 0xFF8187FF);
            drawHealthIcon(matrixStack, healthX + Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(healthText) + 1, centerY - 0.4f);
        }
    }

    private static void drawHealthIcon(MatrixStack matrixStack, float x, float y) {
        QuickImports.image.setMatrixStack(matrixStack)
                .setTexture("textures/health.png")
                .render(ShapeProperties.create(matrixStack.peek().getPositionMatrix(), x, y, 4, 4).color(0xFFFFFFFF).build());
    }

    private static void setupNameTagTransform(MatrixStack matrixStack, Vec3d pos, PlayerEntity player, Camera camera) {
        matrixStack.translate(pos.x, pos.y + player.getHeight() + 0.5f, pos.z);
        matrixStack.multiply(new Quaternionf().rotateY((float) Math.toRadians(-camera.getYaw())).rotateX((float) Math.toRadians(camera.getPitch())));
        float scale = 0.015f * (float) (Math.max(1.0f, pos.length() * 0.25));
        matrixStack.scale(-scale, -scale, scale);
    }

    private static void drawBackground(MatrixStack matrixStack, float width, float height) {
        ShapeRenderer.drawRoundedRect(matrixStack, -width / 2, -height / 2, width, height, 2.0f, 0xCC141724);
        ShapeRenderer.drawRoundedRectOutline(matrixStack, -width / 2, -height / 2, width, height, 2.0f, 1.0f, 0xFF2D2E41);
    }
}