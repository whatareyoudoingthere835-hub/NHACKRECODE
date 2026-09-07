package ru.expensive.implement.screens.menu.components.implement.window.implement.module;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;

public class ThemeWindow extends AbstractWindow {

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix3x2fStack matrices = context.getMatrices();
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        renderImage("textures/about.png",
                positionMatrix,
                x,
                y,
                width,
                height
        );

        FontRenderer fontBold = Fonts.getSize(14, Fonts.Type.BOLD);
        fontBold.drawString(matrices, "Theme Settings", x + 13, y + 16, 0xFFD4D6E1);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        draggable(MathUtil.isHovered(mouseX, mouseY, x, y, width, 40));
        return super.mouseClicked(click, doubled);
    }

    private void renderImage(String texturePath, Matrix4f positionMatrix, float x, float y, float width, float height) {
        image.setTexture(texturePath).render(
                ShapeProperties.create(positionMatrix, x, y, width, height)
                        .build()
        );
    }
}