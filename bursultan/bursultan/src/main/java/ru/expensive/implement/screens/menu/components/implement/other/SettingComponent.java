package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

@Setter
@Accessors(chain = true)
public class SettingComponent extends AbstractComponent {
    private Runnable runnable;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/settings.png")
                .render(
                        ShapeProperties.create(positionMatrix, x, y, 7, 7)
                                .color(0xFFafb0bc)
                                .build()
                );
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (MathUtil.isHovered(mouseX, mouseY, x, y, 7, 7) && button == 0) {
            runnable.run();
        }
        return super.mouseClicked(click, doubled);
    }
}
