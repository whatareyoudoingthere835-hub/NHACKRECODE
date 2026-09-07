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

public class TpLootStageSwitchMap {
    public static final int[] ordinalMap = new int[TpLootStage.values().length];

    static {
        try {
            ordinalMap[TpLootStage.APPROACH.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            ordinalMap[TpLootStage.LOADING_CHUNKS.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            ordinalMap[TpLootStage.LOOTING.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            ordinalMap[TpLootStage.UNLOADING.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
    }
}
