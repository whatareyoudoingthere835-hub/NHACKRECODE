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

import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudConfigCard {
    public String name;
    public final String cloudId;
    public final String date;
    public final long lastModified;
    public StaffProfile author;

    public CloudConfigCard(CloudConfigMetadata class163Var, GlTextureObject class073Var) {
        this.name = class163Var.name();
        this.date = formatDate(class163Var.createdTimestamp());
        this.lastModified = class163Var.updatedTimestamp();
        this.cloudId = class163Var.id();
        this.author = new StaffProfile(class163Var.author(), "", class073Var, class163Var.authorAvatarUrl());
    }

    public String formatDate(long j) {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date(j));
    }

    public String name() {
        return this.name;
    }

    public String cloudId() {
        return this.cloudId;
    }

    public String date() {
        return this.date;
    }

    public long lastModified() {
        return this.lastModified;
    }

    public StaffProfile author() {
        return this.author;
    }

    public CloudConfigCard name(String str) {
        this.name = str;
        return this;
    }

    public CloudConfigCard author(StaffProfile class629Var) {
        this.author = class629Var;
        return this;
    }
}
