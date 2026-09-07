package ru.expensive.implement.screens.menu;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.other.*;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.InfoWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuScreen extends Screen implements QuickImports {
    private final List<AbstractComponent> components = new ArrayList<>();

    private final BackgroundComponent backgroundComponent = new BackgroundComponent();
    private final UserComponent userComponent = new UserComponent();
    private final LanguageComponent languageComponent = new LanguageComponent();
    @Getter
    private final SearchComponent searchComponent = new SearchComponent();
    private final CategoryContainerComponent categoryContainerComponent = new CategoryContainerComponent();
    @Getter

    public int x, y, width, height;

    private static ModuleCategory lastCategory = ModuleCategory.COMBAT;

    public ModuleCategory category;

    private final Animation scaleAnimation = new DecelerateAnimation()
            .setMs(150)
            .setValue(1);

    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(400)
            .setValue(100);

    private final long openedAt;
    private boolean closing = false;

    public MenuScreen() {
        super(Text.of("Extra client menu"));

        this.openedAt = System.currentTimeMillis();
        this.category = lastCategory;

        scaleAnimation.setDirection(Animation.Direction.FORWARDS);
        alphaAnimation.setDirection(Animation.Direction.FORWARDS);

        categoryContainerComponent
                .setMenuScreen(this)
                .initializeCategoryComponents();

        components.addAll(
                Arrays.asList(
                        backgroundComponent,
                        userComponent,
                        languageComponent,
                        searchComponent,
                        categoryContainerComponent
                )
        );
    }

    @Override
    public void tick() {
        if (closing) {
            close();
        }
        components.forEach(AbstractComponent::tick);
        super.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        x = window.getScaledWidth() / 2 - 200;
        y = window.getScaledHeight() / 2 - 125;
        width = 400;
        height = 250;

        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        ru.expensive.common.util.render.ExpensiveRenderLayers.beginGuiFrame();
        try {
            if (InfoWindow.isDarkBackground()) {
                int opacity = alphaAnimation
                        .getOutput()
                        .intValue();

                rectangle.render(ShapeProperties.create(positionMatrix, 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight())
                        .color(MathUtil.applyOpacity(0xFF000000, opacity))
                        .build()
                );
            }

            backgroundComponent.setMenuScreen(this)
                    .position(x, y)
                    .size(width, height);

            userComponent.setMenuScreen(this)
                    .position(x, y + height);

            languageComponent.position(x + 261, y + 6);
            searchComponent.position(x + 300, y + 6);
            categoryContainerComponent.position(x, y);

            MathUtil.scale(context.getMatrices(), x + (float) width / 2, y + (float) height / 2, getScaleAnimation(), () -> {
                components.forEach(component -> component.render(context, mouseX, mouseY, delta));
                windowManager.render(context, mouseX, mouseY, delta);
            });
        } finally {
            ru.expensive.common.util.render.ExpensiveRenderLayers.endGuiFrame();
        }

        super.render(context, mouseX, mouseY, delta);
    }

    public float getScaleAnimation() {
        return scaleAnimation.getOutput().floatValue();
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (!windowManager.mouseClicked(click, doubled)) {
            components.forEach(component -> component.mouseClicked(click, doubled));
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        components.forEach(component -> component.mouseReleased(click));
        windowManager.mouseReleased(click);
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (!windowManager.mouseDragged(click, deltaX, deltaY)) {
            components.forEach(component -> component.mouseDragged(click, deltaX, deltaY));
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!windowManager.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            components.forEach(component -> component.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        if ((keyCode == 256 || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT) && shouldCloseOnEsc()) {
            if (System.currentTimeMillis() - openedAt > 200 && !closing) {
                closing = true;
                scaleAnimation.setDirection(Animation.Direction.BACKWARDS);
                alphaAnimation.setDirection(Animation.Direction.BACKWARDS);
            }
            return true;
        }

        if (!windowManager.keyPressed(input)) {
            components.forEach(component -> component.keyPressed(input));
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        if (!windowManager.charTyped(input)) {
            components.forEach(component -> component.charTyped(input));
        }
        return super.charTyped(input);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (!closing) {
            closing = true;
            scaleAnimation.setDirection(Animation.Direction.BACKWARDS);
            alphaAnimation.setDirection(Animation.Direction.BACKWARDS);
        }
        if (scaleAnimation.isFinished(Animation.Direction.BACKWARDS)) {
            lastCategory = this.category;

            windowManager.getWindows().forEach(abstractWindow -> {
                if (!(abstractWindow instanceof InfoWindow)) {
                    windowManager.delete(abstractWindow);
                }
            });
            super.close();
        }
    }
}
