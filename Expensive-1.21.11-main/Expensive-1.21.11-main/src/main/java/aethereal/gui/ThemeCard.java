package aethereal.gui;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThemeCard {
    public String name;
    public ThemeMode type;

    public ConfigOrigin kind;

    public StylePalette palette;
    public final String cloudId;
    public final String date;

    public StaffProfile author;

    public ThemeCard(Theme class760Var, GlTextureObject class073Var) {
        this.name = class760Var.name();
        this.type = class760Var.type();
        this.kind = class760Var.kind();
        this.palette = class760Var.palette();
        this.date = formatDate(Timestamp.from(class760Var.createdAt()).getTime());
        this.cloudId = class760Var.id();
        this.author = new StaffProfile(class760Var.owner(), "", class073Var, null);
    }

    public String formatDate(long j) {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date(j));
    }

    public String name() {
        return this.name;
    }

    public ThemeMode type() {
        return this.type;
    }

    public ConfigOrigin kind() {
        return this.kind;
    }

    public StylePalette palette() {
        return this.palette;
    }

    public String cloudId() {
        return this.cloudId;
    }

    public String date() {
        return this.date;
    }

    public StaffProfile author() {
        return this.author;
    }

    public ThemeCard(String str, String str2) {
        this.cloudId = str;
        this.date = str2;
    }

    public ThemeCard name(String str) {
        this.name = str;
        return this;
    }
}
