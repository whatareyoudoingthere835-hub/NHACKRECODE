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

public class TooltipWidget extends AbstractWidget {
    static final float cornerRadius = 7.0f;
    static final float outlineThickness = 2.5f;
    static final float contentPadding = 6.0f;
    static final float spacing = 4.0f;
    static final float iconSize = 12.0f;
    static final float iconSpacing = 6.0f;
    public final MsdfFont font = Fonts.INTER_BOLD.get();
    public final ToggleAnimator visibilityAnimator = new ToggleAnimator(100, Easings.LINEAR);
    public final WidgetBounds anchorBounds = new WidgetBounds(0.0f, 0.0f, 0.0f, 0.0f);
    public final WidgetBounds tooltipBounds = new WidgetBounds(0.0f, 0.0f, 0.0f, 0.0f);
    public Translation text = Translation.clearText("Tooltip!");
    public GlTextureObject icon = new GlTextureObject(new ClasspathResource("/icons/menu/new/smile.png"));
    public boolean shown = false;

    public void show(Translation class254Var, GlTextureObject class073Var, WidgetBounds class678Var) {
        this.text = class254Var;
        this.icon = class073Var;
        this.anchorBounds.withPosition(class678Var.x(), class678Var.y()).withSize(class678Var.width(), class678Var.height());
        this.shown = true;
        this.visibilityAnimator.state(true);
    }

    public void hide() {
        this.shown = false;
        this.visibilityAnimator.state(false);
    }

    public boolean isVisible() {
        return this.shown || !this.visibilityAnimator.isZero();
    }

    @Override
    public void render(DrawCtx class699Var) {
        if (this.visibilityAnimator.isZero()) {
            return;
        }
        PaletteColorStack class115VarColorStack= class699Var.colorStack();
        StylePalette class764VarPalette= class699Var.theme().palette();
        float fSmoothAnimation= this.visibilityAnimator.smoothAnimation();
        class115VarColorStack.push();
        class115VarColorStack.alpha(fSmoothAnimation);
        class699Var.bloom(this.tooltipBounds.x(), this.tooltipBounds.y(), this.tooltipBounds.width(), this.tooltipBounds.height(), 20.0f, FrameBufferUtils.getColorAttachmentId(Expensive.INSTANCE.windowController().bloom().getBloomFramebuffer()));
        class699Var.fillOutlinedRoundedRect(this.tooltipBounds.x(), this.tooltipBounds.y(), this.tooltipBounds.width(), this.tooltipBounds.height(), cornerRadius, outlineThickness, class115VarColorStack.computeColor(class764VarPalette.surfaceOutline().tone(400).argb()), class115VarColorStack.computeColor(class764VarPalette.surfaceBackground().tone(600).argb()));
        float fX= this.tooltipBounds.x() + 6.0f;
        float fY= this.tooltipBounds.y() + (this.tooltipBounds.height() / 2.0f);
        if (this.icon != null) {
            class699Var.texture(this.icon, fX, fY - 6.0f, iconSize, iconSize, class115VarColorStack.computeColor(9934746));
            fX += 18.0f;
        }
        class699Var.text(this.font, this.text.effective(), 11, fX, fY - (this.font.getHeight(11.0f) / 2.0f), class115VarColorStack.computeColor(class764VarPalette.text().tone(500).argb()));
        class115VarColorStack.pop();
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        if (isVisible()) {
            float fTextWidthPhysical= class698Var.textWidthPhysical(this.font, this.text.effective(), 11);
            float height= this.font.getHeight(11.0f);
            boolean z= this.icon != null;
            float f= iconSize + fTextWidthPhysical + (z ? 18.0f : 0.0f);
            this.tooltipBounds.withSize(f, 8.0f + Math.max(height, z ? iconSize : 0.0f));
            this.tooltipBounds.withPosition((this.anchorBounds.x() + (this.anchorBounds.width() / 2.0f)) - (f / 2.0f), (this.anchorBounds.y() - this.tooltipBounds.height()) - 10.0f);
        }
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        this.visibilityAnimator.animate(class141Var);
        super.animation(class141Var);
    }

    public void updateAnchor(WidgetBounds class678Var) {
        this.anchorBounds.withPosition(class678Var.x(), class678Var.y()).withSize(class678Var.width(), class678Var.height());
    }

    @Override
    public void collectBloomElements(RenderCommandQueue class676Var) {
        class676Var.record(class699Var -> {
            if (this.visibilityAnimator.isZero()) {
                return;
            }
            PaletteColorStack class115VarColorStack= class699Var.drawEngine().colorStack();
            StylePalette class764VarPalette= class699Var.theme().palette();
            class115VarColorStack.push();
            class115VarColorStack.alpha(this.visibilityAnimator.smoothAnimation());
            class699Var.fillOutlinedRoundedRect(this.tooltipBounds.x(), this.tooltipBounds.y(), this.tooltipBounds.width(), this.tooltipBounds.height(), cornerRadius, outlineThickness, class115VarColorStack.computeColor(class764VarPalette.surfaceOutline().tone(400).argb()), class115VarColorStack.computeColor(class764VarPalette.surfaceBackground().tone(600).argb()));
            class115VarColorStack.pop();
        });
        super.collectBloomElements(class676Var);
    }
}
