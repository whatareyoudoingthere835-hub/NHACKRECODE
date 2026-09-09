package thunder.hack.utility.render;

import net.minecraft.client.gl.Framebuffer;

/**
 * 1.21.11: the GlFramebuffer/MSAA path was reworked; supersampling pass is a passthrough now.
 */
public final class MSAAFramebuffer {
    private MSAAFramebuffer() {
    }

    public static void use(boolean fancy, Runnable drawAction) {
        drawAction.run();
    }

    public static void use(int samples, Framebuffer mainBuffer, Runnable drawAction) {
        drawAction.run();
    }
}
