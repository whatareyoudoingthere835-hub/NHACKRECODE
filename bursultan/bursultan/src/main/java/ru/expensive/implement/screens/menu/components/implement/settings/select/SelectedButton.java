package ru.expensive.implement.screens.menu.components.implement.settings.select;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.Direction;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;
import static ru.expensive.common.util.math.MathUtil.*;

public class SelectedButton extends AbstractComponent {
    private final SelectSetting setting;
    private final String text;

    @Setter
    @Accessors(chain = true)
    private int alpha;

    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(300)
            .setValue(0x2D);

    public SelectedButton(SelectSetting setting, String text) {
        this.setting = setting;
        this.text = text;

        alphaAnimation.setDirection(Animation.Direction.BACKWARDS);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix3x2fStack matrices = context.getMatrices();
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        alphaAnimation.setDirection(setting.getSelected().contains(text)
                ? Animation.Direction.FORWARDS
                : Animation.Direction.BACKWARDS
        );

        alphaAnimation.setMs(400);

        int opacity = alphaAnimation
                .getOutput()
                .intValue();

        int selectedOpacity = applyOpacity(
                applyOpacity(0xFF2D2E41, opacity),
                alpha
        );

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .color(selectedOpacity)
                .build()
        );

        int checkOpacity = applyOpacity(
                applyOpacity(-1, opacity * 5),
                alpha
        );

        image.setMatrixStack(matrices)
                .setTexture("textures/check.png")
                .render(ShapeProperties.create(positionMatrix, x + width - 8, y + 4.5F, 4, 4)
                        .color(checkOpacity)
                        .build()
                );

        Fonts.getSize(12, BOLD).drawString(matrices, text, x + 4, y + 5, applyOpacity(0xFFD4D6E1, alpha));
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            setting.setSelected(text);
        }
        return super.mouseClicked(click, doubled);
    }
}
