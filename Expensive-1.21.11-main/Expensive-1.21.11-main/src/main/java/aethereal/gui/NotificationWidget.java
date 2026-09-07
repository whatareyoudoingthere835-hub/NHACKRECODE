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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class NotificationWidget extends Draggable {
    public float width;
    public float height;
    public final GlTextureObject backgroundTexture;
    public static final float rowHeight = 27.0f;
    public static final int horizontalPadding = 8;
    public static final int iconSize = 12;
    public static final int verticalPadding = 8;
    public Notification previewNotification;
    public final AnimatedFloat previewAnimation;
    public static final Translation placeholderText = Lang.NOTIFICATIONS_PLACEHOLDER;
    public int previewTypeIndex;
    public final Stopwatch previewCycleStopwatch;
    public final List<ModuleStateEvent> pendingModuleEvents;
    public final Stopwatch moduleEventStopwatch;
    public final MsdfFont font;

    public final ModeSetting<NotificationDirection> direction;

    public final BooleanSetting itemPickUp;

    public NotificationWidget(BooleanSupplier booleanSupplier) {
        super("Notifications", booleanSupplier);
        this.backgroundTexture = new GlTextureObject(new ClasspathResource("/textures/hud_background.png"));
        this.previewAnimation = new AnimatedFloat(200, Easings.LINEAR);
        this.previewTypeIndex = 0;
        this.previewCycleStopwatch = new Stopwatch();
        this.pendingModuleEvents = new ArrayList();
        this.moduleEventStopwatch = new Stopwatch();
        this.font = Fonts.INTER_BOLD.get();
        this.direction = new ModeSetting(Lang.WIDGET_NOTIFICATIONS_DIRECTION, Lang.WIDGET_NOTIFICATIONS_DIRECTION_DESC).values(NotificationDirection.class);
        this.itemPickUp = new BooleanSetting(Lang.WIDGET_NOTIFICATIONS_ITEM_PICK_UP, Lang.WIDGET_NOTIFICATIONS_ITEM_PICK_UP_DESC);
        setExcludeFromSnapGrid(true);
        addSettings(this.direction, this.itemPickUp);
        Expensive.INSTANCE.eventDispatcher().register(ModuleStateEvent.class, class080Var -> {
            if (isVisible() && Mc.INSTANCE.isWorldLoaded()) {
                synchronized (this.pendingModuleEvents) {
                    this.pendingModuleEvents.add(class080Var);
                    this.moduleEventStopwatch.reset();
                }
            }
        });
        this.y = 50.0f;
    }

    @Override
    public void layout(DragRenderContext class809Var) {
    }

    @Override
    public void render(DragRenderContext class809Var) throws MatchException {
        boolean z;
        if (isVisible()) {
            MatrixStack matrixStack= class809Var.matrixStack();
            GraphicsDrawEngine class154VarDrawEngine= class809Var.drawEngine();
            PaletteColorStack class115VarColorStack= class154VarDrawEngine.colorStack();
            float fScreenHeight= class809Var.resolution().screenHeight() / 2.0f;
            switch (((NotificationDirection) this.direction.currentValue()).ordinal()) {
                case 0:
                    z = this.y + (this.height / 2.0f) > fScreenHeight;
                    break;
                case 1:
                    z = true;
                    break;
                case 2:
                    z = false;
                    break;
                default:
                    throw new MatchException((String) null, (Throwable) null);
            }
            boolean z2= z;
            float f= 0.0f;
            float fMax= 0.0f;
            List<Notification> notifications= Expensive.INSTANCE.notificationRepository().getNotifications();
            if (this.previewNotification != null) {
                float fAnimatedValue= this.previewNotification.valueAnimation().animatedValue();
                if (fAnimatedValue > 0.0f) {
                    float widthWithStyles= 36.0f + this.font.getWidthWithStyles(this.previewNotification.message(), 12.0f) + 4.0f;
                    this.x = (class809Var.resolution().screenWidth() / 2.0f) - (widthWithStyles / 2.0f);
                    renderNotification(class809Var.drawEngine(), class809Var.theme().palette(), class809Var.matrixStack(), class809Var.drawEngine().colorStack(), this.previewNotification, this.x, this.y, widthWithStyles, rowHeight, fAnimatedValue);
                    this.width = widthWithStyles;
                    this.height = rowHeight;
                    return;
                }
            }
            for (int i = 0; i < notifications.size(); i++) {
                Notification class657Var= z2 ? notifications.get((notifications.size() - 1) - i) : notifications.get(i);
                float fAnimatedValue2= class657Var.valueAnimation().animatedValue();
                if (fAnimatedValue2 > 0.0f) {
                    float widthWithStyles2= 28.0f + this.font.getWidthWithStyles(class657Var.message(), 12.0f) + 8.0f + 2.0f;
                    this.x = (class809Var.resolution().screenWidth() / 2.0f) - (widthWithStyles2 / 2.0f);
                    fMax = Math.max(fMax, widthWithStyles2);
                    renderNotification(class154VarDrawEngine, class809Var.theme().palette(), matrixStack, class115VarColorStack, class657Var, this.x, z2 ? this.y - f : this.y + f, widthWithStyles2, rowHeight, fAnimatedValue2);
                    f += 30.0f * fAnimatedValue2;
                }
            }
            this.width = fMax;
            this.height = rowHeight;
        }
    }

    public void renderNotification(GraphicsDrawEngine class154Var, StylePalette class764Var, MatrixStack matrixStack, PaletteColorStack class115Var, Notification class657Var, float f, float f2, float f3, float f4, float f5) {
        int iComputeColor= class115Var.computeColor(class764Var.surfaceBackground().tone(801).argb(), f5);
        int iComputeColor2= class115Var.computeColor(class764Var.surfaceBackground().tone(900).argb(), f5);
        int blurAttachment= FrameBufferUtils.getColorAttachmentId(Expensive.INSTANCE.windowController().headerBlur().getBlurFramebuffer());
        if (blurAttachment > 0) {
            class154Var.roundedBlur(matrixStack.peek().getPositionMatrix(), f, f2, f3, f4, 8.0f, class115Var.white(), blurAttachment);
        }
        class154Var.roundedRectangle(matrixStack.peek().getPositionMatrix(), f, f2, f3, f4, 8.0f, 2.5f, class115Var.computeColor(class764Var.surfaceOutline().tone(600).argb(), f5), iComputeColor2, iComputeColor2, iComputeColor, iComputeColor);
        class154Var.texture(matrixStack.peek().getPositionMatrix(), f, f2, f3, f4, class154Var.bindTexture(this.backgroundTexture.textureWithSTB()), class115Var.computeColor(f5, StencilBufferUtil.STENCIL_MASK, StencilBufferUtil.STENCIL_MASK, StencilBufferUtil.STENCIL_MASK));
        float f6= f + 8.0f;
        if (class657Var.itemStack() == null || class657Var.itemStack().isEmpty()) {
            class154Var.texture(matrixStack.peek().getPositionMatrix(), f6, (f2 + (f4 / 2.0f)) - 6.0f, 12.0f, 12.0f, class154Var.bindTexture(class657Var.type().getTexture().textureWithSTB()), resolve(class657Var.type(), class764Var, class115Var, f5));
        } else {
            class154Var.itemStack(matrixStack.peek().getPositionMatrix(), class657Var.itemStack(), f6, (f2 + (f4 / 2.0f)) - 6.0f, 0.375f, f5);
        }
        class154Var.msdfText(matrixStack.peek().getPositionMatrix(), this.font, class657Var.message(), f6 + 20.0f, (f2 + (f4 / 2.0f)) - (this.font.getHeight(12.0f) / 2.0f), 12.0f, class115Var.computeColor(class764Var.text().tone(400).argb(), f5));
    }

    @Override
    public void animate(WeightedEngine class141Var) {
        if (isVisible()) {
            Expensive.INSTANCE.notificationRepository().animate(class141Var);
            this.previewAnimation.animate(class141Var);
        }
    }

    @Override
    public void update() {
        if (isVisible()) {
            synchronized (this.pendingModuleEvents) {
                if (!this.pendingModuleEvents.isEmpty()) {
                    ArrayList<ModuleStateEvent> arrayList= new ArrayList(this.pendingModuleEvents);
                    this.pendingModuleEvents.clear();
                    TreeSet treeSet= new TreeSet(String.CASE_INSENSITIVE_ORDER);
                    TreeSet treeSet2= new TreeSet(String.CASE_INSENSITIVE_ORDER);
                    for (ModuleStateEvent class080Var : arrayList) {
                        String name= class080Var.module().getName();
                        if (class080Var.moduleState()) {
                            treeSet.add(name);
                        } else {
                            treeSet2.add(name);
                        }
                    }
                    if (!treeSet.isEmpty()) {
                        List list= treeSet.stream().limit(4).toList();
                        Expensive.INSTANCE.notificationRepository().post(NotificationType.MODULE_ENABLED, (Text) Text.literal(list.size() == 1 ? Lang.NOTIFICATIONS_ENABLED_SINGLE.effective().replace("{module}", (CharSequence) list.getFirst()) : Lang.NOTIFICATIONS_ENABLED_MULTIPLE.effective().replace("{modules}", String.join(", ", list)).replace("{extra}", treeSet.size() > 4 ? Lang.NOTIFICATIONS_MORE.effective().replace("{count}", String.valueOf(treeSet.size() - 4)) : "")), 3L, TimeUnit.SECONDS);
                        treeSet.clear();
                    }
                    if (!treeSet2.isEmpty()) {
                        List list2= treeSet2.stream().limit(4).toList();
                        Expensive.INSTANCE.notificationRepository().post(NotificationType.MODULE_DISABLED, (Text) Text.literal(list2.size() == 1 ? Lang.NOTIFICATIONS_DISABLED_SINGLE.effective().replace("{module}", (CharSequence) list2.getFirst()) : Lang.NOTIFICATIONS_DISABLED_MULTIPLE.effective().replace("{modules}", String.join(", ", list2)).replace("{extra}", treeSet2.size() > 4 ? Lang.NOTIFICATIONS_MORE.effective().replace("{count}", String.valueOf(treeSet2.size() - 4)) : "")), 3L, TimeUnit.SECONDS);
                        treeSet2.clear();
                    }
                }
            }
            Expensive.INSTANCE.notificationRepository().tick();
            boolean z= Mc.INSTANCE.getCurrentScreen() instanceof ChatScreen;
            boolean z2= !Expensive.INSTANCE.notificationRepository().getNotifications().isEmpty();
            if (!z || z2) {
                if (this.previewNotification != null) {
                    this.previewAnimation.destination(0.0f);
                    if (this.previewAnimation.animatedValue() <= 0.01f) {
                        this.previewNotification = null;
                        return;
                    }
                    return;
                }
                return;
            }
            long jCurrentTimeMillis= System.currentTimeMillis();
            if (this.previewNotification == null) {
                this.previewAnimation.destination(1.0f);
                this.previewNotification = new Notification(this.previewAnimation, NotificationType.INFO, Text.literal(placeholderText.effective()), jCurrentTimeMillis, Long.MAX_VALUE, null);
                this.previewCycleStopwatch.reset();
                return;
            }
            this.previewAnimation.destination(1.0f);
            if (this.previewCycleStopwatch.hasElapsed(700L, TimeUnit.MILLISECONDS)) {
                NotificationType[] class659VarArrValues= NotificationType.values();
                this.previewTypeIndex = (this.previewTypeIndex + 1) % class659VarArrValues.length;
                this.previewNotification = new Notification(this.previewAnimation, class659VarArrValues[this.previewTypeIndex], Text.literal(placeholderText.effective()), jCurrentTimeMillis, Long.MAX_VALUE, null);
                this.previewCycleStopwatch.reset();
            }
        }
    }

    public int resolve(NotificationType class659Var, StylePalette class764Var, PaletteColorStack class115Var, float f) throws MatchException {
        switch (NotificationTypeSwitchMap.ordinalToCase[class659Var.ordinal()]) {
            case 1:
                return class115Var.computeColor(class764Var.accent().argb(), f);
            case 2:
            case 3:
                return class115Var.computeColor(StylePalette.deepGreen.argb(), f);
            case 4:
                return class115Var.computeColor(StylePalette.darkGolden.argb(), f);
            case 5:
            case 6:
                return class115Var.computeColor(StylePalette.darkRed.argb(), f);
            case 7:
                return class115Var.computeColor(class764Var.text().tone(50).argb(), f);
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    @Override
    public float width() {
        return this.width;
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public boolean click(MouseButtonInput2 class807Var, boolean z) {
        return false;
    }

    @Override
    public boolean cursor(MouseMoveInput class808Var, boolean z) {
        return z && !class808Var.intercepted();
    }

    public BooleanSetting getItemPickUp() {
        return this.itemPickUp;
    }
}
