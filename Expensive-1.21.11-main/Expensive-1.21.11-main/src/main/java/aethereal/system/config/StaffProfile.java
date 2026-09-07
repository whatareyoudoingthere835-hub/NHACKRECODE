package aethereal.system.config;
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

public class StaffProfile {
    public String name;
    public String role;
    public GlTextureObject avatar;
    public String avatarUrl;

    public StaffProfile(String str, String str2, GlTextureObject class073Var, String str3) {
        this.name = str;
        this.role = str2;
        this.avatar = class073Var;
        this.avatarUrl = str3;
    }

    public String name() {
        return this.name;
    }

    public String role() {
        return this.role;
    }

    public GlTextureObject avatar() {
        return this.avatar;
    }

    public String avatarUrl() {
        return this.avatarUrl;
    }

    public StaffProfile name(String str) {
        this.name = str;
        return this;
    }

    public StaffProfile role(String str) {
        this.role = str;
        return this;
    }

    public StaffProfile avatar(GlTextureObject class073Var) {
        this.avatar = class073Var;
        return this;
    }

    public StaffProfile avatarUrl(String str) {
        this.avatarUrl = str;
        return this;
    }
}
