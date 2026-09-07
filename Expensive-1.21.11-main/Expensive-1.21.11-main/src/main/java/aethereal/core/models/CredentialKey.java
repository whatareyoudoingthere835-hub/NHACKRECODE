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


public final class CredentialKey {
    public final String serverAddress;
    public final String username;

    public CredentialKey(String str, String str2) {
        this.serverAddress = str;
        this.username = str2;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "serverAddress=" + this.serverAddress + ", " + "username=" + this.username + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.serverAddress, this.username);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CredentialKey)) return false;
        CredentialKey o= (CredentialKey) obj;
        return java.util.Objects.equals(this.serverAddress, o.serverAddress) && java.util.Objects.equals(this.username, o.username);
    }
public String serverAddress() {
        return this.serverAddress;
    }

    public String username() {
        return this.username;
    }
}
