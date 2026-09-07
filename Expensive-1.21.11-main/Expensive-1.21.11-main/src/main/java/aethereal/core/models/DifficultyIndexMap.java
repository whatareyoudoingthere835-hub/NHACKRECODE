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

import net.minecraft.world.Difficulty;

public class DifficultyIndexMap {
    public static final int[] difficultyOrdinals = new int[Difficulty.values().length];

    static {
        try {
            difficultyOrdinals[Difficulty.PEACEFUL.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            difficultyOrdinals[Difficulty.EASY.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            difficultyOrdinals[Difficulty.HARD.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
    }
}
