package thunder.hack.injection.accesors;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.damage.DamageSource;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplosionImpl.class)
public interface IExplosion {
    @Accessor("pos")
    Vec3d getPos();

    @Mutable
    @Accessor("pos")
    void setPos(Vec3d pos);

    @Accessor("entity")
    Entity getEntity();

    @Accessor("world")
    World getWorld();

    @Accessor("damageSource")
    DamageSource getDamageSource();

    default void setX(double x) {
        Vec3d p = getPos();
        setPos(new Vec3d(x, p.y, p.z));
    }

    default void setY(double y) {
        Vec3d p = getPos();
        setPos(new Vec3d(p.x, y, p.z));
    }

    default void setZ(double z) {
        Vec3d p = getPos();
        setPos(new Vec3d(p.x, p.y, z));
    }
}
