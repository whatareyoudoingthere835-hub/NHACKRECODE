package aethereal.utils;
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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public final class ProjectionUtil {
    public static final AtomicReference<Matrix4f> LAST_PROJECTION = new AtomicReference<>();
    public static final AtomicReference<Matrix4f> LAST_MODEL_VIEW = new AtomicReference<>();

    public static Optional<Vector2f> worldToScreen(Vec3d vec3d) {
        Matrix4f matrix4f= LAST_PROJECTION.get();
        Matrix4f matrix4f2= LAST_MODEL_VIEW.get();
        if (matrix4f == null || matrix4f2 == null) {
            return Optional.empty();
        }
        Matrix4f matrix4fMul= new Matrix4f(matrix4f).mul(matrix4f2);
        Vec3d vec3dSubtract= vec3d.subtract(MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos());
        Vector4f vector4f= new Vector4f((float) vec3dSubtract.x, (float) vec3dSubtract.y, (float) vec3dSubtract.z, 1.0f);
        matrix4fMul.transform(vector4f);
        if (vector4f.w <= 0.0f || Float.isNaN(vector4f.w)) {
            return Optional.empty();
        }
        float f= vector4f.x / vector4f.w;
        float f2= vector4f.y / vector4f.w;
        Window window= MinecraftClient.getInstance().getWindow();
        float framebufferWidth= (f + 1.0f) * 0.5f * window.getFramebufferWidth();
        float framebufferHeight= (1.0f - f2) * 0.5f * window.getFramebufferHeight();
        return (Float.isNaN(framebufferWidth) || Float.isNaN(framebufferHeight) || Float.isInfinite(framebufferWidth) || Float.isInfinite(framebufferHeight)) ? Optional.empty() : Optional.of(new Vector2f(framebufferWidth, framebufferHeight));
    }

    public static boolean isOutOfScreen(Vector4f vector4f) {
        return vector4f == null || (vector4f.x < 0.0f && vector4f.z < 1.0f) || (vector4f.y < 0.0f && vector4f.w < 1.0f);
    }

    public static Optional<Vector4f> boxToScreen(Box box) {
        Matrix4f matrix4f= LAST_PROJECTION.get();
        Matrix4f matrix4f2= LAST_MODEL_VIEW.get();
        if (matrix4f == null || matrix4f2 == null) {
            return Optional.empty();
        }
        Matrix4f matrix4fMul= new Matrix4f(matrix4f).mul(matrix4f2);
        Vec3d camPos= MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();

        float minX= Float.POSITIVE_INFINITY;
        float minY= Float.POSITIVE_INFINITY;
        float maxX= Float.NEGATIVE_INFINITY;
        float maxY= Float.NEGATIVE_INFINITY;
        int pointsInFront= 0;

        Window window= MinecraftClient.getInstance().getWindow();
        float fw= window.getFramebufferWidth();
        float fh= window.getFramebufferHeight();

        for (Vec3d vec3d : new Vec3d[]{new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.minX, box.minY, box.maxZ), new Vec3d(box.minX, box.maxY, box.minZ), new Vec3d(box.minX, box.maxY, box.maxZ), new Vec3d(box.maxX, box.minY, box.minZ), new Vec3d(box.maxX, box.minY, box.maxZ), new Vec3d(box.maxX, box.maxY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ)}) {
            Vec3d vec3dSubtract= vec3d.subtract(camPos);
            Vector4f vector4f= new Vector4f((float) vec3dSubtract.x, (float) vec3dSubtract.y, (float) vec3dSubtract.z, 1.0f);
            matrix4fMul.transform(vector4f);

            if (vector4f.w > 0.0f) {
                pointsInFront++;
            }

            float w= Math.max(0.0001f, vector4f.w);
            float f= vector4f.x / w;
            float f2= vector4f.y / w;

            float sx= (f + 1.0f) * 0.5f * fw;
            float sy= (1.0f - f2) * 0.5f * fh;

            minX = Math.min(minX, sx);
            minY = Math.min(minY, sy);
            maxX = Math.max(maxX, sx);
            maxY = Math.max(maxY, sy);
        }

        if (pointsInFront == 0) {
            return Optional.empty();
        }
        if (maxX < 0.0f || minX > fw || maxY < 0.0f || minY > fh) {
            return Optional.empty();
        }

        float fMax= Math.max(0.0f, maxX - minX);
        float fMax2= Math.max(0.0f, maxY - minY);
        return (fMax == 0.0f && fMax2 == 0.0f) ? Optional.empty() : Optional.of(new Vector4f(Math.round(minX), Math.round(minY), Math.round(fMax), Math.round(fMax2)));
    }

    public static float centerX(Vector4f vector4f) {
        return vector4f.x + (vector4f.z / 2.0f);
    }

    public ProjectionUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
