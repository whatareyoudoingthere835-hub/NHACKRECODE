package aethereal.graphics;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class RenderCommandQueue {
    public final List<Consumer<DrawCtx>> commands = new ArrayList();

    public void record(Consumer<DrawCtx> consumer) {
        if (consumer == null) {
            return;
        }
        this.commands.add(consumer);
    }

    public void recordElement(WidgetParent class679Var) {
        if (class679Var == null) {
            return;
        }
        Objects.requireNonNull(class679Var);
        record(class679Var::render);
    }

    public void renderRecorded(DrawCtx class699Var) {
        Iterator<Consumer<DrawCtx>> it= this.commands.iterator();
        while (it.hasNext()) {
            it.next().accept(class699Var);
        }
        this.commands.clear();
    }

    public void clear() {
        this.commands.clear();
    }

    public boolean isEmpty() {
        return this.commands.isEmpty();
    }
}
