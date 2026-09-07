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

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.util.math.BlockPos;

public class BlockRegionIterator implements Iterator<BlockPos> {
    public int cursorX;
    public int cursorY;
    public int cursorZ;
    final BlockRegion region;

    public BlockRegionIterator(BlockRegion class306Var) {
        this.region = class306Var;
        this.cursorX = this.region.min.getX();
        this.cursorY = this.region.min.getY();
        this.cursorZ = this.region.min.getZ();
    }

    @Override
    public boolean hasNext() {
        return this.cursorX <= this.region.max.getX() && this.cursorY <= this.region.max.getY() && this.cursorZ <= this.region.max.getZ();
    }

    @Override
    public BlockPos next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        BlockPos blockPos= new BlockPos(this.cursorX, this.cursorY, this.cursorZ);
        if (this.cursorX < this.region.max.getX()) {
            this.cursorX++;
        } else {
            this.cursorX = this.region.min.getX();
            if (this.cursorY < this.region.max.getY()) {
                this.cursorY++;
            } else {
                this.cursorY = this.region.min.getY();
                if (this.cursorZ < this.region.max.getZ()) {
                    this.cursorZ++;
                } else {
                    this.cursorZ = this.region.max.getZ() + 1;
                }
            }
        }
        return blockPos;
    }
}
