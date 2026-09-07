package aethereal.core.models;
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


public final class CommandContext {
    public final String[] args;

    public CommandContext(String[] strArr) {
        this.args = strArr;
    }

    public <T> T getArgument(int i, ArgumentParser<T> class071Var) throws TranslatedException {
        if (i >= this.args.length) {
            throw new TranslatedException(Translation.clearText(Lang.COMMAND_MISSING_ARGUMENT.effective().replace("{index}", String.valueOf(i))));
        }
        return class071Var.parse(this.args[i]);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "args=" + this.args + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.args);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CommandContext)) return false;
        CommandContext o= (CommandContext) obj;
        return java.util.Objects.equals(this.args, o.args);
    }
public String[] args() {
        return this.args;
    }
}
