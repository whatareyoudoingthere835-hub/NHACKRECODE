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

public class ArgumentTypeFactory {
    public static IntegerArgumentType integer(int i, int i2) {
        return new IntegerArgumentType(Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static FloatArgumentParser floatType(float f, float f2) {
        return new FloatArgumentParser(Float.valueOf(f), Float.valueOf(f2));
    }

    public static <E extends Enum<E>> EnumArgumentType<E> enumType(Class<E> cls) {
        return new EnumArgumentType<>(cls);
    }
}
