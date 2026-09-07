package ru.expensive.implement.screens.menu.components;

import net.minecraft.client.gui.DrawContext;

public interface Component {
    void render(DrawContext context, int mouseX, int mouseY, float delta);

    void tick();

    boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled);

    boolean mouseReleased(net.minecraft.client.gui.Click click);

    boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY);

    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);

    boolean keyPressed(net.minecraft.client.input.KeyInput input);

    boolean charTyped(net.minecraft.client.input.CharInput input);

    boolean isHover(double mouseX, double mouseY);
}
