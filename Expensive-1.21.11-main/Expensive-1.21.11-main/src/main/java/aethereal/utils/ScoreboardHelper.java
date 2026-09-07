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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public class ScoreboardHelper {
    public static final ScoreboardHelper INSTANCE = new ScoreboardHelper();
    public Text header;

    public String getHeaderAsString() {
        return this.header == null ? "" : this.header.getString();
    }

    public boolean headerContains(String str) {
        return getHeaderAsString().toLowerCase().contains(str);
    }

    public float getHealthBelowName(LivingEntity livingEntity) {
        try {
            PlayerEntity clientPlayerEntity;
            ScoreAccess orCreateScore;
            if ((livingEntity instanceof PlayerEntity) && (clientPlayerEntity = (PlayerEntity) livingEntity) != Mc.INSTANCE.getPlayer()) {
                ClientWorld world= Mc.INSTANCE.getWorld();
                if (world != null && world.getScoreboard() != null) {
                    ScoreboardObjective objectiveForSlot= world.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
                    if (objectiveForSlot != null && (orCreateScore = world.getScoreboard().getOrCreateScore(ScoreHolder.fromName(livingEntity.getNameForScoreboard()), objectiveForSlot)) != null) {
                        return orCreateScore.getScore() == 0 ? clientPlayerEntity.getHealth() : orCreateScore.getScore();
                    }
                }
            }
            return livingEntity.getHealth();
        } catch (Throwable ignored) {
            return livingEntity != null ? livingEntity.getHealth() : 20.0f;
        }
    }

    public void setHeader(Text text) {
        this.header = text;
    }

    public Text getHeader() {
        return this.header;
    }
}
