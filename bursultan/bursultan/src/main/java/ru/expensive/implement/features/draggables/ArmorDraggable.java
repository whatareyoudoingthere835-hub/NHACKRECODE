package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Extra;
import ru.expensive.implement.features.modules.render.InterfaceModule;

import java.util.List;

public class ArmorDraggable extends AbstractDraggable {
    private List<ItemStack> armor = List.of();
    private float animatedWidth = 15;
    private long lastUpdateTime;
    private static final float ANIMATION_SPEED = 0.02f;

    public ArmorDraggable() {
        super("Armor", 220, 10, 15, 16);
        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Extra.getInstance().getModuleProvider().module("Interface");
        boolean isEmpty = armor.stream().allMatch(ItemStack::isEmpty);
        return interfaceModule != null
                && interfaceModule.isState()
                && interfaceModule.getInterfaceSettings().isSelected("Armor Hud")
                && (interfaceModule.getShowEmpty().isValue() || !isEmpty || mc.currentScreen instanceof ChatScreen);
    }

    @Override
    public void tick(float delta) {
        assert mc.player != null;
        armor = List.of(
                mc.player.getEquippedStack(EquipmentSlot.HEAD),
                mc.player.getEquippedStack(EquipmentSlot.CHEST),
                mc.player.getEquippedStack(EquipmentSlot.LEGS),
                mc.player.getEquippedStack(EquipmentSlot.FEET));

        int itemCount = (int) armor.stream().filter(item -> !item.isEmpty()).count();
        float targetWidth = itemCount == 0 ? 17 : 17 + (itemCount * 13) + 3;

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        animatedWidth += (targetWidth - animatedWidth) * (ANIMATION_SPEED * deltaTime);
        setWidth(Math.round(animatedWidth));

        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = ru.expensive.common.util.math.MathUtil.getPositionMatrix(context);

        InterfaceModule interfaceModule = (InterfaceModule) Extra.getInstance().getModuleProvider().module("Interface");
        float radius = interfaceModule.getCornerRadius().getValue();
        int itemCount = (int) armor.stream().filter(item -> !item.isEmpty()).count();

        if (itemCount == 0) {
            rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                    .round(radius)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(0xFF2D2E41)
                    .color(0xF2181A2A)
                    .build()
            );
        } else {
            rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                    .round(radius)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(0xFF2D2E41)
                    .color(0xCC141724)
                    .build()
            );

            rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), 17, getHeight())
                    .round(radius)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(0xFF2D2E41)
                    .color(0xF2181A2A)
                    .build()
            );
        }

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/shield.png").render(ShapeProperties.create(positionMatrix, getX() + 5.5f, getY() + (double) getHeight() / 2 - 3, 6, 6)
                .build()
        );

        int offset = 17;

        for (int i = armor.size() - 1; i >= 0; i--) {
            ItemStack itemStack = armor.get(i);
            if (itemStack.isEmpty()) continue;

            context.drawItem(itemStack, getX() + offset, (int) (getY() + 2.5f));
            context.drawStackOverlay(mc.textRenderer, itemStack, getX() + offset, (int) (getY() + 2.5f));

            offset += 13;
        }
        
        setHeight(18);
    }
}