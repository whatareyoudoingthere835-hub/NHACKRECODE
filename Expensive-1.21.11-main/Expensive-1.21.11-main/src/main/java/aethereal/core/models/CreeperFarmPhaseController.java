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

import java.util.ArrayList;
import java.util.List;

public class CreeperFarmPhaseController {
    public TpLootStage currentPhase= TpLootStage.APPROACH;
    public final List<PhaseRule> rules = new ArrayList();
    public final CreeperFarmModule module;

    public void tickPhaseRules() {
        for (PhaseRule class530Var : this.rules) {
            if (class530Var.shouldEnter()) {
                setPhase(class530Var.getPhase());
                return;
            }
        }
    }

    public void addRule(PhaseRule class530Var) {
        this.rules.add(class530Var);
    }

    public void setPhase(TpLootStage class529Var) {
        if (this.currentPhase != class529Var) {
            this.currentPhase = class529Var;
            if (this.module.getStatsHandler().getStats() != null) {
                this.module.getStatsHandler().getStats().currentPhase = class529Var;
            }
        }
    }

    public boolean isPhase(TpLootStage class529Var) {
        return this.currentPhase == class529Var;
    }

    public TpLootStage getCurrentPhase() {
        return this.currentPhase;
    }

    public CreeperFarmPhaseController(CreeperFarmModule class597Var) {
        this.module = class597Var;
    }
}
