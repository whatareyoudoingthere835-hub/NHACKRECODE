package aethereal.features.modules.render;
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


@Aliases(aliases = {"HUD", "Widgets", "Overlay", "Target Hud", "Water Mark", "Potion List", "Staff List", "Coords", "Armor Status", "Armor Hud", "Hot keys", "Key binds", "Music Player", "Music", "Media Player"})
public class WidgetsModule extends Module {
    public final MultiSelectSetting<HudWidgetType> elements;
    public final NotificationWidget notificationWidget;

    public WidgetsModule() {
        super(ModuleTab.RENDER, "Widgets");
        setStateSilent(true);
        this.elements = new MultiSelectSetting(Lang.WIDGETS_ELEMENTS, Lang.WIDGETS_ELEMENTS_DESC).values(HudWidgetType.class).select(HudWidgetType.values());
        addSettings(this.elements);
        WidgetStack class814VarWidgetStack= Expensive.INSTANCE.widgetStack();
        class814VarWidgetStack.newWidget(new WatermarkWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.WATERMARK);
        }));
        class814VarWidgetStack.newWidget(new PotionListWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.POTION_LIST);
        }));
        class814VarWidgetStack.newWidget(new HotkeysWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.HOTKEYS);
        }));
        class814VarWidgetStack.newWidget(new TargetHudWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.TARGET_HUD);
        }));
        class814VarWidgetStack.newWidget(new CoordsWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.COORDS);
        }));
        class814VarWidgetStack.newWidget(new ItemBindWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.ITEM_BIND);
        }));
        class814VarWidgetStack.newWidget(new StaffListWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.STAFF_LIST);
        }));
        NotificationWidget class644Var= new NotificationWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.NOTIFICATION);
        });
        this.notificationWidget = class644Var;
        class814VarWidgetStack.newWidget(class644Var);
        class814VarWidgetStack.newWidget(new TrapTimerWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.TRAP_TIMER);
        }));
        class814VarWidgetStack.newWidget(new MusicPlayerWidget(() -> {
            return isState() && this.elements.isSelected(HudWidgetType.MEDIA_PLAYER);
        }));
        register(Render2DEvent.class, class311Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded() && class311Var.isPre() && class814VarWidgetStack.widgets().stream().anyMatch((v0) -> {
                return v0.isVisible();
            })) {
                Expensive.INSTANCE.windowController().headerBlur().apply(32);
                FrameBufferUtils.bindForRendering(net.minecraft.client.MinecraftClient.getInstance().getFramebuffer());
                class814VarWidgetStack.draw();
            }
        });
        register(ClientTickEvent.class, class181Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                class814VarWidgetStack.update();
            }
        });
        register(WindowResizeEvent.class, class061Var -> {
            class814VarWidgetStack.handleResize(class061Var.width(), class061Var.height());
        });
        register(MouseButtonEvent2.class, class300Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                class814VarWidgetStack.click(class300Var);
            }
        });
        register(StatusEffectOverlayEvent.class, class315Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded() && this.elements.isSelected(HudWidgetType.POTION_LIST)) {
                class315Var.cancel();
            }
        });
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    public MultiSelectSetting<HudWidgetType> getElements() {
        return this.elements;
    }

    public NotificationWidget getNotificationWidget() {
        return this.notificationWidget;
    }
}
