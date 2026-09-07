package aethereal.core.models;
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class EnderChestScreenRedirect implements ClientListener {
    public EnderChestScreenRedirect() {
        Expensive.INSTANCE.eventDispatcher().register(ScreenOpenEvent.class, class135Var -> {
            EnderChestPlusModule class491Var= (EnderChestPlusModule) Expensive.INSTANCE.moduleRepository().get(EnderChestPlusModule.class);
            if (Mc.INSTANCE.isWorldLoaded()) {
                MinecraftClient minecraft= Mc.INSTANCE.getMinecraft();
                net.minecraft.client.gui.screen.Screen genericContainerScreenScreen = class135Var.screen();
                if ((genericContainerScreenScreen instanceof InventoryScreen) && class491Var.getScreen() == null) {
                    minecraft.setScreen(new InventoryActionsScreen(minecraft.player));
                    class135Var.cancel();
                }
                if (genericContainerScreenScreen instanceof GenericContainerScreen) {
                    GenericContainerScreen genericContainerScreen= (GenericContainerScreen) genericContainerScreenScreen;
                    minecraft.setScreen(new ContainerActionsScreen(genericContainerScreen.getScreenHandler(), minecraft.player.getInventory(), genericContainerScreen.getTitle()));
                    class135Var.cancel();
                }
            }
        });
    }
}
