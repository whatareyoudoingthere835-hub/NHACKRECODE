package aethereal.gui;
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

import net.minecraft.util.Util;

public final class FrameTimeUtil {
    public static long lastFrameNano = -1;

    public static float frameDt() {
        long measuringTimeNano= Util.getMeasuringTimeNano();
        if (lastFrameNano < 0) {
            lastFrameNano = measuringTimeNano;
            return 0.0f;
        }
        long j= measuringTimeNano - lastFrameNano;
        lastFrameNano = measuringTimeNano;
        return FastMathUtils.clamp(j / 1.0E9f, 0.0f, 0.1f);
    }
}
