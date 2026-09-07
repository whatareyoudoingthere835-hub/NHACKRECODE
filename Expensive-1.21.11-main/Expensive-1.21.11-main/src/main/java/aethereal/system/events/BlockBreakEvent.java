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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockBreakEvent implements Event {
    public BlockPos pos;

    public Direction direction;

    public BlockBreakStage stage;

    public boolean isPost() {
        return this.stage == BlockBreakStage.POST;
    }

    public boolean isPre() {
        return this.stage == BlockBreakStage.PRE;
    }

    public BlockBreakEvent(BlockPos blockPos, Direction direction, BlockBreakStage class242Var) {
        this.pos = blockPos;
        this.direction = direction;
        this.stage = class242Var;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public BlockBreakStage getStage() {
        return this.stage;
    }
}
