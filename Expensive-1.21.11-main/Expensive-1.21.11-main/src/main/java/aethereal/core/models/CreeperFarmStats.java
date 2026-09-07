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

public class CreeperFarmStats {
    public long startTime;
    public int kills;
    public int gunpowder;
    public double money;
    public TpLootStage currentPhase;

    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - this.startTime) / 1000;
    }

    public String formattedTime() {
        int uptimeSeconds= (int) getUptimeSeconds();
        return String.format("%02d:%02d", Integer.valueOf(uptimeSeconds / 60), Integer.valueOf(uptimeSeconds % 60));
    }

    public CreeperFarmStats(long j, int i, int i2, double d, TpLootStage class529Var) {
        this.startTime = j;
        this.kills = i;
        this.gunpowder = i2;
        this.money = d;
        this.currentPhase = class529Var;
    }
}
