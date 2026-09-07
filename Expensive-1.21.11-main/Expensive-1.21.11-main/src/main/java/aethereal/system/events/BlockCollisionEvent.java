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

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockCollisionEvent implements Event {
    public BlockState state;

    public BlockPos pos;

    public BlockState getState() {
        return this.state;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setState(BlockState blockState) {
        this.state = blockState;
    }

    public void setPos(BlockPos blockPos) {
        this.pos = blockPos;
    }

    public BlockCollisionEvent(BlockState blockState, BlockPos blockPos) {
        this.state = blockState;
        this.pos = blockPos;
    }
}
