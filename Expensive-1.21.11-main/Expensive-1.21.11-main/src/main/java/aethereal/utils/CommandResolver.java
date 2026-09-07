package aethereal.utils;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandResolver {
    public final CommandRegistry registry;

    public CommandResolver(CommandRegistry class187Var) {
        this.registry = class187Var;
    }

    public Optional<ClientCommand> resolve(String str) {
        if (str.isEmpty()) {
            return Optional.empty();
        }
        return this.registry.getCommand(str.split("\\s+")[0]);
    }

    public List<String> getSuggestions(String str) {
        if (str.isEmpty()) {
            return this.registry.getCommandNames();
        }
        String[] strArrSplit= str.split("\\s+", -1);
        if (strArrSplit.length == 1) {
            String lowerCase= strArrSplit[0].toLowerCase();
            return (List) this.registry.getCommandNames().stream().filter(str2 -> {
                return str2.startsWith(lowerCase);
            }).sorted().collect(Collectors.toList());
        }
        Optional<ClientCommand> command= this.registry.getCommand(strArrSplit[0]);
        if (!command.isPresent()) {
            return Collections.emptyList();
        }
        String[] strArr= (String[]) Arrays.copyOfRange(strArrSplit, 1, strArrSplit.length);
        return command.get().getSuggestions(strArr, strArr.length - 1);
    }
}
