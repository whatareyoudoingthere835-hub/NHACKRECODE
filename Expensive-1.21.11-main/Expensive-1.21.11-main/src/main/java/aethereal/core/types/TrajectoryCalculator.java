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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public enum TrajectoryCalculator {
    INSTANCE;

    public static final int maxSteps = 300;

    public HitResult calcTrajectory(ProjectileEntity projectileEntity) {
        return traceTrajectory(projectileEntity.getEntityPos(), projectileEntity.getVelocity(), projectileEntity);
    }

    public List<HitResult> checkTrajectory(ProjectileEntity projectileEntity, double d) {
        return new ArrayList<>(Collections.singleton(checkTrajectory(RotationMath.INSTANCE.rotationToVector(PlayerRotationManager.INSTANCE.getCurrentRotation()), projectileEntity, d)));
    }

    public HitResult checkTrajectory(Vec3d vec3d, ProjectileEntity projectileEntity, double d) {
        Vec3d vec3dSubtract;
        double dSqrt= Math.sqrt((vec3d.x * vec3d.x) + (vec3d.y * vec3d.y) + (vec3d.z * vec3d.z));
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        Objects.requireNonNull(projectileEntity);
        if (projectileEntity instanceof ArrowEntity && ((ArrowEntity) projectileEntity).getItemStack().getItem().equals(Items.CROSSBOW)) {
            vec3dSubtract = Vec3d.ZERO;
        } else {
            vec3dSubtract = player.getEntityPos().subtract(player.lastX, player.lastY, player.lastZ);
        }
        return traceTrajectory(player.getEyePos().add(FastMathUtils.interpolate(player).subtract(player.getEntityPos())), vec3d.multiply(d / dSqrt).add(vec3dSubtract), projectileEntity);
    }

    public Direction getDirection(HitResult hitResult) {
        if (hitResult instanceof BlockHitResult) {
            return ((BlockHitResult) hitResult).getSide();
        }
        Vec3d vec3dNormalize= hitResult.getPos().subtract(Mc.INSTANCE.getPlayer().getEyePos()).normalize();
        return Direction.getFacing(vec3dNormalize.x, vec3dNormalize.y, vec3dNormalize.z);
    }

    public boolean visible(Entity entity) {
        return ((entity.getX() > entity.lastX ? 1 : (entity.getX() == entity.lastX ? 0 : -1)) == 0 && (entity.getY() > entity.lastY ? 1 : (entity.getY() == entity.lastY ? 0 : -1)) == 0 && (entity.getZ() > entity.lastZ ? 1 : (entity.getZ() == entity.lastZ ? 0 : -1)) == 0) || ((entity instanceof ItemEntity) && (entity.isOnGround() || BlockUtil.checkBlockIntersection(entity.getBoundingBox().expand(2.0d), block -> {
            return block == Blocks.WATER;
        })));
    }

    public BlockHitResult traceTrajectory(Vec3d vec3d, Vec3d vec3d2, ProjectileEntity projectileEntity, int i) {
        ClientWorld world= Mc.INSTANCE.getWorld();
        if (world == null) {
            return new BlockHitResult(BlockPos.ORIGIN.toCenterPos(), Direction.DOWN, BlockPos.ORIGIN.down(999), true);
        }
        List<Box> list= new ArrayList<>();
        world.getEntitiesByClass(LivingEntity.class, projectileEntity.getBoundingBox().expand(128.0d), entityLiving -> {
            return entityLiving != projectileEntity.getOwner() && entityLiving.isAlive();
        }).forEach(entityLiving -> list.add(entityLiving.getBoundingBox().expand(0.30000001192092896d)));

        for (int i2 = 0; i2 < i; i2++) {
            Vec3d vec3d3= vec3d;
            vec3d = vec3d.add(vec3d2);
            vec3d2 = calculateMotion(projectileEntity, vec3d3, vec3d2);
            BlockHitResult blockHitResultRaycast= Box.raycast(list, vec3d3, vec3d, BlockPos.ORIGIN);
            BlockHitResult blockHitResultRaycast2= WorldRaycastUtils.raycast(vec3d3, vec3d, RaycastContext.ShapeType.COLLIDER, (Entity) projectileEntity);
            if (blockHitResultRaycast != null && blockHitResultRaycast.getType().equals(HitResult.Type.BLOCK)) {
                return blockHitResultRaycast;
            }
            if (!blockHitResultRaycast2.getType().equals(HitResult.Type.MISS)) {
                return blockHitResultRaycast2;
            }
            if (vec3d.y < -128.0d) {
                break;
            }
        }
        return new BlockHitResult(BlockPos.ORIGIN.toCenterPos(), Direction.DOWN, BlockPos.ORIGIN.down(999), true);
    }

    public Vec3d calculateMotion(Entity entity, Vec3d vec3d, Vec3d vec3d2) {
        double d;
        boolean zIsIn= Mc.INSTANCE.getWorld().getBlockState(BlockPos.ofFloored(vec3d)).getFluidState().isIn(FluidTags.WATER);
        Objects.requireNonNull(entity);
        if (entity instanceof TridentEntity) {
            d = 0.99d;
        } else if (entity instanceof PersistentProjectileEntity && zIsIn) {
            d = 0.6d;
        } else {
            d = !zIsIn ? 0.99d : 0.8d;
        }
        return vec3d2.add(0.0d, -entity.getFinalGravity(), 0.0d).multiply(d);
    }

    public HitResult traceTrajectory(Vec3d vec3d, Vec3d vec3d2, ProjectileEntity projectileEntity) {
        float f;
        ClientWorld world= Mc.INSTANCE.getWorld();
        if (world == null) {
            return null;
        }
        double d= vec3d.x;
        double d2= vec3d.y;
        double d3= vec3d.z;
        double d4= vec3d2.x;
        double finalGravity= vec3d2.y;
        double d5= vec3d2.z;
        for (int i = 0; i < maxSteps; i++) {
            double d6= d;
            double d7= d2;
            double d8= d3;
            d += d4;
            d2 += finalGravity;
            d3 += d5;
            boolean zIsIn= world.getFluidState(BlockPos.ofFloored(d6, d7, d8)).isIn(FluidTags.WATER);
            if (projectileEntity instanceof TridentEntity) {
                f = 0.99f;
            } else if (projectileEntity instanceof PersistentProjectileEntity) {
                f = zIsIn ? 0.6f : 0.99f;
            } else {
                f = zIsIn ? 0.8f : 0.99f;
            }
            d4 *= (double) f;
            finalGravity = (finalGravity * ((double) f)) - projectileEntity.getFinalGravity();
            d5 *= (double) f;
            Vec3d vec3d3= new Vec3d(d6, d7, d8);
            Vec3d vec3d4= new Vec3d(d, d2, d3);
            BlockHitResult blockHitResultRaycast= WorldRaycastUtils.raycast(vec3d3, vec3d4, RaycastContext.ShapeType.COLLIDER, (Entity) projectileEntity);
            if (blockHitResultRaycast.getType() != HitResult.Type.MISS) {
                return blockHitResultRaycast;
            }
            if (hitsLivingEntity(world, projectileEntity, vec3d3, vec3d4)) {
                return new TrajectoryEntityHit(this, vec3d4);
            }
            if (d2 < -128.0d) {
                return null;
            }
        }
        return null;
    }

    public static boolean hitsLivingEntity(ClientWorld clientWorld, ProjectileEntity projectileEntity, Vec3d vec3d, Vec3d vec3d2) {
        Entity owner= projectileEntity.getOwner();
        Iterator<Entity> it= clientWorld.getOtherEntities(projectileEntity, new Box(vec3d, vec3d2).expand(0.3d), entity -> {
            return (entity instanceof LivingEntity) && ((LivingEntity) entity).isAlive() && entity != owner;
        }).iterator();
        while (it.hasNext()) {
            if (it.next().getBoundingBox().expand(0.3d).raycast(vec3d, vec3d2).isPresent()) {
                return true;
            }
        }
        return false;
    }

    public Vec3d calculateMotion(World world, Entity entity, Vec3d vec3d, Vec3d vec3d2) {
        float f;
        boolean zIsIn= world.getFluidState(BlockPos.ofFloored(vec3d)).isIn(FluidTags.WATER);
        Objects.requireNonNull(entity);
        if (entity instanceof TridentEntity) {
            f = 0.99f;
        } else if (entity instanceof PersistentProjectileEntity && zIsIn) {
            f = 0.6f;
        } else {
            f = !zIsIn ? 0.99f : 0.8f;
        }
        return vec3d2.multiply(f).add(0.0d, -entity.getFinalGravity(), 0.0d);
    }
}
