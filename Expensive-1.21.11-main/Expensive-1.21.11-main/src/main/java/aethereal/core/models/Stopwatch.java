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

public class Stopwatch {
    public boolean started;
    public long startTime;
    public long pausedTime;
    public boolean paused;

    public Stopwatch(boolean z) {
        if (z) {
            reset();
        }
    }

    public Stopwatch() {
        reset();
    }

    public void reset() {
        this.startTime = System.nanoTime();
        this.paused = false;
        this.pausedTime = 0L;
        this.started = true;
    }

    public boolean hasElapsed(long j) {
        return hasElapsed(j, TimeUnit.MILLISECONDS);
    }

    public boolean hasElapsed(long j, TimeUnit timeUnit) {
        if (getElapsedTime(timeUnit) < j) {
            return false;
        }
        this.started = false;
        return true;
    }

    public long getElapsedTime(TimeUnit timeUnit) {
        return timeUnit.convert(computeElapsedNanos(), TimeUnit.NANOSECONDS);
    }

    public long computeElapsedNanos() {
        return this.paused ? this.pausedTime - this.startTime : System.nanoTime() - this.startTime;
    }

    public Stopwatch setElapsedTime(long j, TimeUnit timeUnit) {
        this.startTime = System.nanoTime() - timeUnit.toNanos(j);
        return this;
    }

    public void overrideStartTime(long j, TimeUnit timeUnit) {
        this.startTime = timeUnit.toNanos(j);
    }

    public void adjustTime(long j, TimeUnit timeUnit) {
        this.startTime += timeUnit.toNanos(j);
    }

    public void pause() {
        if (this.paused) {
            return;
        }
        this.pausedTime = System.nanoTime();
        this.paused = true;
    }

    public void resume() {
        if (this.paused) {
            this.startTime += System.nanoTime() - this.pausedTime;
            this.paused = false;
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getPausedTime() {
        return this.pausedTime;
    }

    public boolean isPaused() {
        return this.paused;
    }
}
