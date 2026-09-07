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

public final class ThemeFilterIndicator {
    public final ConfigFilter filter;
    public final Translation translation;
    public final String directLabel;
    public final ToggleAnimator animation;

    public ThemeFilterIndicator(ConfigFilter filter, Translation translation) {
        this(filter, translation, new ToggleAnimator(250, Easings.EASE_IN_OUT_CUBIC));
    }

    public ThemeFilterIndicator(ConfigFilter filter, Translation translation, ToggleAnimator animation) {
        this.filter = filter;
        this.translation = translation;
        this.directLabel = null;
        this.animation = animation;
    }

    public ThemeFilterIndicator(ConfigFilter filter, String str) {
        this(filter, str, new ToggleAnimator(250, Easings.EASE_IN_OUT_CUBIC));
    }

    public ThemeFilterIndicator(ConfigFilter filter, String str, ToggleAnimator animation) {
        this.filter = filter;
        this.translation = null;
        this.directLabel = str;
        this.animation = animation;
    }

    public ConfigFilter filter() {
        return this.filter;
    }

    public String label() {
        return this.translation != null ? this.translation.effective() : (this.directLabel != null ? this.directLabel : "");
    }

    public ToggleAnimator animation() {
        return this.animation;
    }
}
