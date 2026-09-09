package thunder.hack.injection.accesors;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface ILivingEntity {
    @Accessor("lastAttackedTime")
    int getLastAttackedTicks();

    @Mutable
    @Accessor("lastAttackedTime")
    void setLastAttackedTicks(int val);

    @Mutable
    @Accessor("lastAttackTime")
    void setLastAttackTime(int val);

    @Accessor("jumpingCooldown")
    int getLastJumpCooldown();

    @Accessor("jumpingCooldown")
    void setLastJumpCooldown(int val);
}
