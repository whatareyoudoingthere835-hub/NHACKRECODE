package ru.expensive.implement.screens.menu.components.implement.window.implement.module;

import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.api.feature.module.setting.SettingComponentAdder;
import ru.expensive.api.feature.module.setting.SettingRepository;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.core.Extra;
import ru.expensive.core.client.ClientInfoProvider;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.AbstractSettingComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;

import java.util.ArrayList;
import java.util.List;

public class InfoWindow extends AbstractWindow {
    private final List<AbstractSettingComponent> components = new ArrayList<>();

    public static SettingRepository settingRepository = new SettingRepository();

    public static final BooleanSetting darkBackground = new BooleanSetting("Background", "Darkens the background")
            .setValue(true);

    public InfoWindow() {
        // settingRepository статический — setup только один раз, иначе
        // при каждом создании окна настройки дублируются (2x Menu Key и т.д.).
        if (settingRepository.settings().isEmpty()) {
            settingRepository.setup(darkBackground);
        }

        if (components.isEmpty()) {
            new SettingComponentAdder().addSettingComponent(
                    settingRepository.settings(),
                    components
            );
        }
    }

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        List<Setting> settings = settingRepository.settings();

        final int TEXT_COLOR = 0xFF878894;
        final int WHITE_COLOR = 0xFFD4D6E1;
        final int PADDING_X = 13;
        final int PADDING_Y = 16;
        final int SPACING_Y = 13;
        final int FONT_SIZE = 14;
        final int SMALL_LOGO_WIDTH = 58;
        final int SMALL_LOGO_HEIGHT = 11;
        final int INFO_START_Y = 42;

        Matrix3x2fStack matrices = context.getMatrices();
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        renderImage("textures/about.png",
                positionMatrix,
                x,
                y,
                width,
                height
        );

        renderImage("textures/logo.png",
                positionMatrix,
                x + PADDING_X,
                y + PADDING_Y,
                SMALL_LOGO_WIDTH,
                SMALL_LOGO_HEIGHT
        );

        ClientInfoProvider clientInfoProvider = Extra.getInstance().getClientInfoProvider();
        FontRenderer font = Fonts.getSize(FONT_SIZE);
        FontRenderer fontSemi = Fonts.getSize(FONT_SIZE, Fonts.Type.BOLD);
        float yOffset = y + INFO_START_Y;

        drawText(matrices, fontSemi, font, "Username: ", "yatwinkle", x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Verison: ", clientInfoProvider.clientVersion(), x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Branch: ", clientInfoProvider.clientBranch(), x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Updated: ", "22.05.2024", x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Valid until: ", "22.05.2034", x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);

        float offset = y + 106;
        for (int i = components.size() - 1; i >= 0; i--) {
            AbstractSettingComponent component = components.get(i);

            component.x = x + 4;
            component.y = offset + (getComponentHeight() - component.height);
            component.width = 130;
            component.render(context, mouseX, mouseY, delta);

            offset -= component.height;
        }
    }

    @Override
    public void tick() {
        for (AbstractSettingComponent component : components) {
            component.tick();
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        draggable(MathUtil.isHovered(mouseX, mouseY, x, y, width, 40));

        boolean isAnyComponentHovered = components
                .stream()
                .anyMatch(abstractComponent -> abstractComponent.isHover(mouseX, mouseY));

        if (isAnyComponentHovered) {
            components.forEach(abstractComponent -> {
                if (abstractComponent.isHover(mouseX, mouseY)) {
                    abstractComponent.mouseClicked(click, doubled);
                }
            });
            return super.mouseClicked(click, doubled);
        }

        components.forEach(abstractComponent -> abstractComponent.mouseClicked(click, doubled));
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        components.forEach(abstractComponent -> abstractComponent.isHover(mouseX, mouseY));

        for (AbstractSettingComponent abstractComponent : components) {
            if (abstractComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isHover(mouseX, mouseY);
    }

    public static boolean isDarkBackground() {
        return darkBackground.isValue();
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        components.forEach(abstractComponent -> abstractComponent.mouseDragged(click, deltaX, deltaY));
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        components.forEach(abstractComponent -> abstractComponent.mouseReleased(click));
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        components.forEach(abstractComponent -> abstractComponent.mouseScrolled(mouseX, mouseY, horizontalAmount, amount));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        components.forEach(abstractComponent -> abstractComponent.keyPressed(input));
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        components.forEach(abstractComponent -> abstractComponent.charTyped(input));
        return super.charTyped(input);
    }

    private void renderImage(String texturePath, Matrix4f positionMatrix, float x, float y, float width, float height) {
        image.setTexture(texturePath).render(
                ShapeProperties.create(positionMatrix, x, y, width, height)
                        .build()
        );
    }

    private void drawText(Matrix3x2fStack matrices, FontRenderer fontSemi, FontRenderer font, String label, String value, float x, float y, int labelColor, int valueColor) {
        fontSemi.drawString(matrices, label, x, y, labelColor);
        font.drawString(matrices, value, x + fontSemi.getStringWidth(label), y, valueColor);
    }

    public int getComponentHeight() {
        float offsetY = 0;
        for (AbstractComponent component : components) {
            offsetY += component.height;
        }
        return (int) (offsetY);
    }
}
