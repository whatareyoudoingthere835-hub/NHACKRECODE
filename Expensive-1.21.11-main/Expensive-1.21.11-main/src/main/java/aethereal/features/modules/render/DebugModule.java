package aethereal.features.modules.render;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class DebugModule extends Module {
    public final Mc mc;

    public DebugModule() {
        super(ModuleTab.RENDER, "Debug");
        this.mc = Mc.INSTANCE;
        register(WorldRenderEvent.class, class016Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                MatrixStack matrixStack= class016Var.matrixStack();
                Tessellator tessellator= Tessellator.getInstance();
                Vec3d pos= this.mc.getCamera().getCameraPos();
                class016Var.matrixStack().push();
                class016Var.matrixStack().translate(pos.multiply(-1.0d));
                float fCurrentTimeMillis= (System.currentTimeMillis() % 10000) / 10000.0f;
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(fCurrentTimeMillis * 90.0f));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(fCurrentTimeMillis * 360.0f));
                Matrix4f positionMatrix= matrixStack.peek().getPositionMatrix();
                GL11.glEnable(2848);
                BufferBuilder bufferBuilderBegin= tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                Vec3d vec3d= new Vec3d(0.0d, 300.0d, 0.0d);
                for (int i = 0; i <= 48; i++) {
                    double d= (6.283185307179586d * ((double) i)) / ((double) 48);
                    double d2= (6.283185307179586d * ((double) (i + 1))) / ((double) 48);
                    for (int i2 = 0; i2 <= 24; i2++) {
                        double d3= (3.141592653589793d * ((double) i2)) / ((double) 24);
                        double d4= (3.141592653589793d * ((double) (i2 + 1))) / ((double) 24);
                        Vec3d vec3dAdd= vec3d.add(((double) 25.0f) * Math.sin(d3) * Math.cos(d), ((double) 25.0f) * Math.cos(d3), ((double) 25.0f) * Math.sin(d3) * Math.sin(d));
                        Vec3d vec3dAdd2= vec3d.add(((double) 25.0f) * Math.sin(d4) * Math.cos(d), ((double) 25.0f) * Math.cos(d4), ((double) 25.0f) * Math.sin(d4) * Math.sin(d));
                        Vec3d vec3dAdd3= vec3d.add(((double) 25.0f) * Math.sin(d4) * Math.cos(d2), ((double) 25.0f) * Math.cos(d4), ((double) 25.0f) * Math.sin(d4) * Math.sin(d2));
                        Vec3d vec3dAdd4= vec3d.add(((double) 25.0f) * Math.sin(d3) * Math.cos(d2), ((double) 25.0f) * Math.cos(d3), ((double) 25.0f) * Math.sin(d3) * Math.sin(d2));
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd.x, (float) vec3dAdd.y, (float) vec3dAdd.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd2.x, (float) vec3dAdd2.y, (float) vec3dAdd2.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd2.x, (float) vec3dAdd2.y, (float) vec3dAdd2.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd3.x, (float) vec3dAdd3.y, (float) vec3dAdd3.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd3.x, (float) vec3dAdd3.y, (float) vec3dAdd3.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd4.x, (float) vec3dAdd4.y, (float) vec3dAdd4.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd4.x, (float) vec3dAdd4.y, (float) vec3dAdd4.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                        bufferBuilderBegin.vertex(positionMatrix, (float) vec3dAdd.x, (float) vec3dAdd.y, (float) vec3dAdd.z).color(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
                ImmediateRenderLayers.draw(bufferBuilderBegin.end(), "debug_sphere", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES, null, false, false, false);
                GL11.glDisable(2848);
                class016Var.matrixStack().pop();
            }
        });
    }
}
