package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

public class SearchComponent extends AbstractComponent {
    @Getter
    private String text = "";

    private int cursorPosition = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean isTyping = false;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        width = 80;
        height = 15;

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(7.5F)
                .thickness(1.0F)
                .outlineColor(0xFF11121C)
                .color(0x54191A28)
                .build());

        image.setTexture("textures/search.png").render(
                ShapeProperties.create(positionMatrix, x + width - 12, y + 5, 5F, 5F)
                        .build()
        );

        FontRenderer fontRenderer = Fonts.getSize(12);
        String displayText = text.equalsIgnoreCase("") && !isTyping ? "Search" : text;
        fontRenderer.drawString(context.getMatrices(), displayText, x + 7, y + 6.5, 0xFF878894);

        if (isTyping) {
            float cursorX = x + 7 + fontRenderer.getStringWidth(text.substring(0, cursorPosition));
            rectangle.render(ShapeProperties.create(positionMatrix, cursorX, y + 4, 0.5, height - 8)
                    .color(0xFFFFFFFF)
                    .build());
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
            cursorPosition = getClickedPosition(mouseX);
            selectionStart = -1;
            selectionEnd = -1;
            isTyping = true;
            return true;
        } else {
            isTyping = false;
            return super.mouseClicked(click, doubled);
        }
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        if (isTyping && Fonts.getSize(12).getStringWidth(text) < 55) {
            updateText(chr);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        if (isTyping) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> handleBackspace();
                case GLFW.GLFW_KEY_LEFT -> {
                    if (cursorPosition > 0) {
                        cursorPosition--;
                    }
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    if (cursorPosition < text.length()) {
                        cursorPosition++;
                    }
                }
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_ESCAPE -> isTyping = false;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (isTyping && button == 0 && selectionStart != -1 && selectionEnd == -1) {
            selectionEnd = cursorPosition;
        }
        return super.mouseReleased(click);
    }

    private void handleBackspace() {
        if (cursorPosition > 0) {
            if (selectionStart != -1 && selectionEnd != -1) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                text = text.substring(0, start) + text.substring(end);
                cursorPosition = start;
                selectionStart = -1;
                selectionEnd = -1;
            } else {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
        }
    }

    private void updateText(char chr) {
        if (selectionStart != -1 && selectionEnd != -1) {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            text = text.substring(0, start) + chr + text.substring(end);
            cursorPosition = start + 1;
            selectionStart = -1;
            selectionEnd = -1;
        } else {
            text = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
            cursorPosition++;
        }
    }

    private int getClickedPosition(double mouseX) {
        FontRenderer fontRenderer = Fonts.getSize(12);
        int relativeX = (int) (mouseX - x - 7);
        for (int i = 0; i <= text.length(); i++) {
            if (fontRenderer.getStringWidth(text.substring(0, i)) > relativeX) {
                return i;
            }
        }
        return text.length();
    }
}
