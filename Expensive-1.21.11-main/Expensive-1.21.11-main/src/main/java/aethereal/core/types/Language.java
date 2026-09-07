package aethereal.core.types;
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

public enum Language {
    en_US("en_US.json", "English"),
    ru_RU("ru_RU.json", "Русский");

    public final String source;
    public final String canonical;
    public static final Language PRIMARY = en_US;

    Language(String str, String str2) {
        this.source = str;
        this.canonical = str2;
    }

    public String source() {
        return this.source;
    }

    public String canonical() {
        return this.canonical;
    }
}
