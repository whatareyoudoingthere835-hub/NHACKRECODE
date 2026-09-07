package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.other.ButtonComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;
import ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.component.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorWindow extends AbstractWindow {
    private final List<AbstractComponent> components = new ArrayList<>();

    private final ButtonComponent saveButtonComponent = new ButtonComponent();
    private final ButtonComponent closeButtonComponent = new ButtonComponent();

    private final HueComponent hueComponent;
    private final SaturationComponent saturationComponent;
    private final AlphaComponent alphaComponent;
    private final ColorEditorComponent colorEditorComponent;
    private final ColorPresetComponent colorPresetComponent;

    public ColorWindow(ColorSetting setting) {

        components.addAll(
                Arrays.asList(
                        hueComponent = new HueComponent(setting),
                        saturationComponent = new SaturationComponent(setting),
                        alphaComponent = new AlphaComponent(setting),
                        colorEditorComponent = new ColorEditorComponent(setting),
                        colorPresetComponent = new ColorPresetComponent(setting),
                        saveButtonComponent,
                        closeButtonComponent
                )
        );
    }

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(12)
                .color(0xDC090B15)
                .build()
        );

        rectangle.render(ShapeProperties.create(positionMatrix, x, y + height - 21, width, 21)
                .round(0, 12, 0, 12)
                .color(0xFF141524)
                .build()
        );

        Fonts.getSize(14).drawString(context.getMatrices(), "ColorPicker", x + 6, y + 7, -1);

        alphaComponent.position(x, y);
        hueComponent.position(x, y);
        saturationComponent.position(x, y);
        colorEditorComponent.position(x, y);

        saveButtonComponent.setText("Save")
                .setRunnable(() -> windowManager.delete(this))
                .position(x + 119, y + height - 17);

        ((ButtonComponent) closeButtonComponent.setText("Close")
                .setRunnable(() -> windowManager.delete(this))
                .position(x + 90, y + height - 17))
                .setColor(0x00000000);

        height = ((ColorPresetComponent) colorPresetComponent.position(x, y))
                .getWindowHeight();

        components.forEach(component -> component.render(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        draggable(MathUtil.isHovered(mouseX, mouseY, x, y, width, 17));
        components.forEach(component -> component.mouseClicked(click, doubled));
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        components.forEach(component -> component.mouseScrolled(mouseX, mouseY, horizontalAmount, amount));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        components.forEach(component -> component.mouseReleased(click));
        return super.mouseReleased(click);
    }
}
