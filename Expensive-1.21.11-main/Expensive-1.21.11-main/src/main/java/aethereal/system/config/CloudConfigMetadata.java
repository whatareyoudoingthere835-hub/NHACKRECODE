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

public class CloudConfigMetadata {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("author")
    public String author;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    @SerializedName("author_avatar_url")
    public String authorAvatarUrl;

    @SerializedName("is_local_override")
    public boolean localOverride;

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

    public String author() {
        return this.author;
    }

    public String createdAt() {
        return this.createdAt;
    }

    public String updatedAt() {
        return this.updatedAt;
    }

    public String authorAvatarUrl() {
        return this.authorAvatarUrl;
    }

    public boolean isLocalOverride() {
        return this.localOverride;
    }
}
