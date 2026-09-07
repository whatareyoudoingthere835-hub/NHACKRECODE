package ru.expensive.implement.screens.menu.components.implement.module;

import ru.expensive.implement.screens.menu.MenuScreen;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.setting.SettingComponentAdder;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.other.CheckComponent;
import ru.expensive.implement.screens.menu.components.implement.other.SettingComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.AbstractSettingComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.select.SelectComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.multiselect.MultiSelectComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.ModuleBindWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;
import static ru.expensive.api.system.font.Fonts.Type.DEFAULT;

@Getter
public class ModuleComponent extends AbstractComponent {
    private final List<AbstractSettingComponent> components = new ArrayList<>();
    private final CheckComponent checkComponent = new CheckComponent();
    private final SettingComponent settingComponent = new SettingComponent();
    private MenuScreen menuScreen;
    private final Module module;

    public ModuleComponent(Module module) {
        this.module = module;

        new SettingComponentAdder().addSettingComponent(
                module.settings(),
                components
        );
    }

    public void setMenuScreen(MenuScreen menuScreen) {
        this.menuScreen = menuScreen;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        height = getComponentHeight();

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, 18)
                .round(12, 0, 12, 0)
                .color(0xFF191a28)
                .build()
        );

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(12)
                .softness(1)
                .thickness(2.2F)
                .outlineColor(0x902d2e41)
                .color(0x002d2e41)
                .build()
        );

        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/ico.png")
                .render(ShapeProperties.create(positionMatrix, x + 9, y + 4.5F, 9, 9)
                        .build()
                );

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), module.getVisibleName(), x + 23, y + 7, 0xFFD4D6E1);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), "Enable", x + 9, y + 27, 0xFFD4D6E1);
        Fonts.getSize(12, DEFAULT).drawString(context.getMatrices(), "Enable the feature.", x + 9, y + 36, 0xFF878894);

        ((CheckComponent) checkComponent.position(x + width - 16, y + 28.5F))
                .setRunnable(module::switchState)
                .setState(module.isState())
                .render(context, mouseX, mouseY, delta);

        ((SettingComponent) settingComponent.position(x + width - 28, y + 28.5F))
                .setRunnable(() -> spawnWindow(mouseX, mouseY))
                .render(context, mouseX, mouseY, delta);

        drawBind(context, positionMatrix);

        float currY = y + 46;
        for (AbstractSettingComponent component : components) {
            var visible = component.getSetting()
                    .getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            component.x = x;
            component.y = currY;
            component.width = width;

            component.render(context, mouseX, mouseY, delta);

            currY += component.height;
        }
    }

    public boolean hasOpenDropdown() {
        for (AbstractSettingComponent component : components) {
            if (component instanceof SelectComponent sc && sc.isOpenOrAnimating()) return true;
            if (component instanceof MultiSelectComponent msc && msc.isOpenOrAnimating()) return true;
        }
        return false;
    }

    public void renderOpenDropdown(DrawContext context, int mouseX, int mouseY, float delta) {
        for (AbstractSettingComponent component : components) {
            if (component instanceof SelectComponent sc && sc.isOpenOrAnimating()) {
                sc.renderDropdown(context, mouseX, mouseY, delta);
            } else if (component instanceof MultiSelectComponent msc && msc.isOpenOrAnimating()) {
                msc.renderDropdown(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        for (AbstractSettingComponent component : components) {
            if ((component instanceof SelectComponent sc && sc.isHover(mouseX, mouseY))
             || (component instanceof MultiSelectComponent msc && msc.isHover(mouseX, mouseY))) {
                component.mouseClicked(click, doubled);
                return super.mouseClicked(click, doubled);
            }
        }

        boolean isAnyComponentHovered = components
                .stream()
                .anyMatch(abstractComponent -> abstractComponent.isHover(mouseX, mouseY));

        if (isAnyComponentHovered) {
            components.forEach(abstractComponent -> {
                if (abstractComponent.isHover(mouseX, mouseY)) {
                    abstractComponent.mouseClicked(click, doubled);
                }
            });
            return super.mouseClicked(click, doubled);
        }

        checkComponent.mouseClicked(click, doubled);
        settingComponent.mouseClicked(click, doubled);

        components.forEach(abstractComponent -> abstractComponent.mouseClicked(click, doubled));
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return MathUtil.isHovered(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void tick() {
        for (AbstractComponent component : components) {
            component.tick();
        }
        super.tick();
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        components.forEach(abstractComponent -> abstractComponent.mouseDragged(click, deltaX, deltaY));
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        components.forEach(abstractComponent -> abstractComponent.mouseReleased(click));
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        components.forEach(abstractComponent -> abstractComponent.mouseScrolled(mouseX, mouseY, horizontalAmount, amount));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        components.forEach(abstractComponent -> abstractComponent.keyPressed(input));
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        components.forEach(abstractComponent -> abstractComponent.charTyped(input));
        return super.charTyped(input);
    }

    public int getComponentHeight() {
        float offsetY = 0;
        for (AbstractSettingComponent component : components) {
            var visible = component.getSetting()
                    .getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            offsetY += component.height;
        }
        return (int) (offsetY + 46);
    }

    private void drawBind(DrawContext context, Matrix4f positionMatrix) {
        String bindName = StringUtil.getBindName(module.getKey());
        float stringWidth = Fonts.getSize(12, BOLD).getStringWidth(bindName);

        rectangle.render(ShapeProperties.create(positionMatrix, x + width - stringWidth - 15, y + 4.5F, stringWidth + 6, 9)
                .round(4)
                .thickness(1)
                .softness(1)
                .outlineColor(0xFF282932)
                .color(0xFF161725)
                .build()
        );

        int bindingColor = ColorHelper.getArgb(255, 135, 136, 148);
        Fonts.getSize(12, BOLD).drawString(context.getMatrices(), bindName, x + width - 12 - stringWidth, y + 8, bindingColor);
    }

    private void spawnWindow(int mouseX, int mouseY) {
        AbstractWindow existingWindow = null;

        for (AbstractWindow window : windowManager.getWindows()) {
            if (window instanceof ModuleBindWindow) {
                existingWindow = window;
                break;
            }
        }

        if (existingWindow != null) {
            windowManager.delete(existingWindow);
        } else {
            AbstractWindow moduleBindWindow = new ModuleBindWindow(module)
                    .position(mouseX + 5, mouseY + 5)
                    .size(105, 55)
                    .draggable(false);

            windowManager.add(moduleBindWindow);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleComponent that = (ModuleComponent) o;
        return module.equals(that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module);
    }
}
