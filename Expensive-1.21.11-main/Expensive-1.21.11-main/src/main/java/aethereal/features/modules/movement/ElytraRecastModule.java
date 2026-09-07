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

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ElytraRecastModule extends Module {
    private int jumpTicks = 100;
    public final BooleanSetting autoJump;
    public final BooleanSetting ignoreDurability;
    public final Mc mc;

    public ElytraRecastModule() {
        super(ModuleTab.MOVEMENT, "Elytra Recast");
        this.mc = Mc.INSTANCE;
        this.autoJump = new BooleanSetting(Translation.clearText("Auto Jump"), Translation.clearText("Jumps Automatically."));
        this.ignoreDurability = new BooleanSetting(Translation.clearText("Ignore Durability"), Translation.clearText("Ignores the durability of elytra, allowing them to fly even when they are broken."));
        
        addSettings(this.autoJump, this.ignoreDurability);

        register(JumpEvent.class, e -> {
            this.jumpTicks = 0;
        });

        register(MovementInputEvent.class, e -> {
            if (canStart() && this.mc.getPlayer() != null) {
                if (this.mc.getPlayer().isOnGround()) {
                    if (this.autoJump.isValue() || e.isJumping()) {
                        e.setJumping(true);
                    }
                } else if (!this.mc.getPlayer().isGliding()) {
                    if (this.jumpTicks < 100) {
                        this.jumpTicks++;
                    }
                    if (this.jumpTicks == 1) {
                        e.setJumping(true);
                    } else if (this.jumpTicks >= 2 && this.jumpTicks <= 4) {
                        e.setJumping(false);
                    } else if (this.jumpTicks == 5) {
                        e.setJumping(true);
                        if (this.mc.getNetworkHandler() != null) {
                            this.mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(this.mc.getPlayer(), ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                        }
                    } else if (this.autoJump.isValue() || e.isJumping()) {
                        e.setJumping(true);
                    }
                }
            }
        });
    }

    public boolean shouldResetJump() {
        return canStart() && this.jumpTicks == 0 && this.mc.getPlayer() != null && !this.mc.getPlayer().isOnGround() && !this.mc.getPlayer().isGliding();
    }

    public boolean canStart() {
        if (!isState() || !MovementInputHelper.hasPlayerMovement()) {
            return false;
        }
        
        Module elytraBooster= Expensive.INSTANCE.moduleRepository().get(ElytraBoosterModule.class);
        if (elytraBooster != null && elytraBooster.isState()) {
            return false;
        }

        Module elytraHelper= Expensive.INSTANCE.moduleRepository().get(ElytraHelperModule.class);
        if (elytraHelper != null && elytraHelper.isState()) {
            return false;
        }

        if (this.mc.getPlayer() == null || this.mc.getPlayer().isGliding()) {
            return false;
        }

        ItemStack chestStack= this.mc.getPlayer().getEquippedStack(EquipmentSlot.CHEST);
        if (chestStack.getItem() != Items.ELYTRA) {
            return false;
        }

        if (!this.ignoreDurability.isValue() && chestStack.getDamage() >= chestStack.getMaxDamage() - 1) {
            return false;
        }

        return true;
    }
}
