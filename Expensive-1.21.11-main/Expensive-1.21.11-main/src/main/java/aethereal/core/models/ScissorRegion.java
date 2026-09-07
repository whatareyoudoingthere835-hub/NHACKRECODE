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

import org.lwjgl.opengl.GL11;

public class ScissorRegion {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final float guiX;
    public final float guiY;
    public final float guiWidth;
    public final float guiHeight;

    public ScissorRegion() {
        this(0, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public ScissorRegion(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
        this.x = i;
        this.y = i2;
        this.width = i3;
        this.height = i4;
        this.guiX = f;
        this.guiY = f2;
        this.guiWidth = f3;
        this.guiHeight = f4;
    }

    public void apply() {
        GL11.glScissor(this.x, this.y, Math.max(0, this.width), Math.max(0, this.height));
    }
}
