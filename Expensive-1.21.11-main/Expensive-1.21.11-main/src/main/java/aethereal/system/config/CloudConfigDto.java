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

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.Base64;

public class CloudConfigDto {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("base64")
    public String base64;

    @SerializedName("author")
    public String author;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    @SerializedName("is_local_override")
    public boolean localOverride;

    public static CloudConfigDto of(String str, String str2, String str3) {
        CloudConfigDto class376Var= new CloudConfigDto();
        class376Var.id = str;
        class376Var.name = str2;
        class376Var.author = str3;
        return class376Var;
    }

    public byte[] decodeData() {
        return Base64.getDecoder().decode(this.base64);
    }

    public long createdTimestamp() {
        return parseTimestamp(this.createdAt);
    }

    public long updatedTimestamp() {
        return parseTimestamp(this.updatedAt);
    }

    public static long parseTimestamp(String str) {
        try {
            return Instant.parse(str).toEpochMilli();
        } catch (Exception e) {
            return 0L;
        }
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String base64() {
        return this.base64;
    }

    public String author() {
        return this.author;
    }

    public String createdAt() {
        return this.createdAt;
    }

    public String updatedAt() {
        return this.updatedAt;
    }

    public boolean isLocalOverride() {
        return this.localOverride;
    }
}
