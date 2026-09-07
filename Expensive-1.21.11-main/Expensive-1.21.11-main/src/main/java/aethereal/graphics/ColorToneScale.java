package aethereal.graphics;
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

import java.util.Map;

public final class ColorToneScale {
    public final Map<Integer, ColorValue> all;

    public ColorToneScale(Map<Integer, ColorValue> map) {
        this.all = map;
    }

    public ColorValue tone(int i) {
        ColorValue class761Var= this.all.get(Integer.valueOf(i));
        if (class761Var == null) {
            throw new IllegalArgumentException("Tone " + i + " does not exist in scale");
        }
        return class761Var;
    }

    public boolean has(int i) {
        return this.all.containsKey(Integer.valueOf(i));
    }

    public Map<Integer, ColorValue> all() {
        return this.all;
    }
}
