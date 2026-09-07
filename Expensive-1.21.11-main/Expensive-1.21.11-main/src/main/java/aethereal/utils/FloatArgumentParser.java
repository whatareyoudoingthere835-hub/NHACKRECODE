package aethereal.utils;
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

import java.util.Collections;
import java.util.List;

public class FloatArgumentParser implements ArgumentParser<Float> {
    public final Float min;
    public final Float max;

    public FloatArgumentParser() {
        this(null, null);
    }

    public FloatArgumentParser(Float f, Float f2) {
        this.min = f;
        this.max = f2;
    }

    @Override
    public Float parse(String str) throws TranslatedException {
        try {
            float f= Float.parseFloat(str);
            if (this.min != null && f < this.min.floatValue()) {
                throw new TranslatedException(Translation.clearText(Lang.TYPE_MIN_VALUE.effective().replace("{min}", String.valueOf(this.min))));
            }
            if (this.max == null || f <= this.max.floatValue()) {
                return Float.valueOf(f);
            }
            throw new TranslatedException(Translation.clearText(Lang.TYPE_MAX_VALUE.effective().replace("{max}", String.valueOf(this.max))));
        } catch (NumberFormatException e) {
            throw new TranslatedException(Translation.clearText(Lang.TYPE_INVALID_FLOAT.effective().replace("{input}", str)));
        }
    }

    @Override
    public List<String> getSuggestions(String str) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "float";
    }
}
