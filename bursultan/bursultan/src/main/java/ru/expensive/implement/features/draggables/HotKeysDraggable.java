package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.core.Extra;
import ru.expensive.implement.features.modules.render.InterfaceModule;

import java.util.List;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class HotKeysDraggable extends AbstractDraggable {
    private List<Module> key;
    private float animatedHeight = 16;
    private long lastUpdateTime;
    private static final float ANIMATION_SPEED = 0.02f;

    public HotKeysDraggable() {
        super("HotKeys", 420, 10, 80, 16);
        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public boolean visible() {
        InterfaceModule interfaceModule = (InterfaceModule) Extra.getInstance().getModuleProvider().module("Interface");
        boolean isEmpty = key == null || key.isEmpty();
        return interfaceModule != null
                && interfaceModule.isState()
                && interfaceModule.getInterfaceSettings().isSelected("HotKeys")
                && (interfaceModule.getShowEmpty().isValue() || !isEmpty || mc.currentScreen instanceof ChatScreen);
    }

    @Override
    public void tick(float delta) {
        key = Extra.getInstance().getModuleProvider()
                .getModules()
                .stream()
                .filter(module -> module.isState() && module.getKey() != -1 && module.getCategory() != ModuleCategory.RENDER)
                .toList();

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        float targetHeight = key.isEmpty() ? 16 : 20 + key.size() * 10;
        animatedHeight += (targetHeight - animatedHeight) * (ANIMATION_SPEED * deltaTime);
        setHeight(Math.round(animatedHeight));

        float maxEntryWidth = 80;
        for (Module module : key) {
            String keyName = "[" + StringUtil.getBindName(module.getKey()) + "]";
            float entryWidth = Fonts.getSize(11).getStringWidth(module.getName()) + Fonts.getSize(11).getStringWidth(keyName) + 24;
            if (entryWidth > maxEntryWidth) {
                maxEntryWidth = entryWidth;
            }
        }
        setWidth(Math.round(maxEntryWidth));

        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = ru.expensive.common.util.math.MathUtil.getPositionMatrix(context);

        InterfaceModule interfaceModule = (InterfaceModule) Extra.getInstance().getModuleProvider().module("Interface");
        float radius = interfaceModule.getCornerRadius().getValue();

        if (key.isEmpty()) {
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

            rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), 16)
                    .round(radius)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(0xFF2D2E41)
                    .color(0xF2181A2A)
                    .build()
            );
        }

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());

        image.setTexture("textures/keyboard.png").render(ShapeProperties.create(positionMatrix, getX() + getWidth() - 16, getY() + 5, 8, 8)
                .build()
        );

        Fonts.getSize(13, BOLD).drawString(context.getMatrices(), getName(), getX() + 8, getY() + 7, 0xFFD4D6E1);

        int offset = getY() + 21;
        for (Module module : key) {
            Fonts.getSize(11).drawString(context.getMatrices(), module.getName(), getX() + 8, offset, 0xFFD4D6E1);

            String keyName = "[" + StringUtil.getBindName(module.getKey()) + "]";
            float keyWidth = Fonts.getSize(11).getStringWidth(keyName);
            Fonts.getSize(11).drawString(
                    context.getMatrices(),
                    keyName,
                    getX() + getWidth() - 8 - keyWidth,
                    offset,
                    0xFFFFFFFF
            );

            offset += 10;
        }
    }
}