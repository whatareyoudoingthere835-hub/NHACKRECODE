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

import net.minecraft.client.util.InputUtil;

public class InputTypeSwitchMap {
    public static final int[] typeSwitchMap = new int[InputUtil.Type.values().length];

    static {
        try {
            typeSwitchMap[InputUtil.Type.KEYSYM.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            typeSwitchMap[InputUtil.Type.MOUSE.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
    }
}
