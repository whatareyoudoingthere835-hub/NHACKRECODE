package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

@RequiredArgsConstructor
public class ColorPresetButton extends AbstractComponent {
    private final ColorSetting setting;
    private final int color;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, 8, 8)
                .round(4)
                .color(color)
                .build()
        );
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (MathUtil.isHovered(mouseX, mouseY, x, y, 8, 8) && button == 0) {
            setting.setColor(color);
        }
        return super.mouseClicked(click, doubled);
    }
}
