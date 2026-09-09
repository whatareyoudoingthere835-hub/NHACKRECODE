package thunder.hack.injection.accesors;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClickSlotC2SPacket.class)
public interface IClickSlotC2SPacket {
    @Accessor("mode")
    SlotActionType th$actionType();
}
