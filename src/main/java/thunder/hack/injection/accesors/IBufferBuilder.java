package thunder.hack.injection.accesors;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferBuilder.class)
public interface IBufferBuilder {

    @Accessor("vertexFormat")
    VertexFormat th$vertexFormat();

    @Accessor("drawMode")
    VertexFormat.DrawMode th$drawMode();
}
