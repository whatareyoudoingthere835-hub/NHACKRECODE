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

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class StencilBufferUtil {
    public static final int STENCIL_MASK = 255;
    public static final int NON_EQUALS = 0;
    public static final int EQUALS = 1;
    public static int framebufferId = -1;
    public static int renderbufferId = -1;
    public static int width = -1;
    public static int height = -1;
    public static int textureId = -1;
    public static int previousFramebuffer = -1;
    public static final int[] savedViewport = {0, 0, 0, 0};

    private static final Map<Long, StencilEntry> cache = new HashMap<>();

    private static class StencilEntry {
        int framebufferId = -1;
        int renderbufferId = -1;
        int width = -1;
        int height = -1;
        int textureId = -1;

        void init(int w, int h, int tex) {
            if (this.framebufferId != -1) {
                GL30.glDeleteFramebuffers(this.framebufferId);
                GL30.glDeleteRenderbuffers(this.renderbufferId);
            }
            this.framebufferId = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, this.framebufferId);
            this.textureId = tex;
            GL30.glFramebufferTexture2D(36160, 36064, 3553, this.textureId, 0);
            this.renderbufferId = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(36161, this.renderbufferId);
            GL30.glRenderbufferStorage(36161, 35056, w, h);
            GL30.glBindRenderbuffer(36161, 0);
            GL30.glFramebufferRenderbuffer(36160, 33306, 36161, this.renderbufferId);
            int status= GL30.glCheckFramebufferStatus(36160);
            if (status != 36053) {
                throw new IllegalStateException("Stencil framebuffer error: 0x" + Integer.toHexString(status));
            }
            this.width = w;
            this.height = h;
        }

        void cleanup() {
            if (this.framebufferId != -1) {
                GL30.glDeleteFramebuffers(this.framebufferId);
                GL30.glDeleteRenderbuffers(this.renderbufferId);
                this.framebufferId = -1;
                this.renderbufferId = -1;
            }
        }
    }

    public static void initFramebuffer(int i, int i2, int i3) {
        long key= (((long) i3) << 32) | (((long) (i & 0xFFFF)) << 16) | ((long) (i2 & 0xFFFF));
        StencilEntry entry= cache.computeIfAbsent(key, k -> new StencilEntry());
        entry.init(i, i2, i3);
        framebufferId = entry.framebufferId;
        renderbufferId = entry.renderbufferId;
        width = entry.width;
        height = entry.height;
        textureId = entry.textureId;
    }

    public static void prepareStencil() {
        GL11.glGetIntegerv(2978, savedViewport);
        int i= savedViewport[2];
        int i2= savedViewport[3];
        previousFramebuffer = GL11.glGetInteger(36006);
        int iGlGetFramebufferAttachmentParameteri= GL30.glGetFramebufferAttachmentParameteri(36009, 36064, 36049);
        if (cache.size() > 16) {
            for (StencilEntry entry : cache.values()) {
                entry.cleanup();
            }
            cache.clear();
        }
        long key= (((long) iGlGetFramebufferAttachmentParameteri) << 32) | (((long) (i & 0xFFFF)) << 16) | ((long) (i2 & 0xFFFF));
        StencilEntry entry= cache.get(key);
        if (entry == null || entry.framebufferId == -1 || entry.textureId != iGlGetFramebufferAttachmentParameteri || entry.width != i || entry.height != i2 || (iGlGetFramebufferAttachmentParameteri > 0 && !GL11.glIsTexture(iGlGetFramebufferAttachmentParameteri))) {
            if (entry == null) {
                entry = new StencilEntry();
                cache.put(key, entry);
            }
            entry.init(i, i2, iGlGetFramebufferAttachmentParameteri);
        }
        framebufferId = entry.framebufferId;
        renderbufferId = entry.renderbufferId;
        width = entry.width;
        height = entry.height;
        textureId = entry.textureId;
        GL30.glBindFramebuffer(36160, framebufferId);
        GL11.glViewport(0, 0, i, i2);
        GL11.glClear(1024);
        GL11.glEnable(2960);
        GL11.glStencilFunc(519, 1, STENCIL_MASK);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
    }

    public static void prepareElement(int i) {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glStencilFunc(514, i, STENCIL_MASK);
        GL11.glStencilOp(7680, 7680, 7680);
    }

    public static void cleanup() {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL30.glBindFramebuffer(36160, previousFramebuffer == -1 ? 0 : previousFramebuffer);
        GL11.glViewport(savedViewport[0], savedViewport[1], savedViewport[2], savedViewport[3]);
        previousFramebuffer = -1;
    }

    public StencilBufferUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
