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

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public final class EquipmentUtil {
    private EquipmentUtil() {
    }

    public static List<ItemStack> armor(LivingEntity entity) {
        return new ArrayList<>(List.of(
            entity.getEquippedStack(EquipmentSlot.FEET),
            entity.getEquippedStack(EquipmentSlot.LEGS),
            entity.getEquippedStack(EquipmentSlot.CHEST),
            entity.getEquippedStack(EquipmentSlot.HEAD)
        ));
    }

    public static ItemStack armorStack(LivingEntity entity, int index) {
        return armor(entity).get(index);
    }
}
