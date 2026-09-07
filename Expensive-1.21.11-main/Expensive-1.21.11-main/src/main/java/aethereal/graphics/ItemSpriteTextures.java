package aethereal.graphics;
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

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class ItemSpriteTextures {
    private String type;
    private Map sprites;
    private boolean glint;

    public ItemSpriteTextures() {
    }

    public ItemSpriteTextures(String str, Map map, boolean z) {
        this.type = str;
        this.sprites = map;
        this.glint = z;
    }

    public static ItemSpriteTextures of(String str, Map map, boolean z, int i) {
        HashMap hashMap= new HashMap();
        for (Object obj : map.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            hashMap.put((Direction) entry.getKey(), SpriteRegion.of((Sprite) entry.getValue(), i));
        }
        return new ItemSpriteTextures(str, hashMap, z);
    }

    public Map sprites() {
        return this.sprites;
    }

    public String type() {
        return this.type;
    }
}
