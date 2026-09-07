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

import net.minecraft.text.Text;

public class ChatReceiveEvent extends CancellableEvent {
    public final String message;
    public final Text textData;

    public final ChatMessageType type;

    public final ChatDecorator applyChatDecoration;

    public String message() {
        return this.message;
    }

    public Text textData() {
        return this.textData;
    }

    public ChatMessageType type() {
        return this.type;
    }

    public ChatDecorator applyChatDecoration() {
        return this.applyChatDecoration;
    }

    public ChatReceiveEvent(String str, Text text, ChatMessageType class068Var, ChatDecorator class067Var) {
        this.message = str;
        this.textData = text;
        this.type = class068Var;
        this.applyChatDecoration = class067Var;
    }
}
