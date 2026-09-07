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

import java.util.Comparator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class CreeperFarmLootPhase {
    public final Mc mc = Mc.INSTANCE;
    public final CreeperFarmModule module;

    public boolean shouldEnter() {
        ItemEntity itemEntityMethod001= findLootItem();
        if (itemEntityMethod001 == null) {
            return false;
        }
        Vec3d pos= itemEntityMethod001.getEntityPos();
        return this.module.getCreepersSortedByDistance().stream().noneMatch(creeperEntity -> {
            return creeperEntity.getEntityPos().isInRange(pos, 6.0d);
        });
    }

    public void tickLootingPhase(ClientPlayerEntity clientPlayerEntity) {
        ItemEntity itemEntityMethod001= findLootItem();
        if (itemEntityMethod001 == null) {
            if (this.module.getCreepersSortedByDistance().isEmpty()) {
                this.module.getPhaseManager().setPhase(TpLootStage.LOADING_CHUNKS);
                return;
            } else {
                this.module.getPhaseManager().setPhase(TpLootStage.APPROACH);
                return;
            }
        }
        if (isCreeperNear(itemEntityMethod001.getEntityPos())) {
            this.module.getPhaseManager().setPhase(TpLootStage.APPROACH);
        } else {
            if (clientPlayerEntity.getEntityPos().isInRange(itemEntityMethod001.getEntityPos(), 1.0d)) {
            }
        }
    }

    public ItemEntity findLootItem() {
        return (ItemEntity) IteratorUtil.toList(this.mc.getWorld().getEntities().iterator()).stream().filter(entity -> {
            return entity instanceof ItemEntity;
        }).map(entity2 -> {
            return (ItemEntity) entity2;
        }).filter(itemEntity -> {
            return itemEntity.getStack().getItem() == Items.GUNPOWDER || itemEntity.getStack().getItem() == Items.EXPERIENCE_BOTTLE;
        }).filter(itemEntity2 -> {
            return BlockUtil.isWithinRegion(itemEntity2.getBlockPos(), this.module.getRegionMin(), this.module.getRegionMax());
        }).min(Comparator.comparingDouble(itemEntity3 -> {
            return itemEntity3.distanceTo(this.mc.getPlayer());
        })).orElse(null);
    }

    public boolean isCreeperNear(Vec3d vec3d) {
        return IteratorUtil.toList(this.mc.getWorld().getEntities().iterator()).stream().filter(entity -> {
            if (entity instanceof CreeperEntity) {
                CreeperEntity creeperEntity= (CreeperEntity) entity;
                if (creeperEntity.getHealth() > 0.0f && creeperEntity.isAlive()) {
                    return true;
                }
            }
            return false;
        }).map(entity2 -> {
            return (CreeperEntity) entity2;
        }).anyMatch(creeperEntity -> {
            return creeperEntity.getEntityPos().isInRange(vec3d, 6.0d);
        });
    }

    public CreeperFarmLootPhase(CreeperFarmModule class597Var) {
        this.module = class597Var;
    }
}
