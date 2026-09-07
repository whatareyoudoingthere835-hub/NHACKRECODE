package ru.expensive.api.system.shader.implement;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import ru.expensive.api.system.shader.Shader;
import ru.expensive.api.system.shader.UniformStub;

public class RoundShader extends Shader {
    public UniformStub size;
    public UniformStub location;
    public UniformStub radius;

    public UniformStub color1;
    public UniformStub color2;
    public UniformStub color3;
    public UniformStub color4;

    public UniformStub outlineColor;

    public UniformStub softness;
    public UniformStub thickness;

    public RoundShader() {
        super(Identifier.of("minecraft", "core/round"), VertexFormats.POSITION);
    }

    @Override
    public void setup() {
        this.size = this.getUniform("size");
        this.location = this.getUniform("location");
        this.radius = this.getUniform("radius");

        this.color1 = this.getUniform("color1");
        this.color2 = this.getUniform("color2");
        this.color3 = this.getUniform("color3");
        this.color4 = this.getUniform("color4");

        this.outlineColor = this.getUniform("outlineColor");
        this.softness = this.getUniform("softness");
        this.thickness = this.getUniform("thickness");
    }
}
