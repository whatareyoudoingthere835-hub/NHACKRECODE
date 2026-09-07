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

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import java.util.function.Consumer;

public class AutoBuyEditorPanel extends WidgetContainer {
    
    private final MsdfFont titleFont = Fonts.INTER_SEMIBOLD.get();
    private final MsdfFont mediumFont = Fonts.INTER_MEDIUM.get();
    
    private final GlTextureObject closeIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/cross.png"));
    
    private boolean opened;
    private AutoBuyTarget currentTarget;
    private Consumer<AutoBuyTarget> onSave;
    
    private final IconButtonWidget closeButton;
    private final LabeledIconButton saveButton;
    
    private final TextInputField priceField;
    private final TextInputField countField;
    
    AutoBuyEditorPanel() {
        this.closeButton = new IconButtonWidget(this.closeIcon, this::close, 14.0f, 14.0f);
        this.saveButton = new LabeledIconButton(Translation.clearText("Сохранить"), this::save, this.mediumFont, null, 7.0f, 8.0f, 5.5f, 12);
        
        this.priceField = new TextInputField(this.mediumFont, 14);
        this.countField = new TextInputField(this.mediumFont, 14);
    }
    
    void open(AutoBuyTarget target, Consumer<AutoBuyTarget> onSave) {
        this.currentTarget = target;
        this.onSave = onSave;
        
        this.priceField.clearText();
        this.priceField.textBuffer.append(target.getPriceText() == null ? "" : target.getPriceText());
        
        this.countField.clearText();
        this.countField.textBuffer.append(String.valueOf(target.getMinCount()));
        
        this.opened = true;
    }
    
    void close() {
        this.opened = false;
        this.currentTarget = null;
        this.onSave = null;
    }
    
    private void save() {
        if (this.currentTarget != null && this.onSave != null) {
            this.currentTarget.setPriceText(this.priceField.textBuffer.toString());
            
            try {
                this.currentTarget.setMinCount(Integer.parseInt(this.countField.textBuffer.toString()));
            } catch (NumberFormatException e) {
                // Ignore invalid input for count
            }
            
            this.onSave.accept(this.currentTarget);
        }
        close();
    }
    
    boolean isOpened() {
        return this.opened;
    }

    public boolean opened() {
        return this.opened;
    }
    
    @Override
    public boolean handleInput(InputEventContext ctx, boolean z) {
        if (!this.opened) return false;
        
        boolean handled= false;
        if (this.closeButton.handleInput(ctx, z)) handled = true;
        if (this.saveButton.handleInput(ctx, z)) handled = true;
        if (this.priceField.handleInput(ctx, z)) handled = true;
        if (this.countField.handleInput(ctx, z)) handled = true;
        
        return true; // block input to elements below
    }
    
    @Override
    public void layout(LayoutScaleContext ctx) {
        if (!this.opened) return;
        
        this.closeButton.layout(ctx);
        this.saveButton.layout(ctx);
        
        float panelW= 350.0f;
        float panelH= 250.0f;
        float panelX= x() + (width() - panelW) / 2.0f;
        float panelY= y() + (height() - panelH) / 2.0f;
        
        this.closeButton.setPosition(panelX + panelW - 24.0f, panelY + 12.0f);
        this.saveButton.setPosition(panelX + panelW - this.saveButton.width() - 16.0f, panelY + panelH - this.saveButton.height() - 16.0f);
        
        this.priceField.listen(panelX + 16.0f, panelY + 60.0f, panelW - 32.0f, 26.0f, panelW - 32.0f, panelX + 20.0f, ctx.scaleFactor());
        this.countField.listen(panelX + 16.0f, panelY + 110.0f, panelW - 32.0f, 26.0f, panelW - 32.0f, panelX + 20.0f, ctx.scaleFactor());
    }
    
    @Override
    public void animation(WeightedEngine engine) {
        if (!this.opened) return;
        
        this.closeButton.animation(engine);
        this.saveButton.animation(engine);
    }
    
    public void render(DrawCtx ctx) {
        if (!this.opened) return;
        
        float panelW= 350.0f;
        float panelH= 250.0f;
        float panelX= x() + (width() - panelW) / 2.0f;
        float panelY= y() + (height() - panelH) / 2.0f;
        
        PaletteColorStack cs= ctx.drawEngine().colorStack();
        int bgColor= cs.computeColor(ctx.theme().palette().surfaceBackground().tone(500).argb());
        int outlineColor= cs.computeColor(ctx.theme().palette().surfaceOutline().tone(600).argb());
        int textColor= cs.computeColor(ctx.theme().palette().text().tone(900).argb());
        
        // Draw overlay background
        ctx.fillRoundedRect(x(), y(), width(), height(), 0.0f, cs.computeColor(0, 0, 0, 100));
        
        // Draw panel
        ctx.drawEngine().roundedRectangle(
            ctx.matrixStack().peek().getPositionMatrix(),
            panelX, panelY, panelW, panelH, 12.0f, 1.0f,
            outlineColor, outlineColor, outlineColor, outlineColor,
            bgColor, bgColor, bgColor, bgColor
        );
        
        // Draw title
        ctx.text(this.titleFont, "Настройка предмета", 18, panelX + 16.0f, panelY + 16.0f, textColor);
        
        // Labels
        ctx.text(this.mediumFont, "Цена (за шт.)", 14, panelX + 16.0f, panelY + 45.0f, textColor);
        ctx.text(this.mediumFont, "Количество", 14, panelX + 16.0f, panelY + 95.0f, textColor);
        
        // Close button
        this.closeButton.render(ctx);
        this.saveButton.render(ctx);
        
        // Draw fields
        ctx.fillRoundedRect(this.priceField.bounds.x(), this.priceField.bounds.y(), this.priceField.bounds.width(), this.priceField.bounds.height(), 4.0f, cs.computeColor(ctx.theme().palette().surfaceBackground().tone(400).argb()));
        ctx.text(this.mediumFont, this.priceField.textBuffer.toString(), 14, this.priceField.bounds.x() + 6.0f, this.priceField.bounds.y() + 13.0f - (this.mediumFont.getHeight(14) / 2.0f), textColor);
        
        ctx.fillRoundedRect(this.countField.bounds.x(), this.countField.bounds.y(), this.countField.bounds.width(), this.countField.bounds.height(), 4.0f, cs.computeColor(ctx.theme().palette().surfaceBackground().tone(400).argb()));
        ctx.text(this.mediumFont, this.countField.textBuffer.toString(), 14, this.countField.bounds.x() + 6.0f, this.countField.bounds.y() + 13.0f - (this.mediumFont.getHeight(14) / 2.0f), textColor);
    }
}
