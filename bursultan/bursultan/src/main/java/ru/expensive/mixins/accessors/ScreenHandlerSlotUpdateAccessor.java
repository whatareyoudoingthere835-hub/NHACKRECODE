package ru.expensive.mixins.accessors;

import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenHandlerSlotUpdateS2CPacket.class)
public interface ScreenHandlerSlotUpdateAccessor {
    @Mutable
    @Accessor("slot")
    void setSlot(int slot);
}
