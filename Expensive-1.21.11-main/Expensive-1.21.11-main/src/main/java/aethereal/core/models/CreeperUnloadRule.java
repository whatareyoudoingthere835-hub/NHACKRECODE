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

public class CreeperUnloadRule implements PhaseRule {
    public final CreeperFarmUnloadPhase unloadPhase;

    @Override
    public boolean shouldEnter() {
        if (!this.unloadPhase.tryStartUnload()) {
            return false;
        }
        if (this.unloadPhase.getUnloadTarget() != CreeperFarmUnloadTarget.CHEST) {
        }
        return true;
    }

    @Override
    public TpLootStage getPhase() {
        return TpLootStage.UNLOADING;
    }

    public CreeperUnloadRule(CreeperFarmUnloadPhase class600Var) {
        this.unloadPhase = class600Var;
    }
}
