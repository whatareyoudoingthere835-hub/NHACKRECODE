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

import java.util.List;

public final class BlockUpdateEvent implements Event {
    public final List<BlockUpdateEntry> list;
    public final BlockUpdateType type;

    public BlockUpdateEvent(List<BlockUpdateEntry> list, BlockUpdateType class191Var) {
        this.list = list;
        this.type = class191Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "list=" + this.list + ", " + "type=" + this.type + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.list, this.type);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockUpdateEvent)) return false;
        BlockUpdateEvent o= (BlockUpdateEvent) obj;
        return java.util.Objects.equals(this.list, o.list) && java.util.Objects.equals(this.type, o.type);
    }
public List<BlockUpdateEntry> list() {
        return this.list;
    }

    public BlockUpdateType type() {
        return this.type;
    }
}
