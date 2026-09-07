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

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public abstract class RotationMode {
    public final String name;

    public Rotation process(Rotation class007Var, Rotation class007Var2) {
        return process(class007Var, class007Var2, null, null);
    }

    public Rotation process(Rotation class007Var, Rotation class007Var2, Vec3d vec3d) {
        return process(class007Var, class007Var2, vec3d, null);
    }

    public abstract Rotation process(Rotation class007Var, Rotation class007Var2, Vec3d vec3d, Entity entity);

    public abstract Vec3d randomValue();

    public RotationMode(String str) {
        this.name = str;
    }
}
