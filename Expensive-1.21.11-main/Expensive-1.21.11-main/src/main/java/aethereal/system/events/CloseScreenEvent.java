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

import net.minecraft.client.gui.screen.Screen;

public class CloseScreenEvent extends CancellableEvent {
    public Screen screen;

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CloseScreenEvent)) {
            return false;
        }
        CloseScreenEvent class270Var= (CloseScreenEvent) obj;
        if (!class270Var.canEqual(this) || !super.equals(obj)) {
            return false;
        }
        Screen screen= getScreen();
        Screen screen2= class270Var.getScreen();
        if (screen == null) {
            return screen2 == null;
        }
        return screen.equals(screen2);
    }

    public boolean canEqual(Object obj) {
        return obj instanceof CloseScreenEvent;
    }

    public int hashCode() {
        int iHashCode= super.hashCode();
        Screen screen= getScreen();
        return (iHashCode * 59) + (screen == null ? 43 : screen.hashCode());
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public String toString() {
        return "CloseScreenEvent(screen=" + String.valueOf(getScreen()) + ")";
    }

    public CloseScreenEvent(Screen screen) {
        this.screen = screen;
    }
}
