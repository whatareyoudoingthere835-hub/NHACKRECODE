package ru.expensive.implement.screens.menu.components.implement.settings.multiselect;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.Direction;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.common.util.render.Stencil;
import ru.expensive.core.Extra;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.render.ScissorManager;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.AbstractSettingComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.select.SelectedButton;

import java.util.ArrayList;
import java.util.List;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class MultiSelectComponent extends AbstractSettingComponent {
    private final List<MultiSelectedButton> multiSelectedButtons = new ArrayList<>();

    private final MultiSelectSetting setting;
    private boolean open;

    private float dropdownListX,
            dropDownListY,
            dropDownListWidth,
            dropDownListHeight;

    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(300)
            .setValue(255);

    public MultiSelectComponent(MultiSelectSetting setting) {
        super(setting);
        this.setting = setting;

        alphaAnimation.setDirection(Animation.Direction.BACKWARDS);

        for (String s : setting.getList()) {
            multiSelectedButtons.add(new MultiSelectedButton(setting, s));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix3x2fStack matrices = context.getMatrices();
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        String wrapped = StringUtil.wrap(setting.getDescription(), 45, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        List<String> fullSettingsList = setting.getList();

        this.dropdownListX = x + width - 75;
        this.dropDownListY = y + 20;
        this.dropDownListWidth = 66;
        this.dropDownListHeight = fullSettingsList.size() * 12 + 1.5F;

        alphaAnimation.setDirection(open
                ? Animation.Direction.FORWARDS
                : Animation.Direction.BACKWARDS
        );

        renderSelected(positionMatrix, matrices);

        Fonts.getSize(14, BOLD).drawString(matrices, setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(matrices, wrapped, x + 9, y + 15, 0xFF878894);
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isOpenOrAnimating() {
        return open || alphaAnimation.getOutput().intValue() > 0;
    }

    public void renderDropdown(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isOpenOrAnimating()) {
            Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);
            renderSelectList(context, mouseX, mouseY, delta, positionMatrix);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == 0) {
            if (MathUtil.isHovered(mouseX, mouseY, x + width - 75, y + 4, 66, 14)) {
                open = !open;
            } else if (open && !isHoveredList(mouseX, mouseY)) {
                open = false;
            }

            if (open) {
                multiSelectedButtons.forEach(selectedButton -> selectedButton.mouseClicked(click, doubled));
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        return open && isHoveredList(mouseX, mouseY);
    }

    private void renderSelected(Matrix4f positionMatrix, Matrix3x2fStack matrices) {
        rectangle.render(ShapeProperties.create(positionMatrix, x + width - 75, y + 4, 66, 14)
                .round(4)
                .thickness(1.0F)
                .outlineColor(0x902D2E41)
                .color(0xFF161825)
                .build()
        );

        String selectedName = String.join(", ", setting.getSelected());
        
        ScissorManager scissorManager = Extra.getInstance().getScissorManager();
        scissorManager.push(x + width - 75 + 2, y + 4, 62, 14);
        Fonts.getSize(12, BOLD).drawString(matrices, selectedName, x + width - 75 + 4, y + 10, 0xFFD4D6E1);
        scissorManager.pop();

        rectangle.render(ShapeProperties.create(positionMatrix, x + width - 75 + 44, y + 5, 20, 12)
                .round(3, 3, 0, 0)
                .color(0x00161825, 0x00161825, 0xFF161825, 0xFF161825)
                .build()
        );
    }

    private void renderSelectList(DrawContext context, int mouseX, int mouseY, float delta, Matrix4f positionMatrix) {
        int opacity = alphaAnimation
                .getOutput()
                .intValue();

        rectangle.render(ShapeProperties.create(positionMatrix, dropdownListX, dropDownListY, dropDownListWidth, dropDownListHeight)
                .round(4)
                .thickness(1.0F)
                .outlineColor(MathUtil.applyOpacity(0xFF2D2E41, (float) opacity / 5))
                .color(MathUtil.applyOpacity(0xFF161825, opacity))
                .build()
        );

        int offset = (int) dropDownListY + 1;

        for (MultiSelectedButton button : multiSelectedButtons) {
            button.x = dropdownListX;
            button.y = offset;
            button.width = dropDownListWidth;
            button.height = 12;

            button.setAlpha(opacity);

            button.render(context, mouseX, mouseY, delta);
            offset += 12;
        }
    }

    private boolean isHoveredList(double mouseX, double mouseY) {
        return MathUtil.isHovered(mouseX, mouseY, dropdownListX, dropDownListY - 16, dropDownListWidth, dropDownListHeight + 16);
    }
}

