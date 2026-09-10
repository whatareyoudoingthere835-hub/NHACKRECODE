package thunder.hack.injection.accesors;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.21.11: net.minecraft.world.explosion.Explosion became an interface; mutable state lives
 * in ExplosionImpl. Must stay a pure accessor interface: mixin rejects interface mixins with
 * concrete methods on class targets ("target type mismatch: ... is not an interface").
 */
@Mixin(ExplosionImpl.class)
public interface IExplosion {
    @Accessor("pos")
    Vec3d getPos();

    @Mutable
    @Accessor("pos")
    void setPos(Vec3d pos);

    @Accessor("damageSource")
    DamageSource getDamageSource();
}
