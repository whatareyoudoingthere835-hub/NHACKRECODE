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

import java.util.ArrayList;
import java.util.List;

public class PinnedServersController {
    public final List<PinnedServerEntry> favorites = new ArrayList();

    public void init() {
    }

    public boolean isPinned(String str) {
        String strMethod002= normalizeAddress(str);
        return this.favorites.stream().anyMatch(class029Var -> {
            return normalizeAddress(class029Var.address()).equals(strMethod002);
        });
    }

    public void addFavorite(PinnedServerEntry class029Var) {
        this.favorites.add(class029Var);
    }

    public static String normalizeAddress(String str) {
        if (str == null) {
            return "";
        }
        String strTrim= str.trim();
        return !strTrim.contains(":") ? strTrim + ":25565" : strTrim;
    }

    public List<PinnedServerEntry> favorites() {
        return List.copyOf(this.favorites);
    }
}
