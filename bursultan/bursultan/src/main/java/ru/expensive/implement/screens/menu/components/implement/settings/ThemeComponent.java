package ru.expensive.implement.screens.menu.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.common.util.math.MathUtil;
import org.joml.Vector4i;
import ru.expensive.api.feature.module.setting.implement.ThemeSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;

import static ru.expensive.api.system.font.Fonts.Type.*;

public class ThemeComponent extends AbstractSettingComponent {
    private final ThemeSetting setting;

    public ThemeComponent(ThemeSetting setting) {
        super(setting);
        this.setting = setting;
        this.height = 24;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        rectangle.render(ShapeProperties.create(positionMatrix, x + 5, y + 4, width - 11, height - 6)
                .round(4)
                .thickness(1.0F)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        Fonts.getSize(14, BOLD).drawString(
                context.getMatrices(),
                setting.getName(),
                x + 9,
                y + 12,
                0xFFD4D6E1
        );

        Vector4i gradientColors = new Vector4i(
                setting.getEndColor(),
                setting.getEndColor(),
                setting.getStartColor(),
                setting.getStartColor()
        );

        rectangle.render(
                ShapeProperties.create(positionMatrix, x + width - 40, y + 8, 30, 10)
                        .round(4)
                        .color(gradientColors)
                        .build()
        );
    }
}