package aethereal.system.config;
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

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ModuleConfigData {

    @SerializedName("name")
    public String name;

    @SerializedName("enabled")
    public boolean enabled;

    @SerializedName("bindType")
    public BindMode bindType;

    @SerializedName("keys")
    public List<Integer> keys = new ArrayList();

    @SerializedName("settings")
    public List<LayoutNode> settings = new ArrayList();

    public String name() {
        return this.name;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public List<Integer> keys() {
        return this.keys;
    }

    public BindMode bindType() {
        return this.bindType;
    }

    public List<LayoutNode> settings() {
        return this.settings;
    }

    public ModuleConfigData name(String str) {
        this.name = str;
        return this;
    }

    public ModuleConfigData enabled(boolean z) {
        this.enabled = z;
        return this;
    }

    public ModuleConfigData keys(List<Integer> list) {
        this.keys = list;
        return this;
    }

    public ModuleConfigData bindType(BindMode class660Var) {
        this.bindType = class660Var;
        return this;
    }

    public ModuleConfigData settings(List<LayoutNode> list) {
        this.settings = list;
        return this;
    }
}
