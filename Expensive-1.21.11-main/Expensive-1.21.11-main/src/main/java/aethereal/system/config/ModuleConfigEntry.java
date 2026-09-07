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

public class ModuleConfigEntry {

    @SerializedName("name")
    public String name;

    @SerializedName("x")
    public float x;

    @SerializedName("y")
    public float y;

    @SerializedName("settings")
    public List<LayoutNode> settings = new ArrayList();

    public String name() {
        return this.name;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public List<LayoutNode> settings() {
        return this.settings;
    }

    public ModuleConfigEntry name(String str) {
        this.name = str;
        return this;
    }

    public ModuleConfigEntry x(float f) {
        this.x = f;
        return this;
    }

    public ModuleConfigEntry y(float f) {
        this.y = f;
        return this;
    }

    public ModuleConfigEntry settings(List<LayoutNode> list) {
        this.settings = list;
        return this;
    }
}
