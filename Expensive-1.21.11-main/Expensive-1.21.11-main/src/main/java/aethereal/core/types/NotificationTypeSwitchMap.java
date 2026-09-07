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

public class NotificationTypeSwitchMap {
    public static final int[] ordinalToCase = new int[NotificationType.values().length];

    static {
        try {
            ordinalToCase[NotificationType.INFO.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            ordinalToCase[NotificationType.SUCCESS.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            ordinalToCase[NotificationType.MODULE_ENABLED.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            ordinalToCase[NotificationType.WARNING.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            ordinalToCase[NotificationType.ERROR.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            ordinalToCase[NotificationType.MODULE_DISABLED.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        try {
            ordinalToCase[NotificationType.EVENT.ordinal()] = 7;
        } catch (NoSuchFieldError e7) {
        }
    }
}
