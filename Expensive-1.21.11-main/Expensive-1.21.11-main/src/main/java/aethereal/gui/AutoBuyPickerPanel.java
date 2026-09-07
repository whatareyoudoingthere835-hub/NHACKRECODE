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

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AutoBuyPickerPanel extends WidgetContainer {
    
    private final MsdfFont titleFont = Fonts.INTER_SEMIBOLD.get();
    private final MsdfFont mediumFont = Fonts.INTER_MEDIUM.get();
    
    private final GlTextureObject closeIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/cross.png"));
    
    private boolean opened;
    
    private final TextInputField searchField;
    private final IconButtonWidget closeButton;
    private float scrollY = 0.0f;
    
    private final Consumer<Item> onItemSelected;
    
    // Items grid state
    private final List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    
    AutoBuyPickerPanel(Consumer<Item> onItemSelected) {
        this.onItemSelected = onItemSelected;
        
        this.searchField = new TextInputField(this.mediumFont, 14);
        this.searchField.changeCallback = this::updateSearch;
        
        this.closeButton = new IconButtonWidget(this.closeIcon, this::close, 14.0f, 14.0f);
        
        // Populate allItems from registry
        for (Item item : Registries.ITEM) {
            if (item != Items.AIR) {
                allItems.add(item);
            }
        }
        this.filteredItems.addAll(this.allItems);
    }
    
    private void updateSearch(String query) {
        this.filteredItems.clear();
        String lowerQuery= query.toLowerCase();
        for (Item item : this.allItems) {
            String name= item.getName().getString().toLowerCase();
            if (name.contains(lowerQuery)) {
                this.filteredItems.add(item);
            }
        }
    }
    
    void open() {
        this.opened = true;
        this.searchField.clearText();
        updateSearch("");
    }
    
    void close() {
        this.opened = false;
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
        if (this.searchField.handleInput(ctx, z)) handled = true;
        
        if (ctx.inputEvent() instanceof ScrollInput) {
            ScrollInput scrollEvent= (ScrollInput) ctx.inputEvent();
            float scrollDelta= (float) scrollEvent.deltaY;
            if (scrollDelta != 0.0f) {
                this.scrollY += scrollDelta * 20.0f;
                if (this.scrollY > 0) this.scrollY = 0;
                handled = true;
            }
        }
        
        return true; // block input to elements below
    }
    
    @Override
    public void layout(LayoutScaleContext ctx) {
        if (!this.opened) return;
        
        this.closeButton.layout(ctx);
        
        float panelW= 400.0f;
        float panelH= 300.0f;
        float panelX= x() + (width() - panelW) / 2.0f;
        float panelY= y() + (height() - panelH) / 2.0f;
        
        this.closeButton.setPosition(panelX + panelW - 24.0f, panelY + 12.0f);
        
        this.searchField.listen(panelX + 16.0f, panelY + 40.0f, panelW - 32.0f, 26.0f, panelW - 32.0f, panelX + 20.0f, ctx.scaleFactor());
    }
    
    @Override
    public void animation(WeightedEngine engine) {
        if (!this.opened) return;
        
        this.closeButton.animation(engine);
    }
    
    public void render(DrawCtx ctx) {
        if (!this.opened) return;
        
        float panelW= 400.0f;
        float panelH= 300.0f;
        float panelX= x() + (width() - panelW) / 2.0f;
        float panelY= y() + (height() - panelH) / 2.0f;
        
        PaletteColorStack cs= ctx.drawEngine().colorStack();
        int bgColor= cs.computeColor(ctx.theme().palette().surfaceBackground().tone(500).argb());
        int outlineColor= cs.computeColor(ctx.theme().palette().surfaceOutline().tone(600).argb());
        
        // Draw overlay background
        ctx.fillRoundedRect(x(), y(), width(), height(), 0.0f, cs.computeColor(0, 0, 0, 100)); // dim background
        
        // Draw panel
        ctx.drawEngine().roundedRectangle(
            ctx.matrixStack().peek().getPositionMatrix(),
            panelX, panelY, panelW, panelH, 12.0f, 1.0f,
            outlineColor, outlineColor, outlineColor, outlineColor,
            bgColor, bgColor, bgColor, bgColor
        );
        
        // Draw title
        ctx.text(this.titleFont, "Выберите предмет", 18, panelX + 16.0f, panelY + 16.0f, cs.computeColor(ctx.theme().palette().text().tone(900).argb()));
        
        // Close button
        this.closeButton.render(ctx);
        
        // Draw Search field
        ctx.fillRoundedRect(this.searchField.bounds.x(), this.searchField.bounds.y(), this.searchField.bounds.width(), this.searchField.bounds.height(), 4.0f, cs.computeColor(ctx.theme().palette().surfaceBackground().tone(400).argb()));
        ctx.text(this.mediumFont, this.searchField.textBuffer.toString(), 14, this.searchField.bounds.x() + 6.0f, this.searchField.bounds.y() + 13.0f - (this.mediumFont.getHeight(14) / 2.0f), cs.computeColor(ctx.theme().palette().text().tone(900).argb()));
        
        // TODO: render item grid
    }
}
