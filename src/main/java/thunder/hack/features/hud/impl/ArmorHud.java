package thunder.hack.features.hud.impl;

import thunder.hack.utility.player.ItemChecks;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

public class ArmorHud extends HudElement {
    public ArmorHud() {
        super("ArmorHud", 60, 25);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.V2);

    private enum Mode {
        V1, V2
    }

    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        float xItemOffset = getPosX();
        for (ItemStack itemStack : mc.player.java.util.List.of(getInventory().getStack(39), getInventory().getStack(38), getInventory().getStack(37), getInventory().getStack(36))) {
            if (itemStack.isEmpty()) continue;

            if (mode.is(Mode.V1)) {
                context.drawItem(itemStack, (int) xItemOffset, (int) getPosY());
                context.drawStackOverlay(mc.textRenderer,itemStack,  (int) xItemOffset, (int) getPosY());
            } else {

                context.drawItem(itemStack, (int) xItemOffset, (int) getPosY());

                float offset = (ItemChecks.armorSlot(itemStack) == EquipmentSlot.HEAD) ? -4 : 0;
                Render2DEngine.addWindow(context.getMatrices(), (int) xItemOffset, getPosY() + offset + (15 - offset) * ((float) itemStack.getDamage() / (float) itemStack.getMaxDamage()), xItemOffset + 15, getPosY() + 15, 1f);
                context.drawItem(itemStack, (int) xItemOffset, (int) getPosY());
                Render2DEngine.popWindow();
            }
            xItemOffset += 20;
        }

        setBounds(getPosX(), getPosY(), 60, 25);
    }
}
