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

import java.util.Iterator;
import java.util.List;

public class ExpandedTabPanel extends WidgetContainer {
    public float offsetX= 0.0f;
    public float offsetY= 0.0f;
    final List<MenuTabElement> tabElements = Expensive.INSTANCE.tabsController().tabElements();

    @Override
    public void render(DrawCtx class699Var) {
        Iterator<MenuTabElement> it= this.tabElements.iterator();
        while (it.hasNext()) {
            it.next().drawExpanded(class699Var);
        }
        super.render(class699Var);
    }

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) {
        boolean zHandleInput= super.handleInput(class688Var, z);
        if (!zHandleInput) {
            Iterator<MenuTabElement> it= this.tabElements.iterator();
            while (it.hasNext()) {
                zHandleInput |= it.next().handleInput(class688Var, zHandleInput);
            }
        }
        return zHandleInput;
    }

    @Override
    public void onMenuDrag(boolean z) {
        this.tabElements.forEach(class732Var -> {
            class732Var.onMenuDrag(z);
        });
        super.onMenuDrag(z);
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        setSize(270.0f, MenuWindow.EXPANDED_HEADER_HEIGHT);
        setPosition(this.parent.x(), this.parent.y());
        float fMax= 0.0f;
        float fMax2= 0.0f;
        for (MenuTabElement class732Var : this.tabElements) {
            class732Var.layout(class698Var);
            fMax = Math.max(fMax, class732Var.expandedWidth());
            fMax2 = Math.max(fMax2, class732Var.expandedHeight());
        }
        float fX= x() + 15.0f;
        float fY= y() + MenuWindow.COLLAPSED_HEADER_HEIGHT + 33.0f;
        float f= fX + fMax + 6.0f;
        int size= this.tabElements.size();
        for (int i = 0; i < size; i++) {
            this.tabElements.get(i).setPosition(i % 2 == 0 ? fX : f, fY + ((i / 2) * (fMax2 + 4.0f)));
        }
        super.layout(class698Var);
    }

    @Override
    public void handleClose() {
        this.tabElements.forEach((v0) -> {
            v0.handleClose();
        });
        super.handleClose();
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        Iterator<MenuTabElement> it= this.tabElements.iterator();
        while (it.hasNext()) {
            it.next().animation(class141Var);
        }
        super.animation(class141Var);
    }
}
