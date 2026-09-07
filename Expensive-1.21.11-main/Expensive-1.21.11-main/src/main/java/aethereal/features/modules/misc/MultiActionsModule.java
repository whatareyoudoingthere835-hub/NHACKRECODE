package aethereal.features.modules.misc;
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

@Aliases(aliases = {"Multi Actions", "Multi Item Actions", "Multi-use Actions", "Multi Interactions", "Action Cancel", "Dual Actions", "Multiple Usages", "Concurrent Actions"})
public class MultiActionsModule extends Module {
    public MultiActionsModule() {
        super(ModuleTab.MISC, "Multi Actions");
        register(StopUsingItemEvent.class, class076Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                class076Var.cancel();
            }
        });
    }
}
