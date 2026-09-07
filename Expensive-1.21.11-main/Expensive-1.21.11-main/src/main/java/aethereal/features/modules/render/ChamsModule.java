package aethereal.features.modules.render;
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

import net.minecraft.entity.Entity;

@Aliases(aliases = {"Chams", "Model", "Entity ESP", "ESP"})
public class ChamsModule extends Module {
    public final MultiSelectSetting<ChamsTargetType> selectTargets;
    public final ColorSetting colorSetting;
    public final BooleanSetting blendingSetting;
    public Entity currentEntity;

    public ChamsModule() {
        super(ModuleTab.RENDER, "Chams");
        this.selectTargets = new MultiSelectSetting(Lang.SELECTTARGETS, Lang.SELECTTARGETS_DESC).values(ChamsTargetType.class);
        this.colorSetting = new ColorSetting(Lang.CHAMS_COLOR);
        this.blendingSetting = new BooleanSetting(Lang.CHAMS_BLENDING);
        this.currentEntity = null;
        addSettings(this.selectTargets, this.colorSetting, this.blendingSetting);
    }

    public boolean shouldRender(Entity entity) {
        if (entity == null) {
            return false;
        }
        EntityFilter class095VarMethod001= buildEntityFilter();
        return !class095VarMethod001.isEmpty() && class095VarMethod001.matches(entity);
    }

    public EntityFilter buildEntityFilter() {
        EntityFilter class095Var= new EntityFilter();
        if (this.selectTargets.isSelected(ChamsTargetType.SELF)) {
            class095Var.add(EntityCategory.SELF);
        }
        if (this.selectTargets.isSelected(ChamsTargetType.PLAYERS)) {
            class095Var.add(EntityCategory.PLAYER);
        }
        if (this.selectTargets.isSelected(ChamsTargetType.FRIENDS)) {
            class095Var.add(EntityCategory.FRIEND);
        }
        if (this.selectTargets.isSelected(ChamsTargetType.MOBS)) {
            class095Var.add(EntityCategory.MOB);
        }
        if (this.selectTargets.isSelected(ChamsTargetType.ANIMALS)) {
            class095Var.add(EntityCategory.ANIMAL);
        }
        return class095Var;
    }

    public MultiSelectSetting<ChamsTargetType> selectTargets() {
        return this.selectTargets;
    }

    public ColorSetting colorSetting() {
        return this.colorSetting;
    }

    public BooleanSetting blendingSetting() {
        return this.blendingSetting;
    }

    public Entity currentEntity() {
        return this.currentEntity;
    }

    public ChamsModule currentEntity(Entity entity) {
        this.currentEntity = entity;
        return this;
    }
}
