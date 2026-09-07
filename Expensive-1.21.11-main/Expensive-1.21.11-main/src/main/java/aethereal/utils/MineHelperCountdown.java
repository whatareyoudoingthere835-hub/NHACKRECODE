package aethereal.utils;
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


public final class MineHelperCountdown {
    public final String nextType;
    public final int initialSeconds;
    public final long startTimeMillis;

    public MineHelperCountdown(String str, int i, long j) {
        this.nextType = str;
        this.initialSeconds = i;
        this.startTimeMillis = j;
    }

    public int getSecondsLeft() {
        return Math.max(this.initialSeconds - ((int) ((System.currentTimeMillis() - this.startTimeMillis) / 1000)), 0);
    }

    public boolean isExpired() {
        return getSecondsLeft() <= 0;
    }

    public String formattedTime() {
        int secondsLeft= getSecondsLeft();
        return String.format("%02d:%02d", Integer.valueOf(secondsLeft / 60), Integer.valueOf(secondsLeft % 60));
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "nextType=" + this.nextType + ", " + "initialSeconds=" + this.initialSeconds + ", " + "startTimeMillis=" + this.startTimeMillis + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.nextType, this.initialSeconds, this.startTimeMillis);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MineHelperCountdown)) return false;
        MineHelperCountdown o= (MineHelperCountdown) obj;
        return java.util.Objects.equals(this.nextType, o.nextType) && java.util.Objects.equals(this.initialSeconds, o.initialSeconds) && java.util.Objects.equals(this.startTimeMillis, o.startTimeMillis);
    }
public String nextType() {
        return this.nextType;
    }

    public int initialSeconds() {
        return this.initialSeconds;
    }

    public long startTimeMillis() {
        return this.startTimeMillis;
    }
}
