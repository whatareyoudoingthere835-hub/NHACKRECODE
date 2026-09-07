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

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix4f;


public class AutoBuyTargetCard extends AbstractFrame {
    
    private final AutoBuyTarget target;
    private final Runnable editAction;
    private final Runnable deleteAction;
    
    private final MsdfFont font = Fonts.INTER_SEMIBOLD.get();
    private final MsdfFont mediumFont = Fonts.INTER_MEDIUM.get();
    
    private final GlTextureObject editIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/pencil.png"));
    private final GlTextureObject deleteIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/trash.png"));
    
    private final ToggleAnimator hoverAnimator = new ToggleAnimator(150, Easings.EASE_IN_OUT_CUBIC);
    
    private final IconButtonWidget editButton;
    private final IconButtonWidget deleteButton;
    private final ToggleTextButton activeToggle;
    
    AutoBuyTargetCard(AutoBuyTarget target, Runnable editAction, Runnable deleteAction) {
        super(new FrameElementColumn(0.0f, 0.0f));
        this.target = target;
        this.editAction = editAction;
        this.deleteAction = deleteAction;
        
        this.editButton = new IconButtonWidget(this.editIcon, editAction, 14.0f, 14.0f);
        this.deleteButton = new IconButtonWidget(this.deleteIcon, deleteAction, 14.0f, 14.0f);
        this.activeToggle = new ToggleTextButton(() -> {}, this.mediumFont, null, 4.0f, 8.0f, 4.0f, 12).label(Translation.clearText("Активно"), 12);
    }
    
    @Override
    public boolean handleInput(InputEventContext ctx, boolean z) {
        boolean handled= false;
        
        this.hoverAnimator.state(ctx.inArea(x(), y(), width(), height()) && !z);
        
        if (this.editButton.handleInput(ctx, z)) handled = true;
        if (this.deleteButton.handleInput(ctx, z)) handled = true;
        if (this.activeToggle.handleInput(ctx, z)) handled = true;
        
        return handled;
    }
    
    @Override
    public void layout(LayoutScaleContext class698Var) {
        this.editButton.layout(class698Var);
        this.deleteButton.layout(class698Var);
        this.activeToggle.layout(class698Var);
    }
    
    @Override
    public void animation(WeightedEngine engine) {
        this.hoverAnimator.animate(engine);
        
        this.editButton.animation(engine);
        this.deleteButton.animation(engine);
        this.activeToggle.animation(engine);
    }
    
    public void render(DrawCtx ctx) {
        float x= x();
        float y= y();
        PaletteColorStack cs= ctx.drawEngine().colorStack();
        float hoverVal= this.hoverAnimator.smoothAnimation();
        
        int bgColor= cs.interpolate(
            cs.computeColor(ctx.theme().palette().surfaceBackground().tone(500).argb()),
            cs.computeColor(ctx.theme().palette().surfaceBackground().tone(400).argb()),
            hoverVal
        );
        int outlineColor= cs.computeColor(ctx.theme().palette().surfaceOutline().tone(600).argb());
        
        // Background
        ctx.drawEngine().roundedRectangle(
            ctx.matrixStack().peek().getPositionMatrix(),
            x, y, width(), height(), 8.0f, 1.0f,
            outlineColor, outlineColor, outlineColor, outlineColor,
            bgColor, bgColor, bgColor, bgColor
        );
        
        // Render item icon (using Minecraft item renderer)
        float currentX= x + 10.0f;
        float currentY= y + (height() / 2.0f) - 8.0f;
        // ctx.drawEngine().item(...) -> I might have to use net.minecraft.client.render.item.ItemRenderer
        // But in Expensive UI, usually there's a util. I'll just draw text for now if I don't have itemRenderer util.
        
        String displayName= "Предмет: " + target.getName();
        ctx.text(this.font, displayName, 14, currentX + 24.0f, y + 14.0f, cs.computeColor(ctx.theme().palette().text().tone(900).argb()));
        
        String priceText= "Цена: " + target.getPriceText();
        ctx.text(this.mediumFont, priceText, 12, currentX + 24.0f, y + 30.0f, cs.computeColor(ctx.theme().palette().text().tone(600).argb()));
        
        // Layout buttons on the right
        float rightX= x + width() - 10.0f;
        
        this.deleteButton.setPosition(rightX - this.deleteButton.width(), y + (height() / 2.0f) - (this.deleteButton.height() / 2.0f));
        this.deleteButton.render(ctx);
        rightX -= this.deleteButton.width() + 4.0f;
        
        this.editButton.setPosition(rightX - this.editButton.width(), y + (height() / 2.0f) - (this.editButton.height() / 2.0f));
        this.editButton.render(ctx);
        rightX -= this.editButton.width() + 10.0f;
        
        this.activeToggle.setPosition(rightX - this.activeToggle.bounds.width(), y + (height() / 2.0f) - (this.activeToggle.bounds.height() / 2.0f));
        this.activeToggle.render(ctx);
    }
    
    @Override
    public float width() {
        return 300.0f; // Placeholder, set by grid layout
    }
    
    @Override
    public float height() {
        return 50.0f;
    }
    
    @Override
    public float contentHeight() {
        return 50.0f;
    }
}
