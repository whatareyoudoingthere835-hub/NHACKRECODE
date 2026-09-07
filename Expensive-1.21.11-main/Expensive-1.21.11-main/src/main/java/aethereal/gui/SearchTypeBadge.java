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


public final class SearchTypeBadge {
    public final String text;
    public final GlTextureObject texture;

    public SearchTypeBadge(String str, GlTextureObject class073Var) {
        this.text = str;
        this.texture = class073Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "text=" + this.text + ", " + "texture=" + this.texture + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.text, this.texture);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SearchTypeBadge)) return false;
        SearchTypeBadge o= (SearchTypeBadge) obj;
        return java.util.Objects.equals(this.text, o.text) && java.util.Objects.equals(this.texture, o.texture);
    }
public String text() {
        return this.text;
    }

    public GlTextureObject texture() {
        return this.texture;
    }
}
