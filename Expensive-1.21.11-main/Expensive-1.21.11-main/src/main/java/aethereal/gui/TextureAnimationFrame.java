package aethereal.gui;
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


public final class TextureAnimationFrame {
    public final GifFrame frame;
    public final GlTextureObject texture;

    public TextureAnimationFrame(GifFrame class333Var, GlTextureObject class073Var) {
        this.frame = class333Var;
        this.texture = class073Var;
    }

    public int delay() {
        return this.frame.delay();
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "frame=" + this.frame + ", " + "texture=" + this.texture + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.frame, this.texture);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TextureAnimationFrame)) return false;
        TextureAnimationFrame o= (TextureAnimationFrame) obj;
        return java.util.Objects.equals(this.frame, o.frame) && java.util.Objects.equals(this.texture, o.texture);
    }
public GifFrame frame() {
        return this.frame;
    }

    public GlTextureObject texture() {
        return this.texture;
    }
}
