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

import java.nio.DoubleBuffer;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

public class InputInterceptor {
    public long windowHandle;
    public boolean interceptCursorfScreenNotPresent;
    public boolean interceptKeyboardIfScreenPresent;
    public boolean interceptKeyboard;
    public boolean interceptSetCursorModeAnyway;
    public boolean keysNeedRefresh = false;

    public void begin(long j) {
        this.windowHandle = j;
        this.interceptCursorfScreenNotPresent = false;
        this.interceptKeyboardIfScreenPresent = false;
        this.interceptSetCursorModeAnyway = false;
        this.interceptKeyboard = false;
    }

    public void interceptSetCursorModeAnyway(boolean z) {
        this.interceptSetCursorModeAnyway = z;
    }

    public void interceptCursorfScreenNotPresent(boolean z) {
        this.interceptCursorfScreenNotPresent = z;
    }

    public void interceptKeyboardIfScreenPresent(boolean z) {
        this.interceptKeyboardIfScreenPresent = z;
    }

    public void interceptKeyboard(boolean z) {
        this.interceptKeyboard = z;
        for (KeyBinding keyBinding : MovementInputHelper.getMovementKeys(false, true)) {
            keyBinding.setPressed(false);
            this.keysNeedRefresh = true;
        }
    }

    public void tickRefreshKeysIfNeeded() {
        if (this.keysNeedRefresh && !this.interceptKeyboard) {
            KeyBinding.updatePressedStates();
            this.keysNeedRefresh = false;
        }
    }

    public boolean cursorVisible() {
        return GLFW.glfwGetInputMode(this.windowHandle, 208897) == 212993;
    }

    public void mousePosition(PixelPoint class708Var) {
        GLFW.glfwSetCursorPos(this.windowHandle, class708Var.x(), class708Var.y());
    }

    public PixelPoint determineMousePosition() {
        MemoryStack memoryStackStackPush= MemoryStack.stackPush();
        try {
            DoubleBuffer doubleBufferMallocDouble= memoryStackStackPush.mallocDouble(1);
            DoubleBuffer doubleBufferMallocDouble2= memoryStackStackPush.mallocDouble(1);
            GLFW.glfwGetCursorPos(this.windowHandle, doubleBufferMallocDouble, doubleBufferMallocDouble2);
            PixelPoint class708Var= new PixelPoint(doubleBufferMallocDouble.get(), doubleBufferMallocDouble2.get());
            if (memoryStackStackPush != null) {
                memoryStackStackPush.close();
            }
            return class708Var;
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

    public void showCursor() {
        cursorInputMode(212993);
    }

    public void cursorInputMode(int i) {
        if (cursorInputMode() != i) {
            GLFW.glfwSetInputMode(this.windowHandle, 208897, i);
        }
    }

    public int cursorInputMode() {
        return GLFW.glfwGetInputMode(this.windowHandle, 208897);
    }

    public boolean interceptCursorfScreenNotPresent() {
        return this.interceptCursorfScreenNotPresent;
    }

    public boolean interceptKeyboardIfScreenPresent() {
        return this.interceptKeyboardIfScreenPresent;
    }

    public boolean interceptKeyboard() {
        return this.interceptKeyboard;
    }

    public boolean interceptSetCursorModeAnyway() {
        return this.interceptSetCursorModeAnyway;
    }
}
