package aethereal.features.modules.movement;

import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

@Aliases(aliases = {"Elytra Fly", "ElytraFly", "Elytra Flight", "Elytra Speed"})
public class ElytraFlyModule extends Module {
    public enum ElytraFlyMode implements DisplayNamed {
        BOOST(Translation.clearText("Boost")),
        CONTROL(Translation.clearText("Control")),
        FIREWORK(Translation.clearText("Firework"));

        private final Translation name;

        ElytraFlyMode(Translation name) {
            this.name = name;
        }

        @Override
        public Translation getDisplayName() {
            return this.name;
        }
    }

    public final ModeSetting<ElytraFlyMode> modeSetting;
    public final NumberSetting speedSetting;
    public final BooleanSetting autoTakeoff;
    public final Mc mc;
    private final Stopwatch fireworkTimer = new Stopwatch();

    public ElytraFlyModule() {
        super(ModuleTab.MOVEMENT, "Elytra Fly");
        this.modeSetting = new ModeSetting(Lang.MODE).values(ElytraFlyMode.class);
        this.speedSetting = new NumberSetting(Translation.clearText("Скорость"), Translation.clearText("Скорость полета на элитрах")).range(0.1f, 5.0f).currentValue(1.5f).step(0.1f);
        this.autoTakeoff = new BooleanSetting(Translation.clearText("Авто взлет"), Translation.clearText("Автоматически взлетать при прыжке"));
        this.mc = Mc.INSTANCE;

        addSettings(this.modeSetting, this.speedSetting, this.autoTakeoff);

        register(PlayerTickEvent.class, event -> {
            if (!isState() || !this.mc.isWorldLoaded() || !event.isPre()) {
                return;
            }

            ClientPlayerEntity player = this.mc.getPlayer();
            if (player == null) {
                return;
            }

            if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                return;
            }

            if (this.autoTakeoff.isValue() && !player.isGliding() && !player.isOnGround() && player.getVelocity().y < 0) {
                if (this.mc.getNetworkHandler() != null) {
                    this.mc.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(player, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                }
            }

            if (!player.isGliding()) {
                return;
            }

            float speed = this.speedSetting.currentValue();

            switch (this.modeSetting.currentValue()) {
                case BOOST -> {
                    Vec3d look = player.getRotationVector();
                    player.setVelocity(look.x * speed, look.y * speed * 0.5d, look.z * speed);
                }
                case CONTROL -> {
                    double yaw = Math.toRadians(player.getYaw());
                    double pitch = Math.toRadians(player.getPitch());
                    double x = -Math.sin(yaw) * speed;
                    double z = Math.cos(yaw) * speed;
                    double y = 0.0d;

                    if (this.mc.getGameOptions().jumpKey.isPressed()) {
                        y = speed * 0.5d;
                    } else if (this.mc.getGameOptions().sneakKey.isPressed()) {
                        y = -speed * 0.5d;
                    }

                    if (MovementInputHelper.hasPlayerMovement()) {
                        player.setVelocity(x, y, z);
                    } else {
                        player.setVelocity(0.0d, y, 0.0d);
                    }
                }
                case FIREWORK -> {
                    if (this.fireworkTimer.hasElapsed(1000L)) {
                        InventoryService invService = Expensive.INSTANCE.inventoryService();
                        java.util.function.Predicate<net.minecraft.item.ItemStack> isFirework = stack -> stack.isOf(Items.FIREWORK_ROCKET);
                        invService.searcher().findItem(isFirework, InventoryScope.ALL).ifPresent(result -> {
                            invService.addTask(InventoryTask.create(isFirework, result, true, false, false), this);
                        });
                        this.fireworkTimer.reset();
                    }
                }
            }
        });
    }
}