package ru.expensive.api.system.draw;

import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.render.ExpensiveRenderLayers;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DrawEngineImpl implements DrawEngine, QuickImports {

    @Override
    public void quad(Matrix4f matrix4f, float x, float y, float width, float height) {
        quad(matrix4f, x, y, width, height, 0xFFFFFFFF);
    }

    @Override
    public void quad(Matrix4f matrix4f, float x, float y, float width, float height, int color) {
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix4f, x, y, 0).color(color);
        buffer.vertex(matrix4f, x, y + height, 0).color(color);
        buffer.vertex(matrix4f, x + width, y + height, 0).color(color);
        buffer.vertex(matrix4f, x + width, y, 0).color(color);
        ExpensiveRenderLayers.submitGuiOrDraw(ExpensiveRenderLayers.coloredGui(), buffer.end());
    }
}
