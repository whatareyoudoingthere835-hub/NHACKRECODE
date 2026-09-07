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

import java.time.Instant;

public class Theme {
    public final String id;
    public String name;
    public String owner;
    public final ConfigOrigin kind;

    public final ThemeMode type;

    public final StylePalette palette;

    public final Instant createdAt;
    public Instant updatedAt;

    public static Theme of(String str, String str2, String str3, ConfigOrigin class763Var, ThemeMode class765Var, StylePalette class764Var, Instant instant, Instant instant2) {
        return new Theme(str, str2, str3, class763Var, class765Var, class764Var, instant, instant2);
    }

    public Theme rename(String str) {
        if (str == null || str.isBlank()) {
            return this;
        }
        this.name = str;
        return markUpdated();
    }

    public Theme owner(String str) {
        this.owner = str;
        return markUpdated();
    }

    public Theme markUpdated() {
        this.updatedAt = Instant.now();
        return this;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String owner() {
        return this.owner;
    }

    public ConfigOrigin kind() {
        return this.kind;
    }

    public ThemeMode type() {
        return this.type;
    }

    public StylePalette palette() {
        return this.palette;
    }

    public Instant createdAt() {
        return this.createdAt;
    }

    public Instant updatedAt() {
        return this.updatedAt;
    }

    public Theme(String str, String str2, String str3, ConfigOrigin class763Var, ThemeMode class765Var, StylePalette class764Var, Instant instant, Instant instant2) {
        this.id = str;
        this.name = str2;
        this.owner = str3;
        this.kind = class763Var;
        this.type = class765Var;
        this.palette = class764Var;
        this.createdAt = instant;
        this.updatedAt = instant2;
    }
}
