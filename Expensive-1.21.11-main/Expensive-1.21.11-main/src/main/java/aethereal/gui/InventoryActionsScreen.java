package aethereal.gui;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class InventoryActionsScreen extends InventoryScreen {
    public VanillaButton dropAllButton;

    public InventoryActionsScreen(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    public void init() {
        super.init();
        this.dropAllButton = new VanillaButton((this.x + (this.backgroundWidth / 2)) - (90 / 2), (this.y - 20) - 10, 90, 20, Text.of("Выбросить все"), buttonWidget -> {
            dropAll();
        });
        addDrawableChild(this.dropAllButton);
    }

    public void updateButton() {
        if (this.dropAllButton != null) {
            this.dropAllButton.setPosition((this.x + (this.backgroundWidth / 2)) - (90 / 2), (this.y - 20) - 10);
            this.dropAllButton.active = getScreenHandler().slots.stream().anyMatch(slot -> {
                return !slot.getStack().isEmpty();
            });
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        updateButton();
    }

    public void dropAll() {
        getScreenHandler().slots.stream().filter(slot -> {
            return !slot.getStack().isEmpty();
        }).forEach(slot2 -> {
            PlayerActionUtil.INSTANCE.windowClick(SlotActionType.THROW, getScreenHandler().slots.indexOf(slot2), 1);
        });
    }
}
