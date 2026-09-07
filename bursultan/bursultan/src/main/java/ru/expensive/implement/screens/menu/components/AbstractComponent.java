package ru.expensive.implement.screens.menu.components;

import ru.expensive.common.QuickImports;
import ru.expensive.common.trait.ResizableMovable;

public abstract class AbstractComponent implements Component, QuickImports, ResizableMovable {
    public float x, y, width, height;

    public double scroll = 0;
    public double smoothedScroll = 0;

    @Override
    public ResizableMovable position(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public ResizableMovable size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        return false;
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        return false;
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        return false;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        return false;
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        return false;
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        return false;
    }
}
