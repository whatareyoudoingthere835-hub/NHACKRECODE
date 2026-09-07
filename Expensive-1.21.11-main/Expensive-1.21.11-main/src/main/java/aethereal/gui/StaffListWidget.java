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

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.StringHelper;

public class StaffListWidget extends Draggable {
    public final MsdfFont nameFont;
    public final MsdfFont labelFont;
    static final GlTextureObject staffIcon = loadTexture("/icons/menu/new/staff.png");
    static final GlTextureObject wandIcon = loadTexture("/icons/menu/new/wand_sparkle.png");
    static final GlTextureObject eyeIcon = loadTexture("/icons/menu/new/eyeopen.png");
    static final GlTextureObject playIcon = loadTexture("/icons/menu/new/dottedPlay.png");
    public final GlTextureObject starsTexture;
    public final AnimatedFloat visibilityAnimation;
    public final WidgetBounds headerBounds;

    public float width;

    public float height;
    public float prefixWidth;
    static final float maxPrefixWidth = 50.0f;

    static GlTextureObject loadTexture(String str) {
        return new GlTextureObject(new ClasspathResource(str));
    }

    public StaffListWidget(BooleanSupplier booleanSupplier) {
        super("StaffList", booleanSupplier);
        this.nameFont = Fonts.INTER_SEMIBOLD.get();
        this.labelFont = Fonts.INTER_EXTRA_BOLD.get();
        this.starsTexture = new GlTextureObject(new ClasspathResource("/textures/stars.png"));
        this.visibilityAnimation = new AnimatedFloat(250, Easings.LINEAR);
        this.headerBounds = new WidgetBounds(0.0f, 0.0f, 0.0f, 19.0f);
        this.width = 220.0f;
        this.height = 39.0f;
        this.prefixWidth = 0.0f;
        this.x = 10.0f;
        this.y = 49.0f;
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

    public String cleanText(String str) {
        return str == null ? "" : StringHelper.stripTextFormat(str.replace("âš¡", "").replace("â—", "")).trim();
    }

    public String truncateText(MsdfFont class161Var, String str, float f, float f2) {
        if (str == null) {
            return "";
        }
        if (f2 <= 0.0f) {
            return "...";
        }
        if (class161Var.getWidth(str, f) <= f2) {
            return str;
        }
        float width= f2 - class161Var.getWidth("...", f);
        if (width <= 0.0f) {
            return "...";
        }
        int i= 0;
        int length= str.length();
        while (i < length) {
            int i2= ((i + length) + 1) >>> 1;
            if (class161Var.getWidth(str.substring(0, i2), f) <= width) {
                i = i2;
            } else {
                length = i2 - 1;
            }
        }
        return str.substring(0, i) + "...";
    }

    @Override
    public void layout(DragRenderContext class809Var) {
        if (isVisible()) {
            float fMax= 150.0f;
            float fMin= 39.0f;
            float fMax2= 0.0f;
            List<StaffPlayerEntry> listSortedStaff= StaffDetector.sortedStaff();
            float fMax3= 0.0f;
            for (StaffPlayerEntry class118Var : listSortedStaff) {
                fMax3 = Math.max(fMax3, 10.0f + this.labelFont.getWidth(cleanText(class118Var.prefix()).isEmpty() ? "ÐŸÐ£Ð¡Ð¢Ðž".toUpperCase() : cleanText(class118Var.prefix()).toUpperCase(), 9.0f));
            }
            this.prefixWidth = Math.min(maxPrefixWidth, fMax3);
            int iOrElse= IntStream.range(0, listSortedStaff.size()).filter(i -> {
                return !((StaffPlayerEntry) listSortedStaff.get(i)).stateAnimation().isZero();
            }).reduce((i2, i3) -> {
                return i3;
            }).orElse(-1);
            int i4= 0;
            for (StaffPlayerEntry class118Var2 : listSortedStaff) {
                float fSmoothAnimation= class118Var2.stateAnimation().smoothAnimation();
                String strName= class118Var2.name();
                String status= class118Var2.status().getStatus();
                fMax = Math.max(fMax, 150.0f + (((((20.0f + ((((this.prefixWidth + 5.0f) + 12.0f) + 4.0f) + this.nameFont.getWidth(strName, 12.0f))) + 10.0f) + (((10.0f + 11.0f) + 4.0f) + this.labelFont.getWidth(status, 9.0f))) - 150.0f) * fSmoothAnimation));
                fMin += Math.min(17.0f, 17.0f * fSmoothAnimation) + (i4 != iOrElse ? 6.0f * fSmoothAnimation : 0.0f);
                fMax2 = Math.max(fMax2, fSmoothAnimation);
                i4++;
            }
            if (fMax2 > 0.0f) {
                fMin += 10.0f * fMax2;
            }
            this.width = fMax;
            this.height = fMin;
            this.headerBounds.withSize(fMax - 20.0f, 19.0f).withPosition(this.x + 10.0f, (this.y + 19.5f) - 9.5f);
        }
    }

    @Override
    public void render(DragRenderContext class809Var) {
        if (!isVisible() || this.visibilityAnimation.isZero()) {
            return;
        }
        GraphicsDrawEngine class154VarDrawEngine= class809Var.drawEngine();
        MatrixStack matrixStack= class809Var.matrixStack();
        PaletteColorStack class115VarColorStack= class154VarDrawEngine.colorStack();
        StylePalette class764VarPalette= class809Var.theme().palette();
        float fAnimatedValue= this.visibilityAnimation.animatedValue();
        class115VarColorStack.push();
        class115VarColorStack.alpha(fAnimatedValue);
        int iComputeColor= class115VarColorStack.computeColor(class764VarPalette.surfaceBackground().tone(801).argb());
        int iComputeColor2= class115VarColorStack.computeColor(class764VarPalette.surfaceBackground().tone(900).argb());
        int blurAttachment= FrameBufferUtils.getColorAttachmentId(Expensive.INSTANCE.windowController().headerBlur().getBlurFramebuffer());
        if (blurAttachment > 0) {
            class154VarDrawEngine.roundedBlur(matrixStack.peek().getPositionMatrix(), this.x, this.y, this.width, this.height, 8.0f, class115VarColorStack.white(), blurAttachment);
        }
        class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), this.x, this.y, this.width, this.height, 8.0f, 2.5f, class115VarColorStack.computeColor(class764VarPalette.surfaceOutline().tone(600).argb()), iComputeColor2, iComputeColor2, iComputeColor, iComputeColor);
        class154VarDrawEngine.texture(matrixStack.peek().getPositionMatrix(), this.x, this.y, this.width, this.height, class154VarDrawEngine.bindTexture(this.starsTexture.textureWithSTB()), class115VarColorStack.white());
        class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.nameFont, "Staff List", this.headerBounds.x(), (this.headerBounds.y() + 9.5f) - (this.nameFont.getHeight(13.0f) / 2.0f), 13.0f, 0.0f, class115VarColorStack.computeColor(class764VarPalette.text().tone(200).argb()));
        class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), this.headerBounds.right() - 19.0f, this.headerBounds.y(), 19.0f, 19.0f, 6.0f, class115VarColorStack.computeColor(class764VarPalette.accentBright().argb(), 0.1f));
        class154VarDrawEngine.textureVerticalCHorizontalC(matrixStack.peek().getPositionMatrix(), staffIcon, this.headerBounds.right() - 9.5f, this.headerBounds.y() + 9.5f, 11, 11, class115VarColorStack.computeColor(class764VarPalette.accent().argb()));
        float fBottom= this.headerBounds.bottom() + 12.0f;
        for (StaffPlayerEntry class118Var : StaffDetector.sortedStaff()) {
            float fSmoothAnimation= class118Var.stateAnimation().smoothAnimation();
            String upperCase= cleanText(class118Var.prefix()).isEmpty() ? "ÐŸÐ£Ð¡Ð¢Ðž".toUpperCase() : cleanText(class118Var.prefix()).toUpperCase();
            String strName= class118Var.name();
            String status= class118Var.status().getStatus();
            float width= this.labelFont.getWidth(status, 9.0f);
            float f= this.prefixWidth;
            String strMethod004= truncateText(this.labelFont, upperCase, 9.0f, Math.max(0.0f, f - 10.0f));
            float width2= this.labelFont.getWidth(strMethod004, 9.0f);
            float f2= 10 + 11 + 4.0f + width;
            float f3= this.x + 10.0f;
            class115VarColorStack.push();
            class115VarColorStack.alpha(fSmoothAnimation);
            class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), f3, fBottom, f, 17.0f, 4.0f, class115VarColorStack.computeColor(class764VarPalette.accent().argb(), 0.1f));
            class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.labelFont, strMethod004, f3 + ((f - width2) / 2.0f), (fBottom + (17.0f / 2.0f)) - (this.labelFont.getHeight(9.0f) / 2.0f), 9.0f, 0.0f, class115VarColorStack.computeColor(class764VarPalette.accent().argb()));
            float f4= f3 + f + 5.0f;
            class154VarDrawEngine.roundedTexture(matrixStack.peek().getPositionMatrix(), f4, (fBottom + (17.0f / 2.0f)) - (12 / 2.0f), 12, 12, 4.0f, 4.0f, 4.0f, 4.0f, 0.125f, 0.125f, 0.25f, 0.25f, class154VarDrawEngine.bindTexture(FrameBufferUtils.getTextureId(class118Var.headTexture())), class115VarColorStack.white(), class115VarColorStack.white(), class115VarColorStack.white(), class115VarColorStack.white());
            class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.nameFont, strName, f4 + 12 + 4.0f, (fBottom + (17.0f / 2.0f)) - (this.nameFont.getHeight(12.0f) / 2.0f), 12.0f, 0.0f, class115VarColorStack.computeColor(class764VarPalette.text().tone(400).argb()));
            float fRight= this.headerBounds.right() - f2;
            class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), fRight, fBottom, f2, 17.0f, 6.0f, class115VarColorStack.computeColor(class118Var.status().getColor(), 0.1f));
            class154VarDrawEngine.textureVerticalC(matrixStack.peek().getPositionMatrix(), getStatusIcon(class118Var), fRight + 5.0f, fBottom + (17.0f / 2.0f), 11, 11, class115VarColorStack.computeColor(class118Var.status().getColor()));
            class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.labelFont, status, fRight + 5.0f + 11 + 4.0f, (fBottom + (17.0f / 2.0f)) - (this.labelFont.getHeight(9.0f) / 2.0f), 9.0f, 0.0f, class115VarColorStack.computeColor(class118Var.status().getColor()));
            class115VarColorStack.pop();
            fBottom += (17.0f + 6.0f) * fSmoothAnimation;
        }
        class115VarColorStack.pop();
    }

    public GlTextureObject getStatusIcon(StaffPlayerEntry class118Var) {
        switch (StaffStatusSwitchMap.statusSwitchMap[class118Var.status().ordinal()]) {
            case 1:
                return eyeIcon;
            case 2:
                return playIcon;
            case 3:
                return wandIcon;
            default:
                throw new IllegalStateException("Unknown status: " + String.valueOf(class118Var.status()));
        }
    }

    @Override
    public void animate(WeightedEngine class141Var) {
        if (Mc.INSTANCE.getPlayer() == null) {
            return;
        }
        StaffDetector.staffPlayers.forEach(class118Var -> {
            class118Var.stateAnimation().animate(class141Var);
        });
        this.visibilityAnimation.animate(class141Var);
    }

    @Override
    public void update() {
        if (isVisible()) {
            this.visibilityAnimation.destination((!StaffDetector.staffPlayers.isEmpty() || (Mc.INSTANCE.getCurrentScreen() instanceof ChatScreen)) ? 1.0f : 0.0f);
        }
    }
}
