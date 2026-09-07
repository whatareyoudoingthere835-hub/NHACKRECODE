package ru.expensive.implement.features.modules.render;

import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Extra;
import ru.expensive.implement.events.render.WorldRenderEvent;
import ru.expensive.implement.features.modules.combat.AuraModule;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.expensive.common.util.math.ProjectionUtil;

public class TargetESPModule extends Module {
    private static final float MARKER_SCALE = 0.1f;
    private static final float ROTATION_SPEED = 2.0f;
    private static final float MARKER_SIZE = 4.0f;
    private static final float FADE_IN_MS = 150.0f;
    private static final float FADE_OUT_MS = 200.0f;

    private final ColorSetting colorSetting = new ColorSetting("ESP Color", "Color of the marker")
            .presets(0x80FFFFFF, 0x80FF0000);

    private float rotation;
    private float alpha;
    private Entity lastTarget;
    private Vec3d smoothPosition;
    private Vec3d lastPosition;
    private long lastUpdateTime;
    private long lastAlphaTime;
    private boolean isTargetRemoved = false;

    public TargetESPModule() {
        super("TargetESP", ModuleCategory.RENDER);
        setup(colorSetting);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (mc.world == null || mc.player == null) return;

        updateRotation();
        Entity currentTarget = ((AuraModule) Extra.getInstance().getModuleProvider().module("Aura"))
                .getTarget();

        updateAlpha(currentTarget);
        updatePosition(currentTarget);

        if (alpha > 0.01f && (currentTarget != null || isTargetRemoved || lastTarget != null)) {
            renderMarker(event.getStack(), currentTarget);
        }
    }

    private void updateRotation() {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        rotation = (rotation + (ROTATION_SPEED * deltaTime * 0.1f)) % 360.0f;
        lastUpdateTime = currentTime;
    }

    private void updateAlpha(Entity currentTarget) {
        // Было: шаг ~0.0025 за кадр — появление/исчезновение занимало ~7 секунд.
        // Теперь: появление за ~150мс, исчезновение за ~200мс.
        long now = System.currentTimeMillis();
        if (lastAlphaTime == 0L) lastAlphaTime = now;
        float dt = Math.min(now - lastAlphaTime, 100L);
        lastAlphaTime = now;

        float targetAlpha = currentTarget != null ? 1.0f : 0.0f;
        float duration = targetAlpha > alpha ? FADE_IN_MS : FADE_OUT_MS;
        float step = duration > 0 ? dt / duration : 1.0f;

        alpha = alpha < targetAlpha
                ? Math.min(alpha + step, targetAlpha)
                : Math.max(alpha - step, targetAlpha);
    }

    private void updatePosition(Entity currentTarget) {
        if (currentTarget != null) {
            isTargetRemoved = false;
            Vec3d lerped = currentTarget.getLerpedPos(mc.getRenderTickCounter().getTickProgress(true));
            Vec3d targetPos = new Vec3d(
                lerped.x,
                lerped.y + currentTarget.getHeight() * 0.5,
                lerped.z
            );
            
            if (lastTarget != currentTarget) {
                smoothPosition = targetPos;
            }

            smoothPosition = targetPos;
            lastPosition = targetPos;
            lastTarget = currentTarget;
        } else if (lastTarget != null) {
            assert mc.world != null;
            Entity entityInWorld = mc.world.getEntityById(lastTarget.getId());
            
            if (entityInWorld == null && lastPosition != null) {
                isTargetRemoved = true;
                smoothPosition = lastPosition;
            } else {
                isTargetRemoved = false;
                assert entityInWorld != null;
                Vec3d lerpedWorld = entityInWorld.getLerpedPos(mc.getRenderTickCounter().getTickProgress(true));
                Vec3d targetPos = new Vec3d(
                    lerpedWorld.x,
                    lerpedWorld.y + entityInWorld.getHeight() * 0.5,
                    lerpedWorld.z
                );
                smoothPosition = targetPos;
                lastPosition = targetPos;
                lastTarget = entityInWorld;
            }

            if (alpha <= 0.01f) {
                lastTarget = null;
                smoothPosition = null;
                lastPosition = null;
                isTargetRemoved = false;
            }
        }
    }

    private void renderMarker(MatrixStack matrices, Entity target) {
        if (smoothPosition == null) return;

        // Движок рисует только в экранных координатах — проецируем и рисуем в пикселях.
        Vector2f projection = ProjectionUtil.project(smoothPosition.x, smoothPosition.y, smoothPosition.z);
        if (!ProjectionUtil.isValidProjection(projection)) return;

        float progress = (float) ((Math.sin(Math.toRadians(rotation)) + 1.0) / 2.0);
        int color = colorSetting.interpolateColor(progress);
        color = (color & 0x00FFFFFF) | ((int) (((color >> 24) & 0xFF) * alpha) << 24);

        // Квад реально вращается: матрица T(центр) * Rz(угол), сам квад рисуем
        // относительно центра. Раньше rotation влиял только на пульсацию цвета.
        float size = MARKER_SIZE * 10;
        Matrix4f rotationMatrix = new Matrix4f()
                .translate(projection.x(), projection.y(), 0.0F)
                .rotateZ((float) Math.toRadians(rotation));

        QuickImports.image.setTransformMatrix(rotationMatrix)
                .setTexture("images/render/marker.png")
                .render(ShapeProperties.create(
                                rotationMatrix,
                                -size / 2, -size / 2,
                                size, size)
                        .color(color)
                        .bloom(true)
                        .build());
    }
}