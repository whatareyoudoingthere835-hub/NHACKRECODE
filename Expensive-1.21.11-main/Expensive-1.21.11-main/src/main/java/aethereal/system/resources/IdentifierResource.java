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

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class IdentifierResource implements ResourceSource {
    public final Identifier identifier;

    public IdentifierResource(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public InputStream stream() {
        try {
            return ((Resource) MinecraftClient.getInstance().getResourceManager().getResource(this.identifier).orElseThrow(() -> {
                return new IllegalStateException("Can't find resource for " + String.valueOf(this.identifier));
            })).getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException("Error reading resource for " + String.valueOf(this.identifier), e);
        }
    }
}
