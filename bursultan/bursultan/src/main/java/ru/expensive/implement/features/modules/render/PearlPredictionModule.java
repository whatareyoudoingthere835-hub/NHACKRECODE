package ru.expensive.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.render.DrawEvent;
import ru.expensive.implement.events.render.WorldRenderEvent;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PearlPredictionModule extends Module {

    final List<PearlPoint> pearlPoints = new ArrayList<>();

    public PearlPredictionModule() {
        super("PearlPrediction", "Pearl Prediction", ModuleCategory.RENDER);
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
        DrawContext context = drawEvent.getDrawContext();

        for (PearlPoint pearlPoint : pearlPoints) {
            Vec3d pos = pearlPoint.position;
            Vector2f projection = ProjectionUtil.project(pos.x, pos.y, pos.z);

            if (!isValidProjection(projection)) {
                continue;
            }

            double time = pearlPoint.ticks * 50 / 1000.0;
            String text = String.format("%.1f", time);
            float textWidth = Fonts.getSize(13).getStringWidth(text) + 11;

            float posX = projection.x() - textWidth / 2;
            float posY = projection.y();

            float padding = 2;
            MatrixStack stack = new MatrixStack();
            Matrix4f positionMatrix = stack.peek().getPositionMatrix();

            rectangle.render(ShapeProperties.create(positionMatrix, posX - padding, posY - padding, padding + textWidth + padding, 10)
                    .round(4)
                    .thickness(2)
                    .softness(1)
                    .outlineColor(0xFF060712)
                    .color(0xB2060712)
                    .build()
            );

            Image image = QuickImports.image.setMatrixStack(stack);
            image.setTexture("textures/clock.png").render(ShapeProperties.create(positionMatrix, posX + textWidth - 7.5, posY - 0.5, 7, 7)
                    .build()
            );

            Fonts.getSize(13).drawString(stack, text, posX + 1.2, posY + 1.4, -1);


            rectangle.render(ShapeProperties.create(positionMatrix, posX - padding, posY - 13, 10, 10)
                    .round(4)
                    .thickness(2)
                    .softness(1)
                    .outlineColor(0xFF060712)
                    .color(0xA2060712)
                    .build()
            );

            context.drawItem(new ItemStack(Items.ENDER_PEARL), (int) posX, (int) (posY - 13));

            Matrix4f dotMatrix = stack.peek().getPositionMatrix();
            for (Vec3d trajectoryPoint : pearlPoint.trajectory) {
                Vector2f trajectoryProjection = ProjectionUtil.project(trajectoryPoint.x, trajectoryPoint.y, trajectoryPoint.z);
                if (!isValidProjection(trajectoryProjection)) {
                    continue;
                }
                rectangle.render(ShapeProperties.create(dotMatrix, trajectoryProjection.x() - 1.5, trajectoryProjection.y() - 1.5, 3, 3)
                        .round(1.5f)
                        .color(0xCCFFFFFF)
                        .build()
                );
            }
        }
    }

    private boolean isValidProjection(Vector2f projection) {
        return projection != null &&
                !Float.isInfinite(projection.x()) && !Float.isInfinite(projection.y()) &&
                !Float.isNaN(projection.x()) && !Float.isNaN(projection.y()) &&
                projection.x() != Float.MAX_VALUE && projection.y() != Float.MAX_VALUE;
    }

    @EventHandler
    public void onWorld(WorldRenderEvent worldRenderEvent) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        pearlPoints.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EnderPearlEntity enderPearlEntity) {
                Vec3d motion = enderPearlEntity.getVelocity();
                Vec3d pos = enderPearlEntity.getEntityPos();
                Vec3d prevPos;
                int ticks = 0;
                List<Vec3d> trajectory = new ArrayList<>();
                trajectory.add(pos);

                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);

                    motion = getNextMotion(enderPearlEntity, prevPos, motion);

                    HitResult hitResult = mc.world.raycast(
                            new RaycastContext(prevPos, pos,
                                    RaycastContext.ShapeType.COLLIDER,
                                    RaycastContext.FluidHandling.NONE,
                                    enderPearlEntity)
                    );

                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        pos = hitResult.getPos();
                    }

                    trajectory.add(pos);

                    if (hitResult.getType() == HitResult.Type.BLOCK || pos.y < -128) {
                        pearlPoints.add(new PearlPoint(pos, ticks, trajectory));
                        break;
                    }
                    ticks++;

                    if (i == 149) {
                        pearlPoints.add(new PearlPoint(pos, ticks, trajectory));
                    }
                }
            }
        }
    }

    private Vec3d getNextMotion(ThrownEntity throwable, Vec3d prevPos, Vec3d motion) {
        boolean isInWater = mc.world.getBlockState(BlockPos.ofFloored(prevPos))
                .getFluidState()
                .isIn(FluidTags.WATER);

        if (isInWater) {
            motion = motion.multiply(0.8);
        } else {
            motion = motion.multiply(0.99);
        }

        if (!throwable.hasNoGravity()) {
            motion = motion.add(0, -0.03F, 0);
        }

        return motion;
    }

    record PearlPoint(Vec3d position, int ticks, List<Vec3d> trajectory) {
    }
}
