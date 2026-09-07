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
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class VanillaButton extends ButtonWidget {
    public static final ButtonTextures buttonTextures = new ButtonTextures(Identifier.ofVanilla("widget/button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"));

    public VanillaButton(int i, int i2, int i3, int i4, net.minecraft.text.Text text, ButtonWidget.PressAction pressAction) {
        super(i, i2, i3, i4, text, pressAction, DEFAULT_NARRATION_SUPPLIER);
    }

    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, buttonTextures.get(this.active, isSelected()), getX(), getY(), getWidth(), getHeight(), net.minecraft.util.math.ColorHelper.getWhite(this.alpha));
        int color= this.active ? 16777215 : 10526880;
        context.drawCenteredTextWithShadow(Mc.INSTANCE.getMinecraft().textRenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, color | net.minecraft.util.math.MathHelper.ceil(this.alpha * 255.0f) << 24);
    }
}
