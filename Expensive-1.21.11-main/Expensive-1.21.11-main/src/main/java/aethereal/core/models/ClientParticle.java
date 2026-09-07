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

public class ClientParticle {
    public double prevX;
    public double prevY;
    public double prevZ;
    public double x;
    public double y;
    public double z;
    public double vx;
    public double vy;
    public double vz;
    public float size;
    public int lifetime;

    public ClientParticle(double d, double d2, double d3, double d4, double d5, double d6, int i) {
        this.x = d;
        this.prevX = d;
        this.y = d2;
        this.prevY = d2;
        this.z = d3;
        this.prevZ = d3;
        this.vx = d4;
        this.vy = d5;
        this.vz = d6;
        this.lifetime = i;
    }

    public float fadeAlpha(float f) {
        float f2= f / this.lifetime;
        if (f2 < 0.2f) {
            return f2 / 0.2f;
        }
        if (f2 > 0.8f) {
            return (1.0f - f2) / 0.2f;
        }
        return 1.0f;
    }
}
