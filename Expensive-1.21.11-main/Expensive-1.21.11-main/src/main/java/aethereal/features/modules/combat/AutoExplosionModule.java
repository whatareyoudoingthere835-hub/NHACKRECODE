package aethereal.features.modules.combat;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;


@Aliases(aliases = {"Auto Explosion", "Auto Crystal", "Crystal PvP", "Auto Boom"})
public class AutoExplosionModule extends Module {
    public final BooleanSetting keepCrystalInHand;
    public final Mc mc;
    public final Map<BlockPos, Stopwatch> crystalPlacements;
    public final HitPointResolver hitPointResolver;
    public final ActionScheduler actionScheduler;
    public final ActionScheduler secondaryScheduler;

    public AutoExplosionModule() {
        super(ModuleTab.COMBAT, "Auto Explosion");
        this.keepCrystalInHand = new BooleanSetting(Lang.COMBAT_AUTOEXPLOSION_KEEPCRYSTALINHAND, Lang.COMBAT_AUTOEXPLOSION_KEEPCRYSTALINHAND_DESC);
        this.mc = Mc.INSTANCE;
        this.crystalPlacements = new HashMap();
        this.hitPointResolver = new HitPointResolver();
        this.actionScheduler = new ActionScheduler();
        this.secondaryScheduler = new ActionScheduler();
        addSettings(this.keepCrystalInHand);
        register(BlockInteractEvent.class, class175Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                findCrystalItem().ifPresent(class329Var -> {
                    if (!this.actionScheduler.isFinished() || !GrimDelayHandler.script.isFinished()) {
                        this.mc.getMinecraft().itemUseCooldown = 0;
                        class175Var.cancel();
                        return;
                    }
                    BlockHitResult hitResult= class175Var.getHitResult();
                    boolean zIsOf= this.mc.getPlayer().getStackInHand(class175Var.getHand()).isOf(Items.OBSIDIAN);
                    BlockPos blockPosAdd= zIsOf ? hitResult.getBlockPos().add(hitResult.getSide().getVector()) : hitResult.getBlockPos();
                    if (zIsOf) {
                        rotateToBlock(blockPosAdd, player);
                    }
                    this.actionScheduler.addTickStep(1, () -> {
                        if (this.mc.getWorld().getBlockState(blockPosAdd.up()).isAir() && this.mc.getWorld().getBlockState(blockPosAdd).isOf(Blocks.OBSIDIAN) && blockPosAdd.up().getY() > this.mc.getPlayer().getY() + 0.5d) {
                            if (!zIsOf) {
                                this.mc.getMinecraft().itemUseCooldown = 0;
                            }
                            if (!IteratorUtil.toList(this.mc.getWorld().getEntities().iterator()).stream().anyMatch(entity -> {
                                return (entity instanceof PlayerEntity) && ((double) blockPosAdd.down().getY()) < entity.getY() && entity.squaredDistanceTo(blockPosAdd.up().toCenterPos()) < 20.0d && !FriendManager.isFriend(entity.getName().getString());
                            }) || this.mc.getPlayer().isUsingItem()) {
                                return;
                            }
                            this.crystalPlacements.put(blockPosAdd, new Stopwatch(false).setElapsedTime(-1000L, TimeUnit.MILLISECONDS));
                            IteratorUtil.toList(this.mc.getWorld().getEntities().iterator()).stream().filter(entity2 -> {
                                return (entity2 instanceof EndCrystalEntity) && entity2.getBlockPos().equals(blockPosAdd.up());
                            }).findFirst().ifPresentOrElse(this::attackCrystal, () -> {
                                rotateToBlock(blockPosAdd, this.mc.getPlayer());
                                int swapDuration= this.keepCrystalInHand.isValue() ? 100 : 0;
                                Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().swapTo(this, class329Var.slotReference(), swapDuration);
                                Mc.INSTANCE.getInteractionManager().syncSelectedSlot();
                                PacketSender.sendSequencedNotSilentPacket(i -> {
                                    return new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult.withBlockPos(blockPosAdd), i);
                                });
                            });
                        }
                    });
                });
            }
        });
        register(EntityLifecycleEvent.class, class331Var -> {
            if (isState() && this.mc.isWorldLoaded() && class331Var.type().equals(EntityLifecycleAction.ADD)) {
                if ((class331Var.entity()) instanceof EndCrystalEntity endCrystalEntityEntity ) {
                    EndCrystalEntity endCrystalEntity= endCrystalEntityEntity;
                    if (this.crystalPlacements.containsKey(endCrystalEntity.getBlockPos().down())) {
                        attackCrystal(endCrystalEntity);
                    }
                }
            }
        });
        register(UseItemEvent.class, class059Var -> {
            if (isState() && this.mc.isWorldLoaded() && !this.actionScheduler.isFinished()) {
                class059Var.cancel();
            }
        });
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && this.mc.isWorldLoaded() && class130Var.isPre()) {
                this.crystalPlacements.values().removeIf(class314Var -> {
                    return class314Var.hasElapsed(0L);
                });
                this.secondaryScheduler.update().cleanup();
                this.actionScheduler.update().cleanupIfFinished();
            }
        });
        register(PlayerInitEvent.class, class125Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                this.crystalPlacements.clear();
            }
        });
    }

    public void rotateToBlock(BlockPos blockPos, ClientPlayerEntity clientPlayerEntity) {
        PlayerRotationManager.INSTANCE.scheduleRotation(RotationVector.of(RotationMath.INSTANCE.fromVec3d(blockPos.toCenterPos().add(0.0d, 0.5d, 0.0d).subtract(clientPlayerEntity.getEyePos())).random(0.3f)), (LivingEntity) clientPlayerEntity, RotationConfig.LINEAR_WITH_CORRECTION, 3, (Module) this, 5);
    }

    public void attackCrystal(Entity entity) {
        ClientPlayerEntity player= this.mc.getPlayer();
        if (player == null) {
            return;
        }
        Box boxExpand= entity.getBoundingBox().expand(-0.2d);
        Vec3d eyePos= player.getEyePos();
        Vec3d closestVec= this.hitPointResolver.getClosestVec(eyePos, boxExpand);
        if (eyePos.distanceTo(closestVec) <= 3.15d) {
            PlayerRotationManager.INSTANCE.scheduleRotation(RotationVector.of(RotationMath.INSTANCE.fromVec3d(closestVec.subtract(eyePos)).random(1.0f)), (LivingEntity) this.mc.getPlayer(), RotationConfig.LINEAR_WITH_CORRECTION, 3, (Module) this, 2);
            this.actionScheduler.addTickStep(0, () -> {
                PlayerRotationManager.INSTANCE.scheduleRotation(RotationVector.of(RotationMath.INSTANCE.fromVec3d(closestVec.subtract(eyePos)).random(1.0f)), (LivingEntity) this.mc.getPlayer(), RotationConfig.LINEAR_WITH_CORRECTION, 3, (Module) this, 2);
                this.mc.getInteractionManager().attackEntity(player, entity);
                this.crystalPlacements.remove(entity.getBlockPos().down());
                player.swingHand(Hand.MAIN_HAND);
                if (this.mc.getPlayer().getMainHandStack().isOf(Items.OBSIDIAN)) {
                    if (this.keepCrystalInHand.isValue()) {
                        findCrystalItem().ifPresent(crystalSlot -> {
                            Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().swapTo(this, crystalSlot.slotReference(), 100);
                            Mc.INSTANCE.getInteractionManager().syncSelectedSlot();
                        });
                    } else {
                        return;
                    }
                }
                this.mc.getMinecraft().itemUseCooldown = 0;
            });
        }
    }

    public Optional<SlotSearchResult2> findCrystalItem() {
        return Expensive.INSTANCE.inventoryService().searcher().findItem(itemStack -> {
            return itemStack.getItem() == Items.END_CRYSTAL && !this.mc.getPlayer().getItemCooldownManager().isCoolingDown(itemStack);
        }, InventoryScope.HOTBAR);
    }

    @Override
    public void deactivate() {
        this.crystalPlacements.clear();
        super.deactivate();
    }
}
