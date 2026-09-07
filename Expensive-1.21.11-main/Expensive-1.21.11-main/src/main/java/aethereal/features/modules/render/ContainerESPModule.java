package aethereal.features.modules.render;
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
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;

@Aliases(aliases = {"Storage ESP", "Container ESP", "Chest ESP", "Shulker ESP"})
public class ContainerESPModule extends Module {
    public final MultiSelectSetting<ContainerType> typesSetting;
    public final Mc mc;

    public ContainerESPModule() {
        super(ModuleTab.RENDER, "Container ESP");
        this.typesSetting = new MultiSelectSetting(Lang.CONTAINER_ESP_BLOCKS, Lang.CONTAINER_ESP_BLOCKS_DESC).values(ContainerType.class);
        this.mc = Mc.INSTANCE;
        addSettings(this.typesSetting);
        register(WorldRenderEvent.class, class016Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                for (BlockEntity blockEntity : getBlockEntities()) {
                    ContainerType.fromBlockEntity(blockEntity).ifPresent(class544Var -> {
                        if (this.typesSetting.selectedValues().contains(class544Var)) {
                            PaletteColorStack class115VarColorStack= Expensive.INSTANCE.drawEngine().colorStack();
                            Box boxMethod002= computeBox(blockEntity, blockEntity.getPos());
                            int iComputeColor= class115VarColorStack.computeColor(30, class544Var.getRed(), class544Var.getGreen(), class544Var.getBlue());
                            int iComputeColor2= class115VarColorStack.computeColor(StencilBufferUtil.STENCIL_MASK, class544Var.getRed(), class544Var.getGreen(), class544Var.getBlue());
                            int iComputeColor3= class115VarColorStack.computeColor(100, class544Var.getRed(), class544Var.getGreen(), class544Var.getBlue());
                            ShapeRenderer.INSTANCE.addBox(class016Var.matrixStack().peek().getPositionMatrix(), boxMethod002, iComputeColor);
                            ShapeRenderer.INSTANCE.addOutlineWireframe(class016Var.matrixStack().peek().getPositionMatrix(), boxMethod002, iComputeColor2, iComputeColor3, 3.0f);
                        }
                    });
                }
            }
        });
    }

    public Box computeBox(BlockEntity blockEntity, BlockPos blockPos) {
        if ((blockEntity instanceof ChestBlockEntity) || (blockEntity instanceof EnderChestBlockEntity)) {
            return new Box(((double) blockPos.getX()) + 0.06d, blockPos.getY(), ((double) blockPos.getZ()) + 0.06d, ((double) (blockPos.getX() + 1)) - 0.06d, ((double) blockPos.getY()) + 0.875d, ((double) (blockPos.getZ() + 1)) - 0.06d);
        }
        return blockEntity instanceof ShulkerBoxBlockEntity ? new Box(blockPos).contract(0.01d) : new Box(blockPos);
    }

    public List<BlockEntity> getBlockEntities() {
        ArrayList arrayList= new ArrayList();
        Iterator<WorldChunk> it= getLoadedChunks().iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().getBlockEntities().values());
        }
        return arrayList;
    }

    public List<WorldChunk> getLoadedChunks() {
        ArrayList arrayList= new ArrayList();
        int iIntValue= ((Integer) this.mc.getGameOptions().getViewDistance().getValue()).intValue();
        int iFloor= (int) Math.floor(this.mc.getPlayer().getX() / 16.0d);
        int iFloor2= (int) Math.floor(this.mc.getPlayer().getZ() / 16.0d);
        for (int i = iFloor - iIntValue; i <= iFloor + iIntValue; i++) {
            for (int i2 = iFloor2 - iIntValue; i2 <= iFloor2 + iIntValue; i2++) {
                WorldChunk worldChunk= this.mc.getWorld().getChunkManager().getWorldChunk(i, i2);
                if (worldChunk != null) {
                    arrayList.add(worldChunk);
                }
            }
        }
        return arrayList;
    }
}
