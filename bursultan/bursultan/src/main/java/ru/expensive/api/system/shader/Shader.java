package ru.expensive.api.system.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class Shader implements ShaderSetup {
    protected final Identifier configId;
    protected final VertexFormat vertexFormat;

    public Shader(Identifier configId, VertexFormat vertexFormat) {
        this.configId = configId;
        this.vertexFormat = vertexFormat;
    }

    public void use() {
    }

    protected @Nullable UniformStub getUniform(String name) {
        return null;
    }
}
