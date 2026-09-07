package aethereal.system.events;
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

import java.util.function.Consumer;

public class PostProcessEvent implements Event {
    public final OverlayCommandQueue blurQueue;

    public final RenderCommandQueue bloomQueue;

    public final DrawCtx context;

    public void addBlur(Consumer<DrawCtx> consumer) {
        this.blurQueue.record(consumer);
    }

    public void addBloom(Consumer<DrawCtx> consumer) {
        this.bloomQueue.record(consumer);
    }

    public DrawCtx context() {
        return this.context;
    }

    public PostProcessEvent(OverlayCommandQueue class677Var, RenderCommandQueue class676Var, DrawCtx class699Var) {
        this.blurQueue = class677Var;
        this.bloomQueue = class676Var;
        this.context = class699Var;
    }
}
