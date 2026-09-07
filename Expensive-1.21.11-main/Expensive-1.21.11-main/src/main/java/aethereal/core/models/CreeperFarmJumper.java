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

import net.minecraft.client.network.ClientPlayerEntity;

public class CreeperFarmJumper {
    public final Mc mc = Mc.INSTANCE;
    public long lastJumpTime = 0;

    public void tick(ClientPlayerEntity clientPlayerEntity) {
        if (!clientPlayerEntity.isOnGround() || this.mc.getGameOptions().jumpKey.isPressed() || clientPlayerEntity.input.getMovementInput().y <= 0.0f || clientPlayerEntity.horizontalCollision) {
            return;
        }
        long time= this.mc.getWorld().getTime();
        if (time - this.lastJumpTime > 2) {
            this.lastJumpTime = time;
            clientPlayerEntity.jump();
        }
    }

    public void reset() {
        this.lastJumpTime = 0L;
    }
}
