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
import java.util.List;

public class HotkeyEntry {
    public final String name;
    public final String moduleName;
    public final List<Integer> keys;
    public final ToggleAnimator anim;

    public HotkeyEntry(String str, List<Integer> list) {
        this.keys = new ArrayList();
        this.anim = ToggleAnimator.times(2, 80);
        this.moduleName = null;
        this.name = str;
        if (list != null) {
            this.keys.addAll(list);
        }
    }

    public HotkeyEntry(Module class605Var, String str, List<Integer> list) {
        this.keys = new ArrayList();
        this.anim = ToggleAnimator.times(2, 80);
        this.moduleName = class605Var != null ? class605Var.getName() : null;
        this.name = str;
        if (list != null) {
            this.keys.addAll(list);
        }
    }

    public boolean hasModule() {
        return this.moduleName != null;
    }

    public String name() {
        return this.name;
    }

    public String moduleName() {
        return this.moduleName;
    }

    public List<Integer> keys() {
        return this.keys;
    }

    public ToggleAnimator anim() {
        return this.anim;
    }
}
