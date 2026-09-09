package thunder.hack.injection.accesors;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ExplosionS2CPacket.class)
public interface IExplosionS2CPacket {
    @Accessor("playerKnockback")
    Optional<Vec3d> th$getKnockback();

    @Mutable
    @Accessor("playerKnockback")
    void th$setKnockback(Optional<Vec3d> knockback);

    default Vec3d motion() {
        return th$getKnockback().orElse(Vec3d.ZERO);
    }

    default void setMotionX(float velocityX) {
        Vec3d v = motion();
        th$setKnockback(Optional.of(new Vec3d(velocityX, v.y, v.z)));
    }

    default void setMotionY(float velocityY) {
        Vec3d v = motion();
        th$setKnockback(Optional.of(new Vec3d(v.x, velocityY, v.z)));
    }

    default void setMotionZ(float velocityZ) {
        Vec3d v = motion();
        th$setKnockback(Optional.of(new Vec3d(v.x, v.y, velocityZ)));
    }

    default float getMotionX() {
        return (float) motion().x;
    }

    default float getMotionY() {
        return (float) motion().y;
    }

    default float getMotionZ() {
        return (float) motion().z;
    }
}
