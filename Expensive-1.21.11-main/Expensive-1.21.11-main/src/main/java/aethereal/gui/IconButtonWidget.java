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

public class IconButtonWidget extends AbstractWidget {
    public final GlTextureObject icon;
    public final float iconWidth;
    public final float iconHeight;
    public final ClickableBehavior clickable = new ClickableBehavior();
    public Translation tooltipText = null;
    public GlTextureObject tooltipIcon = null;
    public boolean tooltipShown = false;
    public int color = -1;

    public IconButtonWidget(GlTextureObject class073Var, Runnable runnable, float f, float f2) {
        this.clickable.clickCallback(() -> {
            hideTooltip();
            if (runnable != null) {
                runnable.run();
            }
        });
        this.icon = class073Var;
        this.iconWidth = f;
        this.iconHeight = f2;
    }

    public IconButtonWidget tooltip(Translation class254Var, GlTextureObject class073Var) {
        this.tooltipText = class254Var;
        this.tooltipIcon = class073Var;
        return this;
    }

    @Override
    public void render(DrawCtx class699Var) {
        GraphicsDrawEngine class154VarDrawEngine= class699Var.drawEngine();
        StylePalette class764VarPalette= class699Var.theme().palette();
        PaletteColorStack class115VarColorStack= class154VarDrawEngine.colorStack();
        if (this.color == -1) {
            this.color = class764VarPalette.text().tone(500).argb();
        }
        int iComputeColor= class115VarColorStack.computeColor(this.color);
        class699Var.texture(this.icon, x(), y(), this.iconWidth, this.iconHeight, class115VarColorStack.interpolate(iComputeColor, class115VarColorStack.brighten(iComputeColor, 0.8f), this.clickable.hoverAnimation()));
        
        TooltipService class736Var= Expensive.INSTANCE.menuWindow().tooltipService();
        if (class736Var != null && this.tooltipShown) {
            if (!this.clickable.hoverAnimation().state()) {
                hideTooltip();
            } else {
                class736Var.updateAnchor(this, bounds(), class699Var);
            }
        }
    }

    public void hideTooltip() {
        this.tooltipShown = false;
        TooltipService class736Var= Expensive.INSTANCE.menuWindow().tooltipService();
        if (class736Var != null) {
            class736Var.hide(this);
        }
    }

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) {
        boolean zHandleInput= this.clickable.handleInput(class688Var, z);
        boolean zState= this.clickable.hoverAnimation().state() && !z;
        TooltipService class736Var= Expensive.INSTANCE.menuWindow().tooltipService();
        if (class736Var != null && this.tooltipText != null) {
            if (zState && !this.tooltipShown) {
                this.tooltipShown = true;
                class736Var.show(this, bounds(), this.tooltipText, this.tooltipIcon);
            } else if (!zState && this.tooltipShown) {
                hideTooltip();
            }
        }
        return zHandleInput;
    }

    public WidgetBounds bounds() {
        return new WidgetBounds(x(), y(), width(), height());
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        setSize(this.iconWidth, this.iconHeight);
        this.clickable.setDimensions(x(), y(), width(), height());
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        this.clickable.animate(class141Var);
    }

    public void setColor(int i) {
        this.color = i;
    }
}
