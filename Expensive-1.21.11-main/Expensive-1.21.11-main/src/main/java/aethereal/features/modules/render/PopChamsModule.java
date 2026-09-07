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

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Aliases(aliases = {"Pop Chams", "Totem Pop", "Chams"})
public class PopChamsModule extends Module {
    public final List<PopChamsModel> models;
    public final BooleanSetting blending;
    public final BooleanSetting textured;
    public final ColorSetting color;
    public final DeltaTimeTracker deltaTracker;
    public final AnimationStack2 animationStack;

    public PopChamsModule() {
        super(ModuleTab.RENDER, "Pop Chams");
        this.models = new ArrayList();
        this.blending = new BooleanSetting(Lang.POPCHAMS_BLENDING).setValue(true);
        this.textured = new BooleanSetting(Lang.POPCHAMS_TEXTURED);
        this.color = new ColorSetting(Lang.POPCHAMS_COLOR);
        this.deltaTracker = new DeltaTimeTracker();
        this.animationStack = new AnimationStack2();
        addSettings(this.blending, this.textured, this.color);
        register(PlayerTickEvent.class, class130Var -> {
            if (Mc.INSTANCE.isWorldLoaded() && class130Var.isPre()) {
                this.models.removeIf((v0) -> {
                    return v0.tick();
                });
            }
        });
        register(WorldRenderEvent.class, class016Var -> {
            if (Mc.INSTANCE.isWorldLoaded() && !this.models.isEmpty()) {
                MatrixStack matrixStack= class016Var.matrixStack();
                WeightedEngine class141Var= new WeightedEngine(this.deltaTracker.elapsedUnit(), this.animationStack);
                this.animationStack.begin();
                this.models.forEach(class563Var -> {
                    class563Var.animate(class141Var);
                });
                this.animationStack.end();
                this.models.forEach(class563Var2 -> {
                    renderModel(matrixStack, class563Var2);
                });
            }
        });
        register(PacketReceiveEvent.class, class051Var -> {
            ClientWorld world;
            Entity entity;
            if (isState()) {
                if ((class051Var.getPacket()) instanceof EntityStatusS2CPacket packet ) {
                    EntityStatusS2CPacket entityStatusS2CPacket= packet;
                    if (entityStatusS2CPacket.getStatus() != 35 || (world = Mc.INSTANCE.getWorld()) == null || (entity = entityStatusS2CPacket.getEntity(world)) == null || !(entity instanceof OtherClientPlayerEntity)) {
                        return;
                    }
                    this.models.add(createModel((OtherClientPlayerEntity) entity, this.color.getColor(), this.textured.isValue()));
                }
            }
        });
    }

    public PopChamsModel createModel(OtherClientPlayerEntity otherClientPlayerEntity, int i, boolean z) {
        Mc class815Var= Mc.INSTANCE;
        PopChamsFakePlayer class562Var= new PopChamsFakePlayer(this, class815Var.getWorld(), BlockPos.ORIGIN, otherClientPlayerEntity.bodyYaw, new GameProfile(otherClientPlayerEntity.getUuid(), otherClientPlayerEntity.getName().getString()));
        class562Var.copyPositionAndRotation(otherClientPlayerEntity);
        ((PlayerEntity) class562Var).bodyYaw = otherClientPlayerEntity.bodyYaw;
        ((PlayerEntity) class562Var).headYaw = otherClientPlayerEntity.headYaw;
        ((PlayerEntity) class562Var).handSwingProgress = otherClientPlayerEntity.handSwingProgress;
        ((PlayerEntity) class562Var).handSwingTicks = otherClientPlayerEntity.handSwingTicks;
        class562Var.setSneaking(otherClientPlayerEntity.isSneaking());
        ((PlayerEntity) class562Var).limbAnimator.setSpeed(otherClientPlayerEntity.limbAnimator.getSpeed());
        ((PlayerEntity) class562Var).limbAnimator.updateLimbs(otherClientPlayerEntity.limbAnimator.getAnimationProgress(), otherClientPlayerEntity.limbAnimator.getSpeed(), 1.0f);
        return new PopChamsModel(class562Var, otherClientPlayerEntity.getSkin().body().texturePath(), i, z);
    }

    public void renderModel(MatrixStack matrixStack, PopChamsModel class563Var) {
        Mc class815Var= Mc.INSTANCE;
        Vec3d pos= class815Var.getEntityRenderDispatcher().camera.getCameraPos();
        PlayerEntityModel model= class563Var.getModel();
        PlayerEntity player= class563Var.getPlayer();
        Identifier texture= class563Var.getTexture();
        int iApplyOpacity= ColorUtil.applyOpacity(class563Var.getColor(), ColorUtil.alpha(class563Var.getColor()) * class563Var.getAnimation().smoothAnimation());
        double x= player.getX() - pos.getX();
        double y= player.getY() - pos.getY();
        double z= player.getZ() - pos.getZ();
        class815Var.getTickDelta();
        float pos2= player.limbAnimator.getAnimationProgress();
        float speed= player.limbAnimator.getSpeed();
        PlayerEntityRenderState playerEntityRenderState= new PlayerEntityRenderState();
        playerEntityRenderState.bodyYaw = player.getBodyYaw();
        playerEntityRenderState.relativeHeadYaw = player.headYaw - player.bodyYaw;
        playerEntityRenderState.pitch = player.getPitch();
        playerEntityRenderState.deathTime = player.deathTime;
        playerEntityRenderState.limbSwingAnimationProgress = pos2;
        playerEntityRenderState.limbSwingAmplitude = speed;
        model.setAngles(playerEntityRenderState);
        Identifier renderTexture= class563Var.isTextured() && texture != null ? texture : Identifier.ofVanilla("textures/misc/white.png");
        VertexConsumerProvider.Immediate consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer consumer= consumers.getBuffer(RenderLayers.entityTranslucent(renderTexture));
        matrixStack.push();
        matrixStack.translate((float) x, (float) y, (float) z);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(FastMathUtils.rad(180.0f - player.bodyYaw)));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        model.render(matrixStack, consumer, 10, 0, iApplyOpacity);
        consumers.draw();
        matrixStack.pop();
    }
}
