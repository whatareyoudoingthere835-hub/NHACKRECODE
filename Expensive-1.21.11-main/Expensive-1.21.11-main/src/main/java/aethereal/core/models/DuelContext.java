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


public final class DuelContext {
    public final MultiSelectSetting<DuelKitType> kits;
    public final NumberSetting nextDuelDelay;
    public final AutoDuelArmorType armorType;

    public final AutoDuelOffhandItem offhandItem;

    public final ModuleDeactivateCallback callback;

    public DuelContext(MultiSelectSetting<DuelKitType> class671Var, NumberSetting class613Var, AutoDuelArmorType class440Var, AutoDuelOffhandItem class439Var, ModuleDeactivateCallback class443Var) {
        this.kits = class671Var;
        this.nextDuelDelay = class613Var;
        this.armorType = class440Var;
        this.offhandItem = class439Var;
        this.callback = class443Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "kits=" + this.kits + ", " + "nextDuelDelay=" + this.nextDuelDelay + ", " + "armorType=" + this.armorType + ", " + "offhandItem=" + this.offhandItem + ", " + "callback=" + this.callback + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.kits, this.nextDuelDelay, this.armorType, this.offhandItem, this.callback);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DuelContext)) return false;
        DuelContext o= (DuelContext) obj;
        return java.util.Objects.equals(this.kits, o.kits) && java.util.Objects.equals(this.nextDuelDelay, o.nextDuelDelay) && java.util.Objects.equals(this.armorType, o.armorType) && java.util.Objects.equals(this.offhandItem, o.offhandItem) && java.util.Objects.equals(this.callback, o.callback);
    }
public MultiSelectSetting<DuelKitType> kits() {
        return this.kits;
    }

    public NumberSetting nextDuelDelay() {
        return this.nextDuelDelay;
    }

    public AutoDuelArmorType armorType() {
        return this.armorType;
    }

    public AutoDuelOffhandItem offhandItem() {
        return this.offhandItem;
    }

    public ModuleDeactivateCallback callback() {
        return this.callback;
    }
}
