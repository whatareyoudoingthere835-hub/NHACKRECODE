package ru.expensive.api.system.shape.implement;

import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ru.expensive.common.QuickImports;
import ru.expensive.api.system.shape.Shape;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.render.ExpensiveRenderLayers;

@Setter
@Accessors(chain = true)
public class Image implements Shape, QuickImports {
    private Matrix4f transformMatrix;
    private String texture;

    public Image setMatrixStack(org.joml.Matrix3x2fStack stack) {
        this.transformMatrix = ru.expensive.common.util.math.MathUtil.toMatrix4f(stack);
        return this;
    }

    public Image setMatrixStack(MatrixStack stack) {
        this.transformMatrix = stack != null ? stack.peek().getPositionMatrix() : null;
        return this;
    }

    @Override
    public void render(ShapeProperties shapeProperties) {
        // Файлы лежат в assets/minecraft/expensive/..., значит id — minecraft:expensive/...
        // (assets/expensive/... в моде нет, старый id expensive:... никуда не резолвился).
        Identifier textureId = Identifier.of("minecraft", "expensive/" + texture);
        RenderLayer layer = ExpensiveRenderLayers.texturedGui(textureId);

        float x = shapeProperties.getX();
        float y = shapeProperties.getY();
        float width = shapeProperties.getWidth();
        float height = shapeProperties.getHeight();
        int color = shapeProperties.getColor().x;

        Matrix4f matrix = transformMatrix != null ? transformMatrix
                : (shapeProperties.getMatrix4f() != null ? shapeProperties.getMatrix4f() : new Matrix4f());

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, x, y, 0.0F).texture(0.0F, 0.0F).color(color);
        buffer.vertex(matrix, x, y + height, 0.0F).texture(0.0F, 1.0F).color(color);
        buffer.vertex(matrix, x + width, y + height, 0.0F).texture(1.0F, 1.0F).color(color);
        buffer.vertex(matrix, x + width, y, 0.0F).texture(1.0F, 0.0F).color(color);
        ExpensiveRenderLayers.submitGuiOrDraw(layer, buffer.end());
    }
}