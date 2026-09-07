package aethereal.system.config;
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

public class HandlerRepository {
    public final List<ClientHandler> listeners = new ArrayList();
    public DropAllHandler containerHandler;

    public HandlerRepository() {
        setup();
    }

    public void setup() {
        DropAllHandler class090Var= new DropAllHandler();
        this.containerHandler = class090Var;
        registerHandlers(class090Var);
    }

    public void registerHandlers(ClientHandler... class240VarArr) {
        this.listeners.addAll(List.of(class240VarArr));
    }

    public List<ClientHandler> getListeners() {
        return this.listeners;
    }

    public DropAllHandler getContainerHandler() {
        return this.containerHandler;
    }
}
