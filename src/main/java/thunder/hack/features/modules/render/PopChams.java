package thunder.hack.features.modules.render;

import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.injection.accesors.IEntity;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

import java.util.concurrent.CopyOnWriteArrayList;

public final class PopChams extends Module {
    public PopChams() {
        super("PopChams", Category.RENDER);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.Textured);
    private final Setting<Boolean> secondLayer = new Setting<>("SecondLayer", true);
    private final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(0x8800FF00));
    private final Setting<Integer> ySpeed = new Setting<>("YSpeed", 0, -10, 10);
    private final Setting<Integer> aSpeed = new Setting<>("AlphaSpeed", 5, 1, 100);
    private final Setting<Float> rotSpeed = new Setting<>("RotationSpeed", 0.25f, 0f, 6f);

    private final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList<>();

    private enum Mode {
        Simple, Textured
    }

    @Override
    public void onUpdate() {
        popList.forEach(person -> person.update(popList));
    }

    @Override
    public void onRender3D(MatrixStack stack) {




        popList.forEach(person -> renderEntity(stack, person.player, person.modelPlayer, person.getTexture(), person.getAlpha()));


    }

    @EventHandler
    @SuppressWarnings("unused")
    private void onTotemPop(@NotNull TotemPopEvent e) {
        if (e.getEntity().equals(mc.player) || mc.world == null) return;

        PlayerEntity entity = new PlayerEntity(mc.world, BlockPos.ORIGIN, e.getEntity().bodyYaw, new GameProfile(e.getEntity().getUuid(), e.getEntity().getName().getString())) {
            @Override public boolean isSpectator() {return false;}
            @Override public boolean isCreative() {return false;}
        };

        entity.copyPositionAndRotation(e.getEntity());
        entity.bodyYaw = e.getEntity().bodyYaw;
        entity.headYaw = e.getEntity().headYaw;
        entity.handSwingProgress = e.getEntity().handSwingProgress;
        entity.handSwingTicks = e.getEntity().handSwingTicks;
        entity.setSneaking(e.getEntity().isSneaking());
        entity.limbAnimator.setSpeed(e.getEntity().limbAnimator.getSpeed());
        entity.limbAnimator.getAnimationProgress() = e.getEntity().limbAnimator.getAnimationProgress();
        popList.add(new Person(entity, ((AbstractClientPlayerEntity) e.getEntity()).getSkinTextures().texture()));
    }

    private void renderEntity(@NotNull MatrixStack matrices, @NotNull LivingEntity entity, @NotNull PlayerEntityModel<PlayerEntityRenderState> modelBase, Identifier texture, int alpha) {
        modelBase.leftPants.visible = secondLayer.getValue();
        modelBase.rightPants.visible = secondLayer.getValue();
        modelBase.leftSleeve.visible = secondLayer.getValue();
        modelBase.rightSleeve.visible = secondLayer.getValue();
        modelBase.jacket.visible = secondLayer.getValue();
        modelBase.hat.visible = secondLayer.getValue();

        double x = entity.getX() - gameRenderer.getCamera().getCameraPos().getX();
        double y = entity.getY() - gameRenderer.getCamera().getCameraPos().getY();
        double z = entity.getZ() - gameRenderer.getCamera().getCameraPos().getZ();
        ((IEntity) entity).setPos(new Vec3d(entity.getX(), entity.getY(), entity.getZ()).add(0, (double) ySpeed.getValue() / 50., 0));

        matrices.push();
        matrices.translate((float) x, (float) y, (float) z);

        float yRotYaw = ((alpha / 255f) * 360f * rotSpeed.getValue());
        yRotYaw = yRotYaw == 0 ? 0 : Render2DEngine.interpolateFloat(yRotYaw, yRotYaw - (((aSpeed.getValue() / 255f) * 360f * rotSpeed.getValue())), Render3DEngine.getTickDelta());

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtility.rad(180 - entity.bodyYaw + yRotYaw)));
        prepareScale(matrices);

        PlayerEntityRenderState state = new PlayerEntityRenderState();
        state.age = entity.age + Render3DEngine.getTickDelta();
        state.bodyYaw = entity.bodyYaw;
        state.relativeHeadYaw = entity.headYaw - entity.bodyYaw;
        state.pitch = entity.getPitch();
        state.limbSwingAnimationProgress = entity.limbAnimator.getAnimationProgress(Render3DEngine.getTickDelta());
        state.limbSwingAmplitude = Math.min(entity.limbAnimator.getSpeed(), 1f);
        modelBase.resetTransforms();
        modelBase.setAngles(state);

        BufferBuilder buffer;
        if (mode.is(Mode.Textured)) {
        Render2DEngine.bindTexture(texture);


            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        } else {

            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        }



        modelBase.render(state, matrices, buffer, 10, 0);
        Render2DEngine.endBuilding(buffer);

        matrices.pop();
    }

    private static void prepareScale(@NotNull MatrixStack matrixStack) {
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.scale(1.6f, 1.8f, 1.6f);
        matrixStack.translate(0.0F, -1.501F, 0.0F);
    }

    private class Person {
        private final PlayerEntity player;
        private final PlayerEntityModel<PlayerEntityRenderState> modelPlayer;
        private Identifier texture;
        private int alpha;

        public Person(PlayerEntity player, Identifier texture) {
            this.player = player;
            modelPlayer = new PlayerEntityModel<>(
                    PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), false);
            modelPlayer.getHead().scale(new Vector3f(-0.3f, -0.3f, -0.3f));
            alpha = color.getValue().getAlpha();
            this.texture = texture;
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (alpha <= 0) {
                arrayList.remove(this);
                player.kill((net.minecraft.server.world.ServerWorld) null);
                player.remove(Entity.RemovalReason.KILLED);
                player.onRemoved();
                return;
            }
            alpha -= aSpeed.getValue();
        }

        public int getAlpha() {
            return MathUtility.clamp(alpha, 0, 255);
        }

        public Identifier getTexture() {
            return texture;
        }
    }
}
