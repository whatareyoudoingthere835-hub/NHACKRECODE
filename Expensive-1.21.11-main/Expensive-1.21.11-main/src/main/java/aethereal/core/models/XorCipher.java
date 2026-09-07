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

public final class XorCipher {
    public static final byte[] key = "s8Fk2pL9xQm4vN7wR1jT6yB3cHdA0eZ".getBytes();

    public XorCipher() {
    }

    public static byte[] encrypt(byte[] bArr) {
        return applyCipher(bArr);
    }

    public static byte[] decrypt(byte[] bArr) {
        return applyCipher(bArr);
    }

    public static byte[] applyCipher(byte[] bArr) {
        byte[] bArr2= new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = (byte) (bArr[i] ^ key[i % key.length]);
        }
        return bArr2;
    }
}
