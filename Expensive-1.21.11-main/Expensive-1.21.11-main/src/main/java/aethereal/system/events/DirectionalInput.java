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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;

public class DirectionalInput {
    public static final DirectionalInput NONE = new DirectionalInput(false, false, false, false);

    private final boolean forward;
    private final boolean backward;
    private final boolean left;
    private final boolean right;

    public DirectionalInput() {
        this(false, false, false, false);
    }

    public DirectionalInput(boolean z, boolean z2, boolean z3, boolean z4) {
        this.forward = z;
        this.backward = z2;
        this.left = z3;
        this.right = z4;
    }

    public boolean forward() {
        return this.forward;
    }

    public boolean backward() {
        return this.backward;
    }

    public boolean left() {
        return this.left;
    }

    public boolean right() {
        return this.right;
    }

    public boolean isMoving() {
        return this.forward || this.backward || this.left || this.right;
    }

    public static DirectionalInput fromInput(PlayerInput playerInput) {
        return new DirectionalInput(playerInput.forward(), playerInput.backward(), playerInput.left(), playerInput.right());
    }

    public static DirectionalInput fromPlayerInput(ClientPlayerEntity clientPlayerEntity) {
        return fromInput(clientPlayerEntity.input.playerInput);
    }

    public static DirectionalInput fromGameOptions(GameOptions gameOptions) {
        return new DirectionalInput(gameOptions.forwardKey.isPressed(), gameOptions.backKey.isPressed(), gameOptions.leftKey.isPressed(), gameOptions.rightKey.isPressed());
    }

    public static DirectionalInput fromMovementForwardAndSideways(float f, float f2) {
        return new DirectionalInput(f > 0.0f, f < 0.0f, f2 > 0.0f, f2 < 0.0f);
    }
}
