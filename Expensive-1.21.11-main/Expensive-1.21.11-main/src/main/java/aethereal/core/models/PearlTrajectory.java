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

import java.util.List;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

public final class PearlTrajectory {
    public final EnderPearlEntity pearl;
    public final List<TrajectoryPoint> steps;

    public PearlTrajectory(EnderPearlEntity enderPearlEntity, List<TrajectoryPoint> list) {
        this.pearl = enderPearlEntity;
        this.steps = list;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "pearl=" + this.pearl + ", " + "steps=" + this.steps + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.pearl, this.steps);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PearlTrajectory)) return false;
        PearlTrajectory o= (PearlTrajectory) obj;
        return java.util.Objects.equals(this.pearl, o.pearl) && java.util.Objects.equals(this.steps, o.steps);
    }
public EnderPearlEntity pearl() {
        return this.pearl;
    }

    public List<TrajectoryPoint> steps() {
        return this.steps;
    }
}
