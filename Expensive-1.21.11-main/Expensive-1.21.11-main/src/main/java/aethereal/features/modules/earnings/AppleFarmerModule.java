package aethereal.features.modules.earnings;
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


import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Aliases(aliases = {"Apple Farmer", "Auto Apple", "Apple Farm", "Auto Apples", "Ферма яблок"})
public class AppleFarmerModule extends Module {
    public final BooleanSetting takeBones;
    public final BooleanSetting dropJunk;
    public final Mc mc;

    public enum State {
        IDLE,
        PLANT,
        GROW,
        HARVEST,
        DROP_JUNK,
        DEPOSIT
    }

    public State currentState = State.IDLE;
    public BlockPos targetSaplingPos = null;

    public AppleFarmerModule() {
        super(ModuleTab.EARNINGS, "Apple Farmer");
        this.takeBones = new BooleanSetting(Translation.clearText("Забирать кости"), Translation.clearText("Автоматически забирать кости из сундуков"));
        this.dropJunk = new BooleanSetting(Translation.clearText("Сбрасывать мусор"), Translation.clearText("Сбрасывать лишние саженцы и палки"));
        this.mc = Mc.INSTANCE;
        addSettings(this.takeBones, this.dropJunk);

        register(PlayerTickEvent.class, event -> {
            if (!isState() || !this.mc.isWorldLoaded() || !event.isPre()) {
                return;
            }
            ClientPlayerEntity player= this.mc.getPlayer();
            if (player == null) {
                return;
            }

            tickAppleFarmer(player);
        });
    }

    private void tickAppleFarmer(ClientPlayerEntity player) {
        if (this.dropJunk.isValue()) {
            dropJunkItems(player);
        }

        BlockPos playerPos= player.getBlockPos();
        if (this.targetSaplingPos == null || !this.mc.getWorld().getBlockState(this.targetSaplingPos).isOf(Blocks.OAK_SAPLING)) {
            this.targetSaplingPos = findNearestDirt(playerPos);
        }

        if (this.targetSaplingPos != null) {
            if (this.mc.getWorld().getBlockState(this.targetSaplingPos).isAir()) {
                plantSapling(player, this.targetSaplingPos);
            } else if (this.mc.getWorld().getBlockState(this.targetSaplingPos).isOf(Blocks.OAK_SAPLING)) {
                growSapling(player, this.targetSaplingPos);
            } else if (this.mc.getWorld().getBlockState(this.targetSaplingPos).isOf(Blocks.OAK_LOG) || this.mc.getWorld().getBlockState(this.targetSaplingPos).isOf(Blocks.OAK_LEAVES)) {
                harvestTree(player, this.targetSaplingPos);
            }
        }
    }

    private BlockPos findNearestDirt(BlockPos playerPos) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos= playerPos.add(x, y, z);
                    if (this.mc.getWorld().getBlockState(pos).isOf(Blocks.DIRT) || this.mc.getWorld().getBlockState(pos).isOf(Blocks.GRASS_BLOCK)) {
                        BlockPos up= pos.up();
                        if (this.mc.getWorld().getBlockState(up).isAir() || this.mc.getWorld().getBlockState(up).isOf(Blocks.OAK_SAPLING) || this.mc.getWorld().getBlockState(up).isOf(Blocks.OAK_LOG)) {
                            return up;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void plantSapling(ClientPlayerEntity player, BlockPos pos) {
        int slot= findItemSlot(player, Items.OAK_SAPLING);
        if (slot != -1) {
            Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().swapTo(this, new InventorySlotRef(slot, InventoryScope.HOTBAR), 0);
            this.mc.getInteractionManager().interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false));
        }
    }

    private void growSapling(ClientPlayerEntity player, BlockPos pos) {
        int slot= findItemSlot(player, Items.BONE_MEAL);
        if (slot != -1) {
            Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().swapTo(this, new InventorySlotRef(slot, InventoryScope.HOTBAR), 0);
            this.mc.getInteractionManager().interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false));
        }
    }

    private void harvestTree(ClientPlayerEntity player, BlockPos pos) {
        for (int y = 0; y <= 6; y++) {
            BlockPos targetPos= pos.up(y);
            if (this.mc.getWorld().getBlockState(targetPos).isOf(Blocks.OAK_LOG) || this.mc.getWorld().getBlockState(targetPos).isOf(Blocks.OAK_LEAVES)) {
                this.mc.getInteractionManager().updateBlockBreakingProgress(targetPos, Direction.UP);
                player.swingHand(Hand.MAIN_HAND);
                break;
            }
        }
    }

    private void dropJunkItems(ClientPlayerEntity player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack= player.getInventory().getStack(i);
            if (!stack.isEmpty() && (stack.isOf(Items.STICK) || (stack.isOf(Items.OAK_SAPLING) && stack.getCount() > 16))) {
                player.dropItem(stack, true);
            }
        }
    }

    private int findItemSlot(ClientPlayerEntity player, net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void deactivate() {
        this.currentState = State.IDLE;
        this.targetSaplingPos = null;
        super.deactivate();
    }
}
