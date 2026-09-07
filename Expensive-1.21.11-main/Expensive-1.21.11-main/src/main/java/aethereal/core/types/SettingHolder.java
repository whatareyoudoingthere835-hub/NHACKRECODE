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

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;

public class SettingHolder {
    public final List<Setting> settings = Lists.newArrayList();

    public void addSettings(Setting... class661VarArr) {
        this.settings.addAll(Arrays.asList(class661VarArr));
    }

    public Setting get(String str) {
        return this.settings.stream().filter(class661Var -> {
            return class661Var.getName().effective().equalsIgnoreCase(str);
        }).findFirst().orElse(null);
    }

    public List<Setting> getSettings() {
        return this.settings;
    }
}
