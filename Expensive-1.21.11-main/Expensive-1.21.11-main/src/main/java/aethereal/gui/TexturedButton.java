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

public class TexturedButton extends ButtonWidget {
    public final ButtonTextures textures;
    public final net.minecraft.text.Text label;

    public TexturedButton(int i, int i2, int i3, int i4, net.minecraft.text.Text text, ButtonTextures buttonTextures, ButtonWidget.PressAction pressAction) {
        super(i, i2, i3, i4, net.minecraft.text.Text.empty(), pressAction, DEFAULT_NARRATION_SUPPLIER);
        this.label = text;
        this.textures = buttonTextures;
    }

    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures.get(this.active, isSelected()), getX(), getY(), getWidth(), getHeight());
        if (this.label != null && !this.label.getString().isEmpty()) {
            int color= this.active ? 16777215 : 10526880;
            context.drawCenteredTextWithShadow(Mc.INSTANCE.getMinecraft().textRenderer, this.label, this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, color | net.minecraft.util.math.MathHelper.ceil(this.alpha * 255.0f) << 24);
        }
    }
}
