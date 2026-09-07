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

import java.awt.image.BufferedImage;

public final class GifFrame {
    public final BufferedImage image;
    public final int delay;

    public GifFrame(BufferedImage bufferedImage, int i) {
        this.image = bufferedImage;
        this.delay = i;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "image=" + this.image + ", " + "delay=" + this.delay + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.image, this.delay);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GifFrame)) return false;
        GifFrame o= (GifFrame) obj;
        return java.util.Objects.equals(this.image, o.image) && java.util.Objects.equals(this.delay, o.delay);
    }
public BufferedImage image() {
        return this.image;
    }

    public int delay() {
        return this.delay;
    }
}
