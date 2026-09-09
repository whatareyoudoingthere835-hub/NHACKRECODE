package thunder.hack.injection.accesors;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

/**
 * Pure accessor mixin: interface mixins targeting a class must not declare default methods.
 * Per-axis helpers live in the consuming modules.
 */
@Mixin(ExplosionS2CPacket.class)
public interface IExplosionS2CPacket {
    @Accessor("playerKnockback")
    Optional<Vec3d> th$getKnockback();

    @Mutable
    @Accessor("playerKnockback")
    void th$setKnockback(Optional<Vec3d> knockback);
}
