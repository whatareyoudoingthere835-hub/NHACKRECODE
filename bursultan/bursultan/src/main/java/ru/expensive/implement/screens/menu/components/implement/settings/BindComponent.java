package ru.expensive.implement.screens.menu.components.implement.settings;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.api.feature.module.setting.implement.BindSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import org.lwjgl.glfw.GLFW;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class BindComponent extends AbstractSettingComponent {
    private final BindSetting setting;
    private boolean binding;

    public BindComponent(BindSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        var bindName = StringUtil.getBindName(setting.getKey());
        var stringWidth = Fonts.getSize(13, BOLD).getStringWidth(bindName) - 2;
        var wrapped = StringUtil.wrap(setting.getDescription(), (int) (width - stringWidth - 28), 12);

        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        rectangle.render(ShapeProperties.create(positionMatrix, x + width - stringWidth - 17, y + 5, stringWidth + 10, 12)
                .round(4)
                .thickness(1.0f)
                .outlineColor(0x902D2E41)
                .color(0xFF161825)
                .build()
        );

        int bindingColor = binding
                ? 0xFF8187ff
                : ColorHelper.getArgb(255, 135, 136, 148);

        Fonts.getSize(13, BOLD).drawString(context.getMatrices(), bindName, x + width - 12 - stringWidth - 1, y + 9.5, bindingColor);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == 0) {
            if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
                binding = !binding;
            } else {
                binding = false;
            }
        }

        if (binding && button > 1) {
            setting.setKey(button);
            binding = false;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setting.setKey(GLFW.GLFW_KEY_UNKNOWN);
            } else {
                setting.setKey(keyCode);
            }
            binding = false;
        }
        return super.keyPressed(input);
    }
}
