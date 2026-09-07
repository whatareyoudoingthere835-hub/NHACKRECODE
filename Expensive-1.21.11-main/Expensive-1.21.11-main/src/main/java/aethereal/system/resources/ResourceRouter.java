package aethereal.system.resources;
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

import java.util.function.Function;

public class ResourceRouter {
    public final String basePath;
    public final Function<String, ResourceSource> resolver;

    public ResourceSource route(String str) {
        return this.resolver.apply((this.basePath.endsWith("/") ? this.basePath.substring(0, this.basePath.length() - 1) : this.basePath) + "/" + (str.startsWith("/") ? str.substring(1) : str));
    }

    public ResourceRouter(String str, Function<String, ResourceSource> function) {
        this.basePath = str;
        this.resolver = function;
    }
}
