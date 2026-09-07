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

import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class PopChamsModel {
    public final PlayerEntityModel model = new PlayerEntityModel(Mc.INSTANCE.createEntityRendererContext().getPart(EntityModelLayers.PLAYER), false);

    public final PlayerEntity player;
    public final ToggleAnimator animation;
    public final Identifier texture;
    public final boolean textured;
    public final int color;

    public PopChamsModel(PlayerEntity playerEntity, Identifier identifier, int i, boolean z) {
        this.model.getHead().scale(new Vector3f(-0.2f, -0.2f, -0.2f));
        this.animation = new ToggleAnimator(3000, Easings.LINEAR);
        this.animation.force(true);
        this.texture = identifier;
        this.player = playerEntity;
        this.color = i;
        this.textured = z;
    }

    public void animate(WeightedEngine class141Var) {
        this.animation.animate(class141Var);
    }

    public boolean tick() {
        this.animation.state(false);
        if (!this.animation.isZero()) {
            return false;
        }
        this.player.remove(Entity.RemovalReason.KILLED);
        this.player.onRemoved();
        return true;
    }

    public PlayerEntityModel getModel() {
        return this.model;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public ToggleAnimator getAnimation() {
        return this.animation;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public boolean isTextured() {
        return this.textured;
    }

    public int getColor() {
        return this.color;
    }
}
