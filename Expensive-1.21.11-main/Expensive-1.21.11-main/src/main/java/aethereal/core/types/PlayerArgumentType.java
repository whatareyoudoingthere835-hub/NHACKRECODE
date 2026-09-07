package aethereal.core.types;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class PlayerArgumentType implements ArgumentParser<AbstractClientPlayerEntity> {
    @Override
    public AbstractClientPlayerEntity parse(String str) throws TranslatedException {
        MinecraftClient minecraftClient= MinecraftClient.getInstance();
        if (minecraftClient.world == null) {
            throw new TranslatedException(Lang.TYPE_WORLD_NOT_LOADED);
        }
        return (AbstractClientPlayerEntity) minecraftClient.world.getPlayers().stream().filter(abstractClientPlayerEntity -> {
            return abstractClientPlayerEntity.getName().getString().equalsIgnoreCase(str);
        }).findFirst().orElseThrow(() -> {
            return new TranslatedException(Translation.clearText(Lang.TYPE_PLAYER_NOT_FOUND.effective().replace("{input}", str)));
        });
    }

    @Override
    public List<String> getSuggestions(String str) {
        MinecraftClient minecraftClient= MinecraftClient.getInstance();
        if (minecraftClient.world == null) {
            return List.of();
        }
        String lowerCase= str.toLowerCase();
        return (List) minecraftClient.world.getPlayers().stream().map(abstractClientPlayerEntity -> {
            return abstractClientPlayerEntity.getName().getString();
        }).filter(str2 -> {
            return str2.toLowerCase().startsWith(lowerCase);
        }).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "player";
    }
}
