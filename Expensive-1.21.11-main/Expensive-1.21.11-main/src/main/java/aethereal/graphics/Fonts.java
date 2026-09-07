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

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

public class Fonts {
    public static final Supplier<MsdfFont> INTER_SEMIBOLD = Suppliers.memoize(() -> {
        return MsdfFont.builder().atlas("inter-semi").data("inter-semi").build();
    });
    public static final Supplier<MsdfFont> INTER_BOLD = Suppliers.memoize(() -> {
        return MsdfFont.builder().atlas("inter-bold").data("inter-bold").build();
    });
    public static final Supplier<MsdfFont> INTER_MEDIUM = Suppliers.memoize(() -> {
        return MsdfFont.builder().atlas("inter-medium").data("inter-medium").build();
    });
    public static final Supplier<MsdfFont> INTER_EXTRA_BOLD = Suppliers.memoize(() -> {
        return MsdfFont.builder().atlas("inter-extrabold").data("inter-extrabold").build();
    });
}
