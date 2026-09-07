package aethereal.core.types;
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

public enum InventoryScope {
    ALL(0, 46),
    HOTBAR(0, 8),
    INVENTORY(9, 35),
    OFFHAND(40, 40),
    ARMOR(36, 39);

    public final int start;
    public final int end;

    public int start() {
        return this.start;
    }

    public int end() {
        return this.end;
    }

    InventoryScope(int i, int i2) {
        this.start = i;
        this.end = i2;
    }
}
