package aethereal.features.modules.combat;
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

@Aliases(aliases = {"No Player Trace", "No Entity Trace", "Anti Trace", "No Trace"})
public class NoPlayerTraceModule extends Module {
    public NoPlayerTraceModule() {
        super(ModuleTab.COMBAT, "No Player Trace");
        register(EntityTraceEvent.class, class342Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                class342Var.cancel();
            }
        });
    }
}
