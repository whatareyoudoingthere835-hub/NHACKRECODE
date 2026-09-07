package aethereal;
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

public class ExpensiveBootstrap {
    public static void init() {
        UserSession class385Var;
        try {
            class385Var = new UserSession(AuthStub.uid(), AuthStub.username(), AuthStub.hwid(), AuthStub.role(), AuthStub.expire(), AuthStub.avatarUrl(), new GlTextureObject(new ClasspathResource("assets/expensive/textures/avatar.png")));
        } catch (UnsatisfiedLinkError e) {
            class385Var = new UserSession("1", "Expensive", "null", "admin", "30.01.9999", "https://i.pinimg.com/736x/8e/b9/46/8eb94669194489ac098b5e693a7c6d89.jpg", new GlTextureObject(new ClasspathResource("assets/expensive/textures/avatar.png")));
        }
        try {
            new Expensive(class385Var);
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
}
