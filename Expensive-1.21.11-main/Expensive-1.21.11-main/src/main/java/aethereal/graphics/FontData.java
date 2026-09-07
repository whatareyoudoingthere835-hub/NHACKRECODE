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
import java.util.List;

public class FontData {

    @SerializedName("atlas")
    public SpriteRenderConfig atlas;

    @SerializedName("metrics")
    public FontMetrics metrics;

    @SerializedName("glyphs")
    public List<GlyphData> glyphs;

    @SerializedName("kerning")
    public List<KerningPair> kernings;

    public SpriteRenderConfig atlas() {
        return this.atlas;
    }

    public FontMetrics metrics() {
        return this.metrics;
    }

    public List<GlyphData> glyphs() {
        return this.glyphs;
    }

    public List<KerningPair> kernings() {
        return this.kernings;
    }
}
