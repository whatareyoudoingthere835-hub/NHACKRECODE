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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Setting {
    public final Translation name;
    public final Translation description;
    public Supplier<Boolean> visible;

    public Setting(Translation class254Var) {
        this(class254Var, null);
    }

    public Setting setVisible(Supplier<Boolean> supplier) {
        this.visible = supplier;
        return this;
    }

    public Map<String, Object> toSerializedData() {
        return Map.of();
    }

    public void loadSerializedData(Map<String, Object> map) {
    }

    public List<Setting> getSerializableChildren() {
        return List.of();
    }

    public Translation getName() {
        return this.name;
    }

    public Translation getDescription() {
        return this.description;
    }

    public Supplier<Boolean> getVisible() {
        return this.visible;
    }

    public Setting(Translation class254Var, Translation class254Var2) {
        this.name = class254Var;
        this.description = class254Var2;
    }
}
