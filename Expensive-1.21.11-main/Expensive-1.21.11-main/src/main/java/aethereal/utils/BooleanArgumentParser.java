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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BooleanArgumentParser implements ArgumentParser<Boolean> {
    public static final List<String> trueValues = Arrays.asList("true", "yes", "on", "1");
    public static final List<String> falseValues = Arrays.asList("false", "no", "off", "0");

    @Override
    public Boolean parse(String str) throws TranslatedException {
        String lowerCase= str.toLowerCase();
        if (trueValues.contains(lowerCase)) {
            return true;
        }
        if (falseValues.contains(lowerCase)) {
            return false;
        }
        throw new TranslatedException(Translation.clearText(Lang.TYPE_INVALID_BOOLEAN.effective().replace("{input}", str)));
    }

    @Override
    public List<String> getSuggestions(String str) {
        String lowerCase= str.toLowerCase();
        return (List) Stream.of(new String[]{"true", "false"}).filter(str2 -> {
            return str2.startsWith(lowerCase);
        }).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "boolean";
    }
}
