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

import java.util.Set;

public class TargetSorters {
    public static final DistanceSorter DISTANCE = new DistanceSorter();
    public static final FovSorter FOV = new FovSorter();
    public static final HealthSorter HEALTH = new HealthSorter();

    public static TargetSorter of(Set<TargetSortMode> set) {
        if (set == null || set.isEmpty() || set.size() == TargetSortMode.values().length) {
            return FOV.then(HEALTH).then(DISTANCE);
        }
        TargetSorter class004VarThen= null;
        if (set.contains(TargetSortMode.BY_FOV)) {
            class004VarThen = FOV;
        }
        if (set.contains(TargetSortMode.BY_HEALTH)) {
            class004VarThen = class004VarThen == null ? HEALTH : class004VarThen.then(HEALTH);
        }
        if (set.contains(TargetSortMode.BY_DISTANCE)) {
            class004VarThen = class004VarThen == null ? DISTANCE : class004VarThen.then(DISTANCE);
        }
        return class004VarThen == null ? FOV.then(HEALTH).then(DISTANCE) : class004VarThen;
    }
}
