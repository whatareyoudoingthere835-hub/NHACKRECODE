package aethereal.graphics;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.WindowFramebuffer;

public final class ScaledRenderTarget {
    public final int scaleFactor;
    public Framebuffer framebuffer;
    public boolean initialized;
    public final MinecraftClient client = MinecraftClient.getInstance();
    public final List<Runnable> renderQueue = new ArrayList();

    public ScaledRenderTarget(int i) {
        this.scaleFactor = Math.max(1, i);
    }

    public void init() {
        if (this.initialized) {
            return;
        }
        ensureFramebuffer();
        this.initialized = true;
    }

    public void add(Runnable runnable) {
        ensureInitialized();
        if (runnable != null) {
            this.renderQueue.add(runnable);
        }
    }

    public void addAll(Runnable... runnableArr) {
        ensureInitialized();
        if (runnableArr == null) {
            return;
        }
        for (Runnable runnable : runnableArr) {
            if (runnable != null) {
                this.renderQueue.add(runnable);
            }
        }
    }

    public void clearQueue() {
        this.renderQueue.clear();
    }

    public void renderToFramebuffer() {
        ensureInitialized();
        ensureFramebuffer();
        FrameBufferUtils.renderTo(this.framebuffer, () -> {
            Iterator<Runnable> it= this.renderQueue.iterator();
            while (it.hasNext()) {
                it.next().run();
            }
        });
        clearQueue();
    }

    public Framebuffer getFramebuffer() {
        ensureInitialized();
        ensureFramebuffer();
        return this.framebuffer;
    }

    public void ensureFramebuffer() {
        int iMax= Math.max(1, this.client.getWindow().getFramebufferWidth() / this.scaleFactor);
        int iMax2= Math.max(1, this.client.getWindow().getFramebufferHeight() / this.scaleFactor);
        this.framebuffer = FrameBufferUtils.ensureFramebuffer(this.framebuffer, iMax, iMax2, () -> {
            return new WindowFramebuffer(iMax, iMax2);
        });
        FrameBufferUtils.resizeIfNeeded(this.framebuffer, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
    }

    public void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        init();
    }
}
