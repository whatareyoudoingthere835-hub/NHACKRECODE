package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.common.util.player.MovingUtil;
import ru.expensive.implement.screens.menu.MenuScreen;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.category.CategoryComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.TextComponent;

import java.util.ArrayList;
import java.util.List;

@Setter
@Accessors(chain = true)
public class CategoryContainerComponent extends AbstractComponent {
    private final List<CategoryComponent> categoryComponents = new ArrayList<>();
    private MenuScreen menuScreen;

    public void initializeCategoryComponents() {
        categoryComponents.clear();
        for (ModuleCategory category : ModuleCategory.values()) {
            categoryComponents.add(new CategoryComponent(category, menuScreen));
        }
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float offset = 0;

        for (CategoryComponent component : categoryComponents) {
            component.x = x + 6;
            component.y = y + 50 + offset;
            component.width = 73;
            component.height = 17;
            component.render(context, mouseX, mouseY, delta);
            offset += component.height + 2;
        }
    }

    @Override
    public void tick() {
        boolean typing = TextComponent.typing;
        if (!typing) {
            for (KeyBinding keyBinding : MovingUtil.getMovementKeys(false)) {
                int keyCode = keyBinding.getDefaultKey().getCode();
                keyBinding.setPressed(InputUtil.isKeyPressed(mc.getWindow(), keyCode));
            }
        } else {
            mc.options.jumpKey.setPressed(false);
            mc.options.forwardKey.setPressed(false);
            mc.options.rightKey.setPressed(false);
            mc.options.leftKey.setPressed(false);
            mc.options.backKey.setPressed(false);
        }

        for (CategoryComponent component : categoryComponents) {
            component.tick();
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseClicked(click, doubled));
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseReleased(click));
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseDragged(click, deltaX, deltaY));
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseScrolled(mouseX, mouseY, horizontalAmount, amount));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        categoryComponents.forEach(categoryComponent -> categoryComponent.keyPressed(input));
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        categoryComponents.forEach(categoryComponent -> categoryComponent.charTyped(input));
        return super.charTyped(input);
    }
}
