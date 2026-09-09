package thunder.hack.injection.accesors;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface ISPacketEntityVelocity {
    @Accessor("velocity")
    Vec3d th$getVelocity();

    @Mutable
    @Accessor("velocity")
    void th$setVelocity(Vec3d velocity);

    default void setMotionX(int velocityX) {
        Vec3d v = th$getVelocity();
        th$setVelocity(new Vec3d(velocityX / 8000.0, v.y, v.z));
    }

    default void setMotionY(int velocityY) {
        Vec3d v = th$getVelocity();
        th$setVelocity(new Vec3d(v.x, velocityY / 8000.0, v.z));
    }

    default void setMotionZ(int velocityZ) {
        Vec3d v = th$getVelocity();
        th$setVelocity(new Vec3d(v.x, v.y, velocityZ / 8000.0));
    }
}
