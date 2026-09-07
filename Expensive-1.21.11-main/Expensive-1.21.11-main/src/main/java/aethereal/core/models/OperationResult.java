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


public final class OperationResult {
    public final boolean successfully;
    public final String errorMessage;

    public OperationResult(boolean z, String str) {
        this.successfully = z;
        this.errorMessage = str;
    }

    public static OperationResult success() {
        return new OperationResult(true, "");
    }

    public static OperationResult failure(String str) {
        return new OperationResult(false, str);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "successfully=" + this.successfully + ", " + "errorMessage=" + this.errorMessage + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.successfully, this.errorMessage);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OperationResult)) return false;
        OperationResult o= (OperationResult) obj;
        return java.util.Objects.equals(this.successfully, o.successfully) && java.util.Objects.equals(this.errorMessage, o.errorMessage);
    }
public boolean successfully() {
        return this.successfully;
    }

    public String errorMessage() {
        return this.errorMessage;
    }
}
