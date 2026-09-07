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

public class LoginPacket implements OutgoingPacket {
    public final String username;
    public final String password;
    public final byte[] token;

    public LoginPacket(String str, String str2, byte[] bArr) {
        this.username = str;
        this.password = str2;
        this.token = bArr;
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public void encode(PacketBuffer class621Var) {
        class621Var.writeString(this.username);
        class621Var.writeString(this.password != null ? this.password : "");
        class621Var.writeByteArray(this.token);
    }
}
