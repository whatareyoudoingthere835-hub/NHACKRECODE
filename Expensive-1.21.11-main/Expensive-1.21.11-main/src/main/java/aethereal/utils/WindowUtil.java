package aethereal.utils;
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

import java.nio.FloatBuffer;
import java.util.Locale;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

public final class WindowUtil {
    public static final boolean mac = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");

    public WindowUtil() {
    }

    public static boolean isMac() {
        return mac;
    }

    public static float getWindowContentScale(long j) {
        if (j == 0) {
            return 1.0f;
        }
        MemoryStack memoryStackStackPush= MemoryStack.stackPush();
        try {
            FloatBuffer floatBufferMallocFloat= memoryStackStackPush.mallocFloat(1);
            FloatBuffer floatBufferMallocFloat2= memoryStackStackPush.mallocFloat(1);
            GLFW.glfwGetWindowContentScale(j, floatBufferMallocFloat, floatBufferMallocFloat2);
            float fMax= Math.max(floatBufferMallocFloat.get(0), floatBufferMallocFloat2.get(0));
            if (Float.isNaN(fMax) || fMax <= 0.0f) {
                if (memoryStackStackPush != null) {
                    memoryStackStackPush.close();
                }
                return 1.0f;
            }
            if (memoryStackStackPush != null) {
                memoryStackStackPush.close();
            }
            return fMax;
        } catch (Throwable th) {
            if (memoryStackStackPush != null) {
                try {
                    memoryStackStackPush.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
