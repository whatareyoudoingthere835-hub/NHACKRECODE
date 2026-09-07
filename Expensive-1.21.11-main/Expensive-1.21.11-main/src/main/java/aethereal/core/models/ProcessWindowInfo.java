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

public final class ProcessWindowInfo {
    public final Object hWnd;
    public final String className;
    public final String windowText;
    public final String processName;
    public final int pid;

    public ProcessWindowInfo(Object hwnd, String str, String str2, String str3, int i) {
        this.hWnd = hwnd;
        this.className = str;
        this.windowText = str2;
        this.processName = str3;
        this.pid = i;
    }
}
