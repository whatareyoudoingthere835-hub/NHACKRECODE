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

import com.google.gson.annotations.SerializedName;

public class FontMetrics {
    @SerializedName("emSize")
    public float emSize;
    @SerializedName("lineHeight")
    public float lineHeight;
    @SerializedName("ascender")
    public float ascender;
    @SerializedName("descender")
    public float descender;
    @SerializedName("underlineY")
    public float underlineY;
    @SerializedName("underlineThickness")
    public float underlineThickness;

    public float ascent() {
        return this.ascender;
    }

    public float descent() {
        return this.descender;
    }

    public float lineHeight() {
        return this.lineHeight;
    }
}
