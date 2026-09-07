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

public final class MenuSearchNavigator implements SearchNavigator {
    @Override
    public void focusFrame(AbstractFrame class757Var, MenuTabElement class732Var) {
        TabsController class733VarTabsController= Expensive.INSTANCE.tabsController();
        if (class733VarTabsController.current() != class732Var) {
            class733VarTabsController.open(class732Var);
            class732Var.markFramesDirty();
        }
        ensureCategoryVisible(class733VarTabsController, class732Var, class757Var);
        class732Var.focusFrame(class757Var);
    }

    @Override
    public void focusSetting(MenuTabElement class732Var, AbstractFrame class757Var, Setting class661Var) {
        TabsController class733VarTabsController= Expensive.INSTANCE.tabsController();
        if (class733VarTabsController.current() != class732Var) {
            class733VarTabsController.open(class732Var);
            class732Var.markFramesDirty();
        }
        ensureCategoryVisible(class733VarTabsController, class732Var, class757Var);
        class732Var.focusSetting(class757Var, class661Var);
    }

    public void ensureCategoryVisible(TabsController class733Var, MenuTabElement class732Var, AbstractFrame class757Var) {
        if (class757Var instanceof ModuleCard) {
            class733Var.ensureCategoryVisible(class732Var, ((ModuleCard) class757Var).module().getCategory());
        }
    }
}
