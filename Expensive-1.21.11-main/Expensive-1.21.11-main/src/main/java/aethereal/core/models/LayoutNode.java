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

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayoutNode {

    @SerializedName("name")
    public String name;

    @SerializedName("type")
    public String type;

    @SerializedName("data")
    public Map<String, Object> data = new HashMap();

    @SerializedName("children")
    public List<LayoutNode> children = new ArrayList();

    public String name() {
        return this.name;
    }

    public String type() {
        return this.type;
    }

    public Map<String, Object> data() {
        return this.data;
    }

    public List<LayoutNode> children() {
        return this.children;
    }

    public LayoutNode name(String str) {
        this.name = str;
        return this;
    }

    public LayoutNode type(String str) {
        this.type = str;
        return this;
    }

    public LayoutNode data(Map<String, Object> map) {
        this.data = map;
        return this;
    }

    public LayoutNode children(List<LayoutNode> list) {
        this.children = list;
        return this;
    }
}
