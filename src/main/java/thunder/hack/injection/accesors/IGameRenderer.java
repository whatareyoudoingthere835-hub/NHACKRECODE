package thunder.hack.injection.accesors;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface IGameRenderer {
    @Invoker("renderHand")
    void irenderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix);

    @Accessor("firstPersonRenderer")
    HeldItemRenderer th$heldItemRenderer();
}
