package ru.expensive.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.item.TooltipEvent;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
            at = @At("RETURN"))
    private void onGetTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type,
                              CallbackInfoReturnable<List<Text>> cir) {
        if (cir.getReturnValue() == null) {
            return;
        }
        EventManager.callEvent(new TooltipEvent((ItemStack) (Object) this, cir.getReturnValue()));
    }
}
