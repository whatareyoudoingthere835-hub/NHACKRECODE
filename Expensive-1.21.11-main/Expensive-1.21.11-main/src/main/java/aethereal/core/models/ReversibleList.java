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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ReversibleList<T> implements Iterable<T> {
    public final List<T> items;
    public boolean reversed;

    public ReversibleList(int i) {
        this.items = new ArrayList(i);
    }

    public void add(T t) {
        this.items.add(t);
    }

    public void clear() {
        this.items.clear();
        this.reversed = false;
    }

    public void reverse() {
        if (this.reversed) {
            return;
        }
        Collections.reverse(this.items);
        this.reversed = true;
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        reverse();
        return this.items.iterator();
    }
}
