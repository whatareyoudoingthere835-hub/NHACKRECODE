package ru.expensive.api.feature.draggable;

import net.minecraft.client.gui.DrawContext;

public interface Draggable {
    boolean visible();

    void tick(float delta);

    void render(DrawContext context, int mouseX, int mouseY, float delta);

    boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled);

    boolean mouseReleased(net.minecraft.client.gui.Click click);
}
