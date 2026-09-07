package aethereal.core.types;
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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.util.InputUtil;

public class KeyArgumentType implements ArgumentParser<Integer> {
    public final Map<String, Integer> keyMap = buildKeyMap();

    public Map<String, Integer> buildKeyMap() {
        HashMap map= new HashMap();
        ObjectIterator it= InputUtil.Type.KEYSYM.map.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            int intKey= ((Int2ObjectMap.Entry) it.next()).getIntKey();
            map.put(KeyboardUtil.keyToString(intKey).replaceAll("\\s", "").toLowerCase(), Integer.valueOf(intKey));
        }
        return map;
    }

    @Override
    public Integer parse(String str) throws TranslatedException {
        Integer num= this.keyMap.get(str.toLowerCase());
        if (num == null) {
            throw new TranslatedException(Translation.clearText(Lang.TYPE_UNKNOWN_KEY.effective().replace("{input}", str)));
        }
        return num;
    }

    @Override
    public List<String> getSuggestions(String str) {
        String lowerCase= str.toLowerCase();
        return (List) this.keyMap.keySet().stream().filter(str2 -> {
            return str2.startsWith(lowerCase);
        }).sorted().collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "key";
    }
}
