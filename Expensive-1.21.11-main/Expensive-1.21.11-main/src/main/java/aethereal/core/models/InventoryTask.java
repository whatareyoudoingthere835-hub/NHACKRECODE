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

import java.util.function.Predicate;

public class InventoryTask {
    private final Predicate predicate;
    private final SlotSearchResult2 result;
    private final boolean z;
    private final boolean needStop;
    private final boolean z3;
    private Rotation rotation = null;

    private InventoryTask(Predicate predicate, SlotSearchResult2 result, boolean z, boolean needStop, boolean z3) {
        this.predicate = predicate;
        this.result = result;
        this.z = z;
        this.needStop = needStop;
        this.z3 = z3;
    }

    public static InventoryTask create(Predicate predicate, SlotSearchResult2 class329Var, boolean z, boolean z2, boolean z3) {
        return new InventoryTask(predicate, class329Var, z, z2, z3);
    }

    public boolean needStop() {
        return this.needStop;
    }

    public SlotSearchResult2 result() {
        return this.result;
    }

    public Rotation rotation() {
        return this.rotation != null ? this.rotation : PlayerRotationManager.INSTANCE.getCurrentRotation();
    }

    public InventoryTask withRotation(Rotation class007Var) {
        this.rotation = class007Var;
        return this;
    }
}
