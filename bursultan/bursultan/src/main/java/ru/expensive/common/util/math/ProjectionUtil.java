package ru.expensive.common.util.math;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.expensive.common.QuickImports;
import ru.expensive.mixins.accessors.GameRendererAccessor;

public class ProjectionUtil implements QuickImports {
    public static Vector2f project(double x, double y, double z) {
        Camera camera = mc.getEntityRenderDispatcher().camera;
        Vec3d cameraPos = camera.getCameraPos();

        Quaternionf cameraRotation = new Quaternionf(camera.getRotation());
        cameraRotation.conjugate();

        // Вектор от камеры к точке.
        Vector3f result3f = new Vector3f(
                (float) (x - cameraPos.x),
                (float) (y - cameraPos.y),
                (float) (z - cameraPos.z)
        );

        result3f.rotate(cameraRotation);

        float fov = ((GameRendererAccessor) mc.gameRenderer)
                .invokeGetFov(camera, mc.getRenderTickCounter().getTickProgress(true), true);

        return calculateScreenPosition(result3f, fov);
    }

    public static boolean isValidProjection(Vector2f projection) {
        return projection != null &&
                !Float.isInfinite(projection.x()) && !Float.isInfinite(projection.y()) &&
                !Float.isNaN(projection.x()) && !Float.isNaN(projection.y()) &&
                projection.x() != Float.MAX_VALUE && projection.y() != Float.MAX_VALUE;
    }

    private static Vector2f calculateScreenPosition(Vector3f result3f, double fov) {
        Window window = mc.getWindow();
        float width = window.getScaledWidth() / 2.0F;
        float height = window.getScaledHeight() / 2.0F;
        float x = result3f.x;
        float y = result3f.y;
        float z = result3f.z;

        // Точка впереди камеры имеет z < 0. Делим на -z, чтобы масштаб был
        // положительным: деление на отрицательное z зеркалило обе оси —
        // картинка была перевёрнута и «плыла» вслед за камерой.
        if (z < 0.0F) {
            float scaleFactor = height / (-z * (float) Math.tan(Math.toRadians(fov / 2.0F)));
            return new Vector2f(x * scaleFactor + width, height - y * scaleFactor);
        }
        return new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    }
}
