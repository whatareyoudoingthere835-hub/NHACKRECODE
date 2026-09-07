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

public class IconLabelBadge extends AbstractWidget {
    public final MsdfFont font = Fonts.INTER_EXTRA_BOLD.get();
    static final int textSize = 9;
    static final float padding = 6.0f;
    public final Translation label;
    public final GlTextureObject icon;
    public float width;
    public Integer baseColor;
    public Integer textColor;

    public IconLabelBadge(GlTextureObject class073Var, Translation class254Var) {
        this.label = class254Var;
        this.icon = class073Var;
    }

    @Override
    public void render(DrawCtx class699Var) {
        StylePalette class764VarPalette= class699Var.theme().palette();
        PaletteColorStack class115VarColorStack= class699Var.colorStack();
        int iIntValue= this.baseColor != null ? this.baseColor.intValue() : class764VarPalette.accent().argb();
        int iIntValue2= this.textColor != null ? this.textColor.intValue() : iIntValue;
        float textH= this.font.getHeight(9.0f);
        float badgeH= height();
        float centerY= y() + (badgeH / 2.0f);
        class699Var.fillRoundedRect(x(), y(), width(), badgeH, 5.0f, class115VarColorStack.computeColor(iIntValue, 0.18f));
        class699Var.textureVerticalC(this.icon, x() + padding, centerY, 10, 10, class115VarColorStack.computeColor(iIntValue2));
        class699Var.text(this.font, this.label.effective().toUpperCase(), textSize, x() + padding + 10.0f + 4.0f, centerY - (textH / 2.0f) - 0.5f, class115VarColorStack.computeColor(iIntValue2));
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        this.width = 26.0f + class698Var.textWidthPhysical(this.font, this.label.effective().toUpperCase(), textSize);
    }

    @Override
    public float width() {
        return this.width;
    }

    @Override
    public float height() {
        return 19.0f;
    }

    public void setBaseColor(Integer num) {
        this.baseColor = num;
    }

    public void setTextColor(Integer num) {
        this.textColor = num;
    }
}
