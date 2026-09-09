package thunder.hack.injection.accesors;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mutable;

@Mixin(ClientWorld.class)
public interface IClientWorldMixin {
    @Accessor("pendingUpdateManager")
    PendingUpdateManager getPendingUpdateManager();

    @Accessor("timeOfDay")
    @Mutable
    void setTimeOfDay(long timeOfDay);
}