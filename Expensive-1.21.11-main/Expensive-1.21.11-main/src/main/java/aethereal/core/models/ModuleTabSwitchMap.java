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

public class ModuleTabSwitchMap {
    public static final int[] tabSwitchMap = new int[ModuleTab.values().length];

    static {
        try {
            tabSwitchMap[ModuleTab.COMBAT.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            tabSwitchMap[ModuleTab.MOVEMENT.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            tabSwitchMap[ModuleTab.PLAYER.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            tabSwitchMap[ModuleTab.RENDER.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            tabSwitchMap[ModuleTab.MISC.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            tabSwitchMap[ModuleTab.EARNINGS.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        try {
            tabSwitchMap[ModuleTab.AUTOBUY.ordinal()] = 7;
        } catch (NoSuchFieldError e7) {
        }
        try {
            tabSwitchMap[ModuleTab.CONFIGS.ordinal()] = 8;
        } catch (NoSuchFieldError e8) {
        }
    }
}
