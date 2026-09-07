package aethereal.features.modules.movement;
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

import java.util.concurrent.TimeUnit;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class SpiderModule extends Module {
    public enum SpiderMode implements DisplayNamed {
        DEFAULT(Translation.clearText("Default")),
        FUNTIME_NETHERITE_BRICKS(Translation.clearText("Funtime Netherite Bricks"));

        private final Translation name;

        SpiderMode(Translation name) {
            this.name = name;
        }

        @Override
        public Translation getDisplayName() {
            return this.name;
        }
    }

    public final ModeSetting<SpiderMode> modeSetting;
    public final ActionScheduler scheduler;
    public final Stopwatch timer;
    public final Mc mc;

    public SpiderModule() {
        super(ModuleTab.MOVEMENT, "Spider");
        this.modeSetting = new ModeSetting(Lang.MODE).values(SpiderMode.class);
        this.scheduler = new ActionScheduler();
        this.timer = new Stopwatch();
        this.mc = Mc.INSTANCE;
        addSettings(this.modeSetting);

        register(PlayerTickEvent.class, class130Var -> {
            if (!isState() || !this.mc.isWorldLoaded()) {
                return;
            }
            this.scheduler.update();

            if (this.modeSetting.isSelected(SpiderMode.FUNTIME_NETHERITE_BRICKS)) {
                if (this.mc.getPlayer() != null && this.mc.getPlayer().horizontalCollision) {
                    if (this.timer.hasElapsed(150L, TimeUnit.MILLISECONDS)) {
                        InventoryService invService = Expensive.INSTANCE.inventoryService();
                        java.util.function.Predicate<net.minecraft.item.ItemStack> isBrickPredicate = stack -> stack.isOf(Items.NETHER_BRICKS) || stack.isOf(Items.NETHERITE_BLOCK) || stack.isOf(Items.NETHERITE_SCRAP) || stack.isOf(Items.NETHER_BRICK);
                        invService.searcher().findItem(isBrickPredicate, InventoryScope.ALL).ifPresent(result -> {
                            invService.addTask(InventoryTask.create(isBrickPredicate, result, true, true, false), this);
                        });
                        this.mc.getPlayer().setVelocity(this.mc.getPlayer().getVelocity().x, 0.42d, this.mc.getPlayer().getVelocity().z);
                        this.timer.reset();
                    }
                }
            }
        });

        register(PacketReceiveEvent.class, class051Var -> {
            if (isState() && this.mc.isWorldLoaded() && (class051Var.getPacket() instanceof PlayerPositionLookS2CPacket) && this.timer.hasElapsed(600L, TimeUnit.MILLISECONDS)) {
                this.scheduler.cleanup().addTickStep(0, () -> {
                    this.mc.getPlayer().setOnGround(true);
                    this.mc.getPlayer().setVelocity(new Vec3d(this.mc.getPlayer().getVelocity().x, 0.6d, this.mc.getPlayer().getVelocity().z));
                });
                this.timer.reset();
            }
        });

        register(BlockCollisionEvent.class, class114Var -> {
            if (isState() && this.mc.isWorldLoaded() && class114Var.getPos().getY() >= this.mc.getPlayer().getBlockY() && this.mc.getPlayer().horizontalCollision && this.timer.hasElapsed(600L, TimeUnit.MILLISECONDS)) {
                if (this.modeSetting.isSelected(SpiderMode.DEFAULT)) {
                    class114Var.setState(Blocks.AIR.getDefaultState());
                }
            }
        });
    }
}
