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

public enum NotificationPriority {
    CRITICAL_FOR_USER_PROTECTION(60),
    CRUCIAL_FOR_PLAYER_LIFE(40),
    HIGH_IMPORTANCE_3(35),
    HIGH_IMPORTANCE_2(30),
    HIGH_IMPORTANCE_1(20),
    STANDARD(0),
    LOW_PRIORITY(-20);

    public final int priority;

    public int getPriority() {
        return this.priority;
    }

    NotificationPriority(int i) {
        this.priority = i;
    }
}
