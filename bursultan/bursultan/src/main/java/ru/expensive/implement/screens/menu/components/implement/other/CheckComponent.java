package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.render.Stencil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static ru.expensive.api.system.animation.Direction.BACKWARDS;
import static ru.expensive.api.system.animation.Direction.FORWARDS;

@Setter
@Accessors(chain = true)
public class CheckComponent extends AbstractComponent {
    private boolean state;
    private Runnable runnable;

    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(300)
            .setValue(255);

    private final Animation stencilAnimation = new DecelerateAnimation()
            .setMs(200)
            .setValue(8);

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        alphaAnimation.setDirection(state ? Animation.Direction.FORWARDS : Animation.Direction.BACKWARDS);
        stencilAnimation.setDirection(state ? Animation.Direction.FORWARDS : Animation.Direction.BACKWARDS);

        int stateColor = state
                ? 0xFF8187ff
                : 0x00161725;

        int outlineStateColor = state
                ? 0xFF8187ff
                : 0xFF282932;

        int opacity = alphaAnimation
                .getOutput()
                .intValue();

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, 8, 8)
                .round(2)
                .thickness(1.0F)
                .outlineColor(outlineStateColor)
                .color(MathUtil.applyOpacity(stateColor, opacity))
                .build()
        );

        if (opacity > 0) {
            image.setMatrixStack(context.getMatrices())
                    .setTexture("textures/check.png")
                    .render(
                            ShapeProperties.create(positionMatrix, x + 2, y + 2, 4, 4)
                                    .color(MathUtil.applyOpacity(0xFFFFFFFF, opacity))
                                    .build()
                    );
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (MathUtil.isHovered(mouseX, mouseY, x, y, 8, 8) && button == 0) {
            runnable.run();
        }
        return super.mouseClicked(click, doubled);
    }
}
