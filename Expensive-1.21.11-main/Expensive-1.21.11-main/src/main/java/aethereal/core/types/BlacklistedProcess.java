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

public enum BlacklistedProcess {
    IDEA("idea64.exe", "SunAwtFrame"),
    CALCULATOR("CalculatorApp.exe", "MSCTFIME UI"),
    VISUAL_STUDIO("devenv.exe", "MSCTFIME UI"),
    IDA("ida.exe", "Qt5153QTQWindowIcon"),
    X64DBG("x64dbg.exe", null),
    X32DBG("x32dbg.exe", null),
    WIRESHARK("Wireshark.exe", null),
    FIDDLE("Fiddler.exe", null),
    POSTMAN("Postman.exe", null),
    HTTP_TOOLKIT("httptoolkit.exe", null),
    HTTP_DEBUGGER("HTTPDebuggerUI.exe", null),
    WINDBG("windbg.exe", null),
    OLLYDBG("ollydbg.exe", null),
    PYCHARM("pycharm64.exe", "SunAwtFrame"),
    VS_CODE("Code.exe", "Electron_SystemPreferencesHostWindow");

    public final String processName;
    public final String className;

    BlacklistedProcess(String str, String str2) {
        this.processName = str;
        this.className = str2;
    }
}
