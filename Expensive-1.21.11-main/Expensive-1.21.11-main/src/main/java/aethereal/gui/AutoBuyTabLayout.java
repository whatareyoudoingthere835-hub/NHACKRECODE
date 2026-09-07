package aethereal.gui;

import aethereal.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.resources.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import net.minecraft.item.ItemStack;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class AutoBuyTabLayout extends AbstractTabLayout {
    private final MsdfFont font = Fonts.INTER_SEMIBOLD.get();
    private final MsdfFont mediumFont = Fonts.INTER_MEDIUM.get();
    
    private final GlTextureObject diamondIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/tabs/autobuy.png"));
    private final GlTextureObject clickIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/plus.png"));
    private final GlTextureObject sortIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/multienum.png"));
    
    final IconLabelBadge headerBadge = new IconLabelBadge(this.diamondIcon, Translation.clearText("Автобай предметов"));
    public final List<LabeledIconButton> actionButtons = new ArrayList<>();
    public final DropdownWidget<SortOrder> sortDropdown;
    public final TextInputField searchField;
    
    final ToggleTextButton funtimeToggle;
    public final ToggleTextButton vanillaToggle;
    
    ScrollbarWidget scrollbar;
    private final WidgetBounds headerBounds = new WidgetBounds(0, 0, 0, 0);
    
    final AutoBuyEditorPanel editorPanel = new AutoBuyEditorPanel();
    
    final AutoBuyPickerPanel pickerPanel = new AutoBuyPickerPanel((item) -> {
        this.pickerPanel.close();
        AutoBuyTarget target = new AutoBuyTarget(item.getDefaultStack(), item.getName().getString(), true, "1", 1, 0);
        this.editorPanel.open(target, (savedTarget) -> {
            AutoBuyDataStorage.INSTANCE.getTargets().add(savedTarget);
            refreshCards();
        });
    });
    
    public AutoBuyTabLayout() {
        this.actionButtons.add(new LabeledIconButton(Translation.clearText("Опции"), () -> {}, this.font, new GlTextureObject(new ClasspathResource("/icons/menu/new/property.png")), 7.0f, 8.0f, 5.5f, 12));
        this.actionButtons.add(new LabeledIconButton(Translation.clearText("История"), () -> {}, this.font, new GlTextureObject(new ClasspathResource("/icons/menu/new/time.png")), 7.0f, 8.0f, 5.5f, 12));
        this.actionButtons.add(new LabeledIconButton(Translation.clearText("Спарсить цены"), () -> {}, this.font, new GlTextureObject(new ClasspathResource("/icons/menu/new/progress.png")), 7.0f, 8.0f, 5.5f, 12));
        this.actionButtons.add(new LabeledIconButton(Translation.clearText("Добавить предмет"), this.pickerPanel::open, this.font, this.clickIcon, 7.0f, 8.0f, 5.5f, 12));
        
        this.sortDropdown = new DropdownWidget<>(this.font, this.sortIcon, 8.0f, 8.0f, 5.5f, 12);
        this.sortDropdown.setOptions(List.of(
            new DropdownOption<>(SortOrder.ALPHABETICAL_AZ, Translation.clearText("В алфавитном порядке")),
            new DropdownOption<>(SortOrder.NEWEST_FIRST, Translation.clearText("По количеству")),
            new DropdownOption<>(SortOrder.OLDEST_FIRST, Translation.clearText("По цене"))
        ), SortOrder.ALPHABETICAL_AZ);
        this.sortDropdown.label(Translation.clearText("В алфавитном порядке"), 12);
        
        this.searchField = new TextInputField(this.mediumFont, 14);
        
        this.funtimeToggle = new ToggleTextButton(() -> {}, this.mediumFont, null, 4.0f, 8.0f, 4.0f, 12).label(Translation.clearText("Funtime предметы"), 12);
        this.vanillaToggle = new ToggleTextButton(() -> {}, this.mediumFont, null, 4.0f, 8.0f, 4.0f, 12).label(Translation.clearText("Ванильные предметы"), 12);
        this.funtimeToggle.setActive(true);
        this.vanillaToggle.setActive(true);
        
        refreshCards();
    }
    
    private void refreshCards() {
        if (this.tab == null) return;
        this.tab.frames().clear();
        for (AutoBuyTarget target : AutoBuyDataStorage.INSTANCE.getTargets()) {
            AutoBuyTargetCard card = new AutoBuyTargetCard(target, () -> {
                this.editorPanel.open(target, (savedTarget) -> {
                    refreshCards();
                });
            }, () -> {
                AutoBuyDataStorage.INSTANCE.getTargets().remove(target);
                refreshCards();
            });
            this.tab.frames().add(card);
        }
    }

    @Override
    public void initialize(MenuTabElement tab) {
        super.initialize(tab);
        ScrollArea scrollArea = tab.scrollingAreaComponent();
        Objects.requireNonNull(scrollArea);
        Supplier<Float> scrollY = scrollArea::scrollY;
        Supplier<Float> contentHeight = this::getContentHeight;
        Supplier<Float> viewportHeight = () -> ((MenuWindow.MENU_HEIGHT - MenuWindow.COLLAPSED_HEADER_HEIGHT) - this.headerBounds.height()) - 16.0f;
        this.scrollbar = new ScrollbarWidget(scrollY, contentHeight, viewportHeight, scrollArea::scrollTo, 16.0f, 3.0f, 24.0f);
        refreshCards();
    }

    @Override
    public float getContentHeight() {
        return this.tab != null ? this.tab.contentHeight() : 0.0f;
    }

    @Override
    public float width() {
        return MenuWindow.MENU_WIDTH;
    }

    @Override
    public void positionFrames() {
        if (this.tab == null) return;
        float x = 20.0f;
        float y = 20.0f;
        float cardWidth = 140.0f;
        float cardHeight = 60.0f;
        float gap = 10.0f;

        for (AbstractFrame frame : this.tab.frames()) {
            frame.setPosition(x, y);
            x += cardWidth + gap;
            if (x + cardWidth > width() - 20.0f) {
                x = 20.0f;
                y += cardHeight + gap;
            }
        }
    }

    @Override
    public void render(DrawCtx ctx) {
        if (this.pickerPanel != null) {
            this.pickerPanel.render(ctx);
        }
        if (this.editorPanel != null) {
            this.editorPanel.render(ctx);
        }
    }

    @Override
    public void layout(LayoutScaleContext ctx) {
    }

    @Override
    public void animation(WeightedEngine engine) {
    }

    @Override
    public boolean handleInput(InputEventContext ctx, boolean active) {
        if (this.editorPanel != null && this.editorPanel.handleInput(ctx, active)) {
            return true;
        }
        if (this.pickerPanel != null && this.pickerPanel.handleInput(ctx, active)) {
            return true;
        }
        return false;
    }
}
