package ru.expensive.implement.screens.menu.components.implement.window;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WindowManager extends AbstractComponent {
    private final List<AbstractWindow> windows = new ArrayList<>();

    public void add(AbstractWindow window) {
        windows.add(window);
    }

    public void delete(AbstractWindow window) {
        window.startCloseAnimation();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<AbstractWindow> toRemove = new ArrayList<>();
        windows.forEach(window -> {
            window.render(context, mouseX, mouseY, delta);

            if (window.isCloseAnimationFinished()) {
                toRemove.add(window);
            }
        });
        windows.removeAll(toRemove);
    }


    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        boolean clickedInsideWindow = false;

        List<AbstractWindow> windowsCopy = new ArrayList<>(windows);

        for (int i = windowsCopy.size() - 1; i >= 0; i--) {
            AbstractWindow window = windowsCopy.get(i);
            if (window.isHovered(mouseX, mouseY) || isHover(mouseX, mouseY)) {
                clickedInsideWindow = true;
                window.mouseClicked(click, doubled);
                break;
            }
        }

        if (!clickedInsideWindow) {
            for (AbstractWindow window : windows) {
                window.startCloseAnimation();

            }
            return false;
        }

        return clickedInsideWindow;
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        windows.forEach(window -> window.isHovered(mouseX, mouseY));
        
        for (AbstractWindow window : windows) {
            if (window.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isHover(mouseX, mouseY);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        windows.forEach(window -> window.charTyped(input));
        return super.charTyped(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        for (AbstractWindow window : windows) {
            if (window.mouseScrolled(mouseX, mouseY, horizontalAmount, amount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        windows.forEach(window -> window.keyPressed(input));
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        windows.forEach(window -> window.mouseReleased(click));
        return super.mouseReleased(click);
    }
}

