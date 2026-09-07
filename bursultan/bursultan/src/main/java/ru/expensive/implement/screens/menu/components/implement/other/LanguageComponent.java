package ru.expensive.implement.screens.menu.components.implement.other;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.localization.Language;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.core.Extra;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

public class LanguageComponent extends AbstractComponent {

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        width = 31;
        height = 15;

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(7.5F)
                .thickness(1.0F)
                .outlineColor(0xAF11121C)
                .color(0x1A191A28)
                .build()
        );

        image.setTexture("textures/locate.png").render(
                ShapeProperties.create(positionMatrix, x + 5, y + 4.5, 6F, 6F)
                        .build()
        );

        Fonts.getSize(12).drawString(context.getMatrices(), Extra.getInstance().getLanguage().name(), x + 13.3, y + 6.5, 0xFF878894);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            Extra.getInstance().toggleLanguage();
        }
        return super.mouseClicked(click, doubled);
    }
}
