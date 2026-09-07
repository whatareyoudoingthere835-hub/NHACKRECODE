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
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class ChinaHatModule extends Module {
    public final Mc mc;

    public ChinaHatModule() {
        super(ModuleTab.RENDER, "China Hat");
        this.mc = Mc.INSTANCE;
        register(EntityModelRenderEvent.class, class053Var -> {
            PlayerEntity playerEntity;
            if (isState() && this.mc.isWorldLoaded()) {
                PlayerEntity player= this.mc.getPlayer();
                if ((class053Var.model()) instanceof ModelWithHead modelWithHeadModel ) {
                    ModelWithHead modelWithHead= modelWithHeadModel;
                    PlayerEntity playerEntityLivingEntity= (PlayerEntity) (class053Var.livingEntity());
                    if ((playerEntityLivingEntity instanceof PlayerEntity) && (playerEntity = playerEntityLivingEntity) == player) {
                        int iComputeColor= Expensive.INSTANCE.drawEngine().colorStack().computeColor(128, 51, 204);
                        MatrixStack matrixStack= class053Var.matrixStack();
                        float width= playerEntity.getWidth();
                        float f= player.getEquippedStack(EquipmentSlot.HEAD).isEmpty() ? 0.38f : 0.5f;
                        Tessellator tessellator= Tessellator.getInstance();
                        matrixStack.push();
                        modelWithHead.getHead().applyTransform(matrixStack);
                        matrixStack.translate(0.0f, -f, 0.0f);
                        matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180.0f));
                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                        Matrix4f positionMatrix= matrixStack.peek().getPositionMatrix();
                        BufferBuilder bufferBuilderBegin= tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                        bufferBuilderBegin.vertex(positionMatrix, 0.0f, 0.3f, 0.0f).color(iComputeColor);
                        for (int i = 0; i <= 60; i++) {
                            bufferBuilderBegin.vertex(positionMatrix, (float) ((-Math.sin(((((double) i) * 3.141592653589793d) * 2.0d) / ((double) 60))) * ((double) width)), 0.0f, (float) (Math.cos(((((double) i) * 3.141592653589793d) * 2.0d) / ((double) 60)) * ((double) width))).color(iComputeColor);
                        }
                        ImmediateRenderLayers.draw(bufferBuilderBegin.end(), "china_hat", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN, null, false, true, false);
                        matrixStack.pop();
                    }
                }
            }
        });
    }
}
