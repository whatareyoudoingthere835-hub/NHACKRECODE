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

import java.util.function.Supplier;

public class ButtonSetting extends Setting {
    public Runnable runnable;
    public Translation buttonName;

    public ButtonSetting(Translation class254Var, Translation class254Var2) {
        super(class254Var, class254Var2);
    }

    public ButtonSetting(Translation class254Var) {
        this(class254Var, null);
    }

    public ButtonSetting visible(Supplier<Boolean> supplier) {
        setVisible(supplier);
        return this;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public Translation getButtonName() {
        return this.buttonName;
    }

    public ButtonSetting setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public ButtonSetting setButtonName(Translation class254Var) {
        this.buttonName = class254Var;
        return this;
    }
}
