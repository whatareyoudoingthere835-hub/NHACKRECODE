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

import java.util.List;
import java.util.stream.Collectors;

public class ModuleArgumentParser implements ArgumentParser<Module> {
    @Override
    public Module parse(String str) {
        return Expensive.INSTANCE.moduleRepository().getModules().stream().filter(class605Var -> {
            return class605Var.getName().replaceAll("\\s", "").equalsIgnoreCase(str);
        }).findFirst().orElseThrow(() -> {
            return new TranslatedException(Translation.clearText(Lang.TYPE_MODULE_NOT_FOUND.effective().replace("{input}", str)));
        });
    }

    @Override
    public List<String> getSuggestions(String str) {
        String lowerCase= str.toLowerCase();
        return (List) Expensive.INSTANCE.moduleRepository().getModules().stream().map(class605Var -> {
            return class605Var.getName().replaceAll("\\s", "");
        }).filter(str2 -> {
            return str2.replaceAll("\\s", "").toLowerCase().startsWith(lowerCase);
        }).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "module";
    }
}
