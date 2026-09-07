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

public class KeybindColorsBuilder {
    private int outlineColor;
    private int backgroundColor;
    private int textColor;
    private int textEmptyColor;

    public KeybindColorsBuilder outline(int i) {
        this.outlineColor = i;
        return this;
    }

    public KeybindColorsBuilder background(int i) {
        this.backgroundColor = i;
        return this;
    }

    public KeybindColorsBuilder text(int i) {
        this.textColor = i;
        return this;
    }

    public KeybindColorsBuilder textEmpty(int i) {
        this.textEmptyColor = i;
        return this;
    }

    public KeybindColors build() {
        return new KeybindColors(this.outlineColor, this.backgroundColor, this.textColor, this.textEmptyColor);
    }
}
