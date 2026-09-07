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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FovSorter implements TargetSorter {
    @Override
    public List<LivingEntity> sort(List<LivingEntity> list, ClientPlayerEntity clientPlayerEntity) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        list.sort(Comparator.comparingDouble(livingEntity -> {
            return computeFovAngle(livingEntity, clientPlayerEntity);
        }));
        return list;
    }

    public double computeFovAngle(LivingEntity livingEntity, ClientPlayerEntity clientPlayerEntity) {
        Vec3d vec3dSubtract= livingEntity.getEyePos().subtract(clientPlayerEntity.getEyePos());
        double dWrapDegrees= MathHelper.wrapDegrees(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec3dSubtract.z, vec3dSubtract.x)) - 90.0d) - ((double) clientPlayerEntity.getYaw()));
        if (Math.abs(dWrapDegrees) > 180.0d) {
            dWrapDegrees -= Math.signum(dWrapDegrees) * 360.0d;
        }
        return Math.abs(dWrapDegrees);
    }
}
