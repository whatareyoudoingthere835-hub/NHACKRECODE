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

import java.util.function.BooleanSupplier;

public class DelayStep implements Comparable<DelayStep> {
    private final int delay;
    private final PerformAction action;
    private final BooleanSupplier condition;
    private final int id;

    public DelayStep(int delay, PerformAction action, BooleanSupplier condition, int id) {
        this.delay = delay;
        this.action = action;
        this.condition = condition;
        this.id = id;
    }

    @Override
    public int compareTo(DelayStep o) {
        return Integer.compare(this.delay, o.delay);
    }

    public PerformAction action() {
        return this.action;
    }

    public BooleanSupplier condition() {
        return this.condition;
    }

    public int delay() {
        return this.delay;
    }
}
