package aethereal.system.events;
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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;

public class MovementInputState {
    public PlayerInput playerInput;
    public float movementForward;
    public float movementSideways;
    public boolean forceSafeWalk;

    public MovementInputState() {
    }

    public MovementInputState(PlayerInput playerInput2) {
        this.playerInput = playerInput2;
    }

    public static MovementInputState fromClientPlayer(PlayerInput playerInput2) {
        return new MovementInputState(playerInput2);
    }

    public static MovementInputState guessInput(PlayerEntity playerEntity) {
        MovementInputState result= new MovementInputState();
        Vec3d velocity= playerEntity.getVelocity();
        result.movementForward = (float) velocity.z;
        result.movementSideways = (float) velocity.x;
        result.playerInput = PlayerInput.DEFAULT;
        return result;
    }

    public void update() {
        if (this.playerInput == null) return;
        this.movementForward = this.playerInput.forward() ? 1.0f : (this.playerInput.backward() ? -1.0f : 0.0f);
        this.movementSideways = this.playerInput.left() ? 1.0f : (this.playerInput.right() ? -1.0f : 0.0f);
    }
}
