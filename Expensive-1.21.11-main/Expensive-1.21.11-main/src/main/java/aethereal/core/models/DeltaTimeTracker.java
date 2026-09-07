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

import java.util.concurrent.TimeUnit;

public class DeltaTimeTracker {
    public static final double nanosPerSecond = TimeUnit.SECONDS.toNanos(1);
    public long lastTime = System.nanoTime();

    public void clear() {
        this.lastTime = System.nanoTime();
    }

    public float elapsedUnit() {
        long jNanoTime= System.nanoTime();
        double d= (jNanoTime - this.lastTime) / nanosPerSecond;
        this.lastTime = jNanoTime;
        return Math.max(0.0f, Math.min((float) d, 0.05f));
    }
}
