package aethereal.gui;
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

import java.util.function.Supplier;
import java.util.WeakHashMap;
import java.util.Map;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public final class FrameBufferUtils {
    private static final Map<Framebuffer, Integer> framebufferIds = new WeakHashMap<>();
    
    public static Framebuffer ensureFramebuffer(Framebuffer framebuffer, int i, int i2, Supplier<Framebuffer> supplier) {
        if (framebuffer == null) {
            framebuffer = supplier.get();
            clearTransparent(framebuffer);
        }
        setLinearTextureFilter(framebuffer);
        return framebuffer;
    }

    public static void resizeIfNeeded(Framebuffer framebuffer, int i, int i2) {
        if (framebuffer.textureWidth == i && framebuffer.textureHeight == i2) {
            return;
        }
        Integer oldId= framebufferIds.remove(framebuffer);
        if (oldId != null && oldId > 0) {
            GL30.glDeleteFramebuffers(oldId);
        }
        framebuffer.resize(i, i2);
        clearTransparent(framebuffer);
        setLinearTextureFilter(framebuffer);
    }

    public static void clearTransparent(Framebuffer framebuffer) {
        renderTo(framebuffer, () -> {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        });
    }

    public static void setLinearTextureFilter(Framebuffer framebuffer) {
        int colorId= getColorAttachmentId(framebuffer);
        if (colorId <= 0) {
            return;
        }
        int iGlGetInteger= GL11.glGetInteger(34016);
        int iGlGetInteger2= GL11.glGetInteger(32873);
        GL11.glBindTexture(3553, colorId);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33071);
        GL11.glTexParameteri(3553, 10243, 33071);
        GL11.glBindTexture(3553, iGlGetInteger2);
        GL13.glActiveTexture(iGlGetInteger);
    }

    public static int getColorAttachmentId(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.getColorAttachment() == null) {
            return 0;
        }
        return ((net.minecraft.client.texture.GlTexture) framebuffer.getColorAttachment()).getGlId();
    }

    public static int glId(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.getColorAttachment() == null) {
            return 0;
        }
        return ((net.minecraft.client.texture.GlTexture) framebuffer.getColorAttachment()).getGlId();
    }

    public static int getTextureId(net.minecraft.client.texture.AbstractTexture texture) {
        if (texture == null || texture.getGlTexture() == null) {
            return 0;
        }
        return ((net.minecraft.client.texture.GlTexture) texture.getGlTexture()).getGlId();
    }

    public static int glId(GlTextureObject texture) {
        if (texture == null) {
            return 0;
        }
        return texture.textureWithSTB();
    }

    public static void renderTo(Framebuffer framebuffer, Runnable renderer) {
        int previousFramebuffer= GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int[] viewport= new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        bindForRendering(framebuffer);
        try {
            renderer.run();
        } finally {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);
            GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
        }
    }

    public static void bindForRendering(Framebuffer framebuffer) {
        if (framebuffer == null) {
            return;
        }
        int framebufferId= framebufferIds.computeIfAbsent(framebuffer, ignored -> GL30.glGenFramebuffers());
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferId);
        int colorId= getColorAttachmentId(framebuffer);
        if (colorId > 0) {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorId, 0);
        }
        GL11.glViewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);
    }

    public FrameBufferUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
