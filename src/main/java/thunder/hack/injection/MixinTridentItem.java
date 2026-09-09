package thunder.hack.injection;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.UseTridentEvent;

import static thunder.hack.ThunderHack.mc;

@Mixin(TridentItem.class)
public abstract class MixinTridentItem {

    @Inject(method = "onStoppedUsing", at = @At(value = "HEAD"), cancellable = true)
    public void onStoppedUsingHook(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user == mc.player && mc.world != null && EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(Enchantments.RIPTIDE.getRegistryRef()).getEntry(Enchantments.RIPTIDE).get(), stack) > 0) {
            UseTridentEvent e = new UseTridentEvent();
            ThunderHack.EVENT_BUS.post(e);
            if (e.isCancelled())
                ci.cancel();
        }
    }

}
