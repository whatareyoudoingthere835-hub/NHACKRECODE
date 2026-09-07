package aethereal.core.models;
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

import java.util.List;
import java.util.stream.Collectors;

public class GifPlayer {
    public final List<TextureAnimationFrame> frames;
    public int currentFrame = 0;
    public long lastFrameTime = System.currentTimeMillis();

    public GifPlayer(ResourceSource class178Var) {
        this.frames = loadFrames(class178Var);
    }

    public List<TextureAnimationFrame> loadFrames(ResourceSource class178Var) {
        return (List) new GifDecoder(class178Var.stream()).frames().stream().map(class333Var -> {
            return new TextureAnimationFrame(class333Var, new GlTextureObject(ImageBufferUtil.convertToByteBuffer(class333Var.image())).setDimensions(class333Var.image().getWidth(), class333Var.image().getHeight()));
        }).collect(Collectors.toList());
    }

    public void updateFrame() {
        if (System.currentTimeMillis() - this.lastFrameTime >= this.frames.get(this.currentFrame).delay()) {
            this.currentFrame = (this.currentFrame + 1) % this.frames.size();
            this.lastFrameTime = System.currentTimeMillis();
        }
    }

    public GlTextureObject currentImage() {
        if (this.frames.isEmpty()) {
            throw new IllegalStateException("No frames loaded");
        }
        return this.frames.get(this.currentFrame).texture();
    }

    public int getCurrentFrame() {
        return this.currentFrame;
    }
}
