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


public final class FrameSearchTarget implements SearchNavTarget {
    public final AbstractFrame frameContainer;
    public final MenuTabElement tab;

    public FrameSearchTarget(AbstractFrame class757Var, MenuTabElement class732Var) {
        this.frameContainer = class757Var;
        this.tab = class732Var;
    }

    @Override
    public void navigate(SearchNavigator class843Var) {
        class843Var.focusFrame(this.frameContainer, this.tab);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "frameContainer=" + this.frameContainer + ", " + "tab=" + this.tab + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.frameContainer, this.tab);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FrameSearchTarget)) return false;
        FrameSearchTarget o= (FrameSearchTarget) obj;
        return java.util.Objects.equals(this.frameContainer, o.frameContainer) && java.util.Objects.equals(this.tab, o.tab);
    }
public AbstractFrame frameContainer() {
        return this.frameContainer;
    }

    public MenuTabElement tab() {
        return this.tab;
    }
}
