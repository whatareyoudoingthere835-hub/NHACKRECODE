package aethereal.core.types;
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


public final class SettingSearchTarget implements SearchNavTarget {
    public final MenuTabElement tab;
    public final AbstractFrame frame;
    public final Setting setting;

    public SettingSearchTarget(MenuTabElement class732Var, AbstractFrame class757Var, Setting class661Var) {
        this.tab = class732Var;
        this.frame = class757Var;
        this.setting = class661Var;
    }

    @Override
    public void navigate(SearchNavigator class843Var) {
        class843Var.focusSetting(this.tab, this.frame, this.setting);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "tab=" + this.tab + ", " + "frame=" + this.frame + ", " + "setting=" + this.setting + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.tab, this.frame, this.setting);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SettingSearchTarget)) return false;
        SettingSearchTarget o= (SettingSearchTarget) obj;
        return java.util.Objects.equals(this.tab, o.tab) && java.util.Objects.equals(this.frame, o.frame) && java.util.Objects.equals(this.setting, o.setting);
    }
public MenuTabElement tab() {
        return this.tab;
    }

    public AbstractFrame frame() {
        return this.frame;
    }

    public Setting setting() {
        return this.setting;
    }
}
