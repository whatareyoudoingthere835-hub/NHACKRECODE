package aethereal.core.models;
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryChipBar extends WidgetContainer {
    public static final float chipHeight = 27.0f;
    public static final float chipCornerRadius = 14.0f;
    public static final int labelFontSize = 12;
    public static final float iconSize = 12.0f;
    public static final float rowSpacing = 8.0f;
    public static final int iconTextGap = 12;
    public static final int columnCount = 2;
    public final MsdfFont labelFont = Fonts.INTER_SEMIBOLD.get();
    public final Map<ModuleCategory, CategoryChip> chipCache = new EnumMap(ModuleCategory.class);
    public final List<CategoryChip> chips = new ArrayList();

    @Override
    public void render(DrawCtx class699Var) {
        ModuleTab class847VarModuleTab= Expensive.INSTANCE.tabsController().current().moduleTab();
        if (ModuleCategory.categoriesForTab(class847VarModuleTab).isEmpty()) {
            return;
        }
        StylePalette class764VarPalette= class699Var.theme().palette();
        PaletteColorStack class115VarColorStack= class699Var.colorStack();
        Set<ModuleCategory> setSelectedCategories= Expensive.INSTANCE.tabsController().selectedCategories(class847VarModuleTab);
        for (CategoryChip class788Var : this.chips) {
            class788Var.selected(setSelectedCategories.contains(class788Var.category()));
            class788Var.render(class699Var, class764VarPalette, class115VarColorStack);
        }
        super.render(class699Var);
    }

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) throws MatchException {
        if (super.handleInput(class688Var, z)) {
            return true;
        }
        ModuleTab class847VarModuleTab= Expensive.INSTANCE.tabsController().current().moduleTab();
        if (ModuleCategory.categoriesForTab(class847VarModuleTab).isEmpty()) {
            return z;
        }
        InputEvent class691VarInputEvent= class688Var.inputEvent();
        if (class691VarInputEvent instanceof CursorMoveInput) {
            try {
                PixelPoint class708VarMousePosition= ((CursorMoveInput) class691VarInputEvent).mousePosition();
                float fX= class708VarMousePosition.x();
                float fY= class708VarMousePosition.y();
                for (CategoryChip class788Var : this.chips) {
                    class788Var.hoverAnimator.state(class788Var.contains(fX, fY));
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        if (class691VarInputEvent instanceof MouseButtonInput) {
            MouseButtonInput class693Var= (MouseButtonInput) class691VarInputEvent;
            if (class693Var.action().press() && class693Var.button() == 0) {
                float fX2= class688Var.logicalMousePosition().x();
                float fY2= class688Var.logicalMousePosition().y();
                for (CategoryChip class788Var2 : this.chips) {
                    if (class788Var2.contains(fX2, fY2)) {
                        Expensive.INSTANCE.tabsController().toggleCategory(class847VarModuleTab, class788Var2.category());
                        return true;
                    }
                }
            }
        }
        return z;
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        List<ModuleCategory> listCategoriesForTab= ModuleCategory.categoriesForTab(Expensive.INSTANCE.tabsController().current().moduleTab());
        this.chips.clear();
        if (listCategoriesForTab.isEmpty()) {
            visible(false);
            return;
        }
        visible(true);
        float fX= x();
        float fY= y();
        float fWidth= width();
        int i= 0;
        for (int i2 = 0; i2 < listCategoriesForTab.size(); i2 += 2) {
            ModuleCategory class672Var= listCategoriesForTab.get(i2);
            CategoryChip class788VarComputeIfAbsent= this.chipCache.computeIfAbsent(class672Var, class672Var2 -> {
                return new CategoryChip(this, class672Var2);
            });
            float fTextWidthPhysical= 42.0f + class698Var.textWidthPhysical(this.labelFont, class672Var.displayName(), 12);
            float f= fY + (i * 35.0f);
            class788VarComputeIfAbsent.bounds(fX, f, fTextWidthPhysical, chipHeight);
            this.chips.add(class788VarComputeIfAbsent);
            if (i2 + 1 < listCategoriesForTab.size()) {
                ModuleCategory class672Var3= listCategoriesForTab.get(i2 + 1);
                CategoryChip class788VarComputeIfAbsent2= this.chipCache.computeIfAbsent(class672Var3, class672Var4 -> {
                    return new CategoryChip(this, class672Var4);
                });
                float fTextWidthPhysical2= 42.0f + class698Var.textWidthPhysical(this.labelFont, class672Var3.displayName(), 12);
                float f2= fX + fTextWidthPhysical + 5.0f;
                float f3= fWidth - (f2 - fX);
                class788VarComputeIfAbsent2.bounds(f2, f, fTextWidthPhysical2, chipHeight);
                this.chips.add(class788VarComputeIfAbsent2);
            }
            i++;
        }
        setSize(width(), (i * chipHeight) + ((i - 1) * rowSpacing));
        super.layout(class698Var);
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        for (CategoryChip class788Var : this.chips) {
            class788Var.hoverAnimator.animate(class141Var);
            class788Var.selectedAnimator.animate(class141Var);
        }
        super.animation(class141Var);
    }

    public boolean hasElements() {
        return !ModuleCategory.categoriesForTab(Expensive.INSTANCE.tabsController().current().moduleTab()).isEmpty();
    }
}
