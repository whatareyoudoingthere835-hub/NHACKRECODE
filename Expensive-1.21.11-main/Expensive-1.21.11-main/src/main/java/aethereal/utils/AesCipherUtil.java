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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AesCipherUtil {
    public static final String algorithm = "AES";
    public static final String secretKey = "1Jh5pJ5NGgvXgKvI";

    public static String encrypt(String str) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpecMethod001= createKeySpec();
        Cipher cipher= Cipher.getInstance(algorithm);
        cipher.init(1, secretKeySpecMethod001);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
    }

    public static String decrypt(String str) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpecMethod001= createKeySpec();
        Cipher cipher= Cipher.getInstance(algorithm);
        cipher.init(2, secretKeySpecMethod001);
        return new String(cipher.doFinal(Base64.getDecoder().decode(str)));
    }

    public static SecretKeySpec createKeySpec() {
        return new SecretKeySpec(secretKey.getBytes(), algorithm);
    }
}
