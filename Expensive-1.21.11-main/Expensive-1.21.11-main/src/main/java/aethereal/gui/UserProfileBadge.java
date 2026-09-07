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

public class UserProfileBadge extends AbstractWidget {
    public final MsdfFont font = Fonts.INTER_SEMIBOLD.get();

    @Override
    public void render(DrawCtx class699Var) {
        PaletteColorStack class115VarColorStack= class699Var.drawEngine().colorStack();
        StylePalette class764VarPalette= class699Var.theme().palette();
        UserSession class385VarUserSession= Expensive.INSTANCE.userSession();
        String strUsername= class385VarUserSession.username();
        String strExpire= class385VarUserSession.expire();
        GlTextureObject class073VarTexture= class385VarUserSession.texture();
        class073VarTexture.magFilter(9728);
        class073VarTexture.minFilter(9728);
        class699Var.roundedTexture(class073VarTexture, x(), y(), 24.0f, 24.0f, 7.0f, class115VarColorStack.white());
        float fY= y() + ((24.0f - ((this.font.getHeight(12.0f) + 1.0f) + this.font.getHeight(11.0f))) / 2.0f);
        class699Var.text(this.font, strUsername, 12, x() + 24.0f + 6.0f, fY, class115VarColorStack.computeColor(class764VarPalette.text().tone(200).argb()));
        class699Var.text(this.font, "Till " + strExpire, 11, x() + 24.0f + 6.0f, fY + this.font.getHeight(12.0f) + 1.0f, class115VarColorStack.computeColor(class764VarPalette.accent().argb()));
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        UserSession class385VarUserSession= Expensive.INSTANCE.userSession();
        String strUsername= class385VarUserSession.username();
        String strExpire= class385VarUserSession.expire();
        setSize(30.0f + Math.max(class698Var.textWidthPhysical(this.font, "Till " + strExpire, 10), class698Var.textWidthPhysical(this.font, strUsername, 12)) + 8.0f, 24.0f);
    }
}
