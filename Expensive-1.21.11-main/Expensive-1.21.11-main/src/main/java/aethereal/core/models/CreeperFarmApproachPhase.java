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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperFarmApproachPhase {
    public void tickApproachPhase(ClientPlayerEntity clientPlayerEntity, List<CreeperEntity> list) {
        if (list.isEmpty()) {
            return;
        }
        CreeperEntity creeperEntity= (CreeperEntity) list.getFirst();
        double dDistanceTo= clientPlayerEntity.distanceTo(creeperEntity);
        if (dDistanceTo >= 4.5d && dDistanceTo > 4.5d) {
            creeperEntity.getEntityPos().add(clientPlayerEntity.getEntityPos().subtract(creeperEntity.getEntityPos()).normalize().multiply(4.5d));
        }
    }
}
