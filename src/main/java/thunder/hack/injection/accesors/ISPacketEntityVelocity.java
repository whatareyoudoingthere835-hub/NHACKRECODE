package thunder.hack.injection.accesors;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Pure accessor mixin (see IExplosionS2CPacket for the rationale). 8000x conversion helpers
 * are inlined in the consuming modules.
 */
@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface ISPacketEntityVelocity {
    @Accessor("velocity")
    Vec3d th$getVelocity();

    @Mutable
    @Accessor("velocity")
    void th$setVelocity(Vec3d velocity);
}
