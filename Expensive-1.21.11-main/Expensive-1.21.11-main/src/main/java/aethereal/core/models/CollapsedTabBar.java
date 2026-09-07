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

import java.util.Iterator;
import java.util.List;

public class CollapsedTabBar extends WidgetContainer {
    final AnimatedFloat indicatorPosition = new AnimatedFloat(100, Easings.EASE_IN_OUT_CUBIC);
    final GlTextureObject triangleIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/triangle.png"));
    final List<MenuTabElement> tabElements = Expensive.INSTANCE.tabsController().tabElements();

    @Override
    public void render(DrawCtx class699Var) {
        PaletteColorStack class115VarColorStack= class699Var.colorStack();
        class699Var.fillOutlinedRoundedRect(x(), y(), width(), height(), 20.0f, 2.5f, class115VarColorStack.computeColor(class699Var.theme().palette().surfaceOutline().tone(600).argb()), class115VarColorStack.computeColor(class699Var.theme().palette().surfaceOutline().tone(600).argb(), 0));
        Iterator<MenuTabElement> it= this.tabElements.iterator();
        while (it.hasNext()) {
            it.next().drawCollapsed(class699Var);
        }
        class699Var.fillRect(x() + 2.0f + this.indicatorPosition.animatedValue(), (y() + height()) - 1.0f, 10.0f, 1.0f, class115VarColorStack.computeColor(class699Var.theme().palette().accent().argb()));
        super.render(class699Var);
    }

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) {
        Iterator<MenuTabElement> it= this.tabElements.iterator();
        while (it.hasNext()) {
            if (it.next().handleInput(class688Var, z)) {
                return true;
            }
        }
        return super.handleInput(class688Var, z);
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
        float totalTabsW= 0.0f;
        int tabCount= this.tabElements.size();
        for (MenuTabElement class732Var : this.tabElements) {
            class732Var.layout(class698Var);
            totalTabsW += class732Var.collapsedWidth();
        }
        float padding= 18.0f;
        float spacing= 18.0f;
        float totalWidth= (padding * 2.0f) + totalTabsW + Math.max(0, tabCount - 1) * spacing;
        setSize(totalWidth, 36.0f);
        setPosition((this.parent.x() + (this.parent.width() / 2.0f)) - (width() / 2.0f), (this.parent.y() + (parent().height() / 2.0f)) - (height() / 2.0f));

        float curX= x() + padding;
        for (MenuTabElement class732Var : this.tabElements) {
            class732Var.setPosition(curX, (y() + (height() / 2.0f)) - (class732Var.collapsedHeight() / 2.0f));
            curX += class732Var.collapsedWidth() + spacing;
        }
        this.indicatorPosition.destination(Expensive.INSTANCE.tabsController().current().x() - x());
        super.layout(class698Var);
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        Iterator<MenuTabElement> it= this.tabElements.iterator();
        while (it.hasNext()) {
            it.next().animation(class141Var);
        }
        this.indicatorPosition.animate(class141Var);
        super.animation(class141Var);
    }

    @Override
    public void handleClose() {
        this.tabElements.forEach((v0) -> {
            v0.handleClose();
        });
        super.handleClose();
    }

    @Override
    public void collectBloomElements(RenderCommandQueue class676Var) {
        this.tabElements.forEach(class732Var -> {
            class732Var.collectBloomElements(class676Var);
        });
        super.collectBloomElements(class676Var);
    }
}
