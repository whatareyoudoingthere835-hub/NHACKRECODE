package thunder.hack.core.manager.client;

import thunder.hack.core.manager.IManager;

/**
 * Stub for 1.21.11.
 * <p>
 * The old satin-based post process pipeline (JsonEffectShaderProgram / PostEffectProcessor
 * framebuffer swapping) has no 1.21.11 equivalent yet; custom shaders must be ported to
 * {@code PostEffectPipeline}. Until then this manager is a no-op so every "outline/blur"
 * style effect just renders normally.
 */
public class ShaderManager implements IManager {

    public float time = 0;

    public void renderShader(Runnable runnable, Shader mode) {
        // no shader buffer to render into: run the task normally
        runnable.run();
    }

    public void renderShaders() {
    }

    public void applyShader(Runnable runnable, Shader mode) {
        runnable.run();
    }

    public void reloadShaders() {
    }

    public void setupShader(Shader mode, Object shader) {
    }

    public Object getShaderOutline(Shader mode) {
        return null;
    }

    public Object getShader(Shader mode) {
        return null;
    }

    public void fade(int mode) {
    }

    public boolean isShaderEnabled() {
        return false;
    }

    public boolean fullNullCheck() {
        return false;
    }

    public enum Shader {
        Outline("Outline"),
        Smoke("Smoke"),
        Gradient("Gradient"),
        Snow("Snow"),
        Fade("Fade"),
        Blur("Blur"),
        Default("Default");

        public final String name;

        Shader(String name) {
            this.name = name;
        }
    }
}
