package thunder.hack.utility.render;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;

/**
 * Replacement for the removed vanilla PoseStack (1.21.9+). Backed by a joml
 * Matrix4fStack but keeps the old push/pop/translate(double)/scale/last() surface
 * so existing render code compiles unchanged.
 */
public class PoseStack extends Matrix4fStack {
    public PoseStack() {
        super(64);
    }

    public void push() {
        pushMatrix();
    }

    public void pop() {
        popMatrix();
    }

    public void translate(double x, double y, double z) {
        translate((float) x, (float) y, (float) z);
    }

    public void translate(float x, float y) {
        translate(x, y, 0f);
    }

    public void translate(double x, double y) {
        translate((float) x, (float) y, 0f);
    }

    public void scale(double x, double y, double z) {
        scale((float) x, (float) y, (float) z);
    }

    public void scale(double s) {
        scale((float) s, (float) s, (float) s);
    }

    @Override
    public boolean multiply(Quaternionfc quaternion) {
        return super.multiply(quaternion);
    }

    public void multiply(Matrix4f matrix) {
        super.multiply(matrix);
    }

    public void multiplyCurrent(Matrix4f matrix) {
        super.multiply(matrix);
    }

    public Matrix4f last() {
        return new Matrix4f().set(this);
    }

    public Entry peek() {
        return new Entry(new Matrix4f().set(this), new Matrix3f());
    }

    public Entry entry() {
        return peek();
    }

    public static final class Entry {
        private final Matrix4f position;
        private final Matrix3f normal;

        Entry(Matrix4f position, Matrix3f normal) {
            this.position = position;
            this.normal = normal;
        }

        public Matrix4f getPositionMatrix() {
            return position;
        }

        public Matrix3f getNormalMatrix() {
            return normal;
        }
    }

    /** vertex helper mirroring the old VertexConsumer.vertex(Matrix4f, ...) shape */
    public static void vertex(VertexConsumer consumer, PoseStack stack, float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
        consumer.vertex(stack, x, y, z).color(color).texture(u, v).overlay(overlay).light(light).normal(nx, ny, nz);
    }
}
