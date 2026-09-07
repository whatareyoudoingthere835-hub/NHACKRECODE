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

public enum StaffStatus {
    SPEC("Spec", 0xFF55FF55),
    PLAYING("Playing", 0xFFFFFF55),
    VANISH("Vanish", 0xFFFF5555);

    private final String status;
    private final int color;

    StaffStatus(String status, int color) {
        this.status = status;
        this.color = color;
    }

    public String getStatus() {
        return this.status;
    }

    public int getColor() {
        return this.color;
    }
}
