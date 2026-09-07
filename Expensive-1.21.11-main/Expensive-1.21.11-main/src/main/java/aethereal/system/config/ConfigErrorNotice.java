package aethereal.system.config;
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

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import java.net.URI;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ConfigErrorNotice {
    public static final String discordUrl = "https://discord.gg/gPQ7yBY9zy";

    public ConfigErrorNotice() {
    }

    public static Text withDiscord() {
        return withDiscord("сохранении");
    }

    public static Text withDiscord(String str) {
        return Text.literal("").append(Text.literal("Ошибка конфигурации ").formatted(Formatting.RED)).append(Text.literal("[Исправить]").setStyle(Text.literal("[Исправить]").getStyle().withColor(Formatting.GREEN).withHoverEvent(new HoverEvent.ShowText(Text.literal("").append(Text.literal("Произошла ошибка при %s конфигурации.\n".formatted(str)).formatted(Formatting.GRAY)).append(Text.literal("Пожалуйста, отправьте latest.log файл\n").formatted(Formatting.GRAY)).append(Text.literal("администрации в Discord.\n\n").formatted(Formatting.GRAY)).append(Text.literal("Нажмите, чтобы перейти на сервер.").formatted(Formatting.YELLOW)))).withClickEvent(new ClickEvent.OpenUrl(URI.create(discordUrl)))));
    }
}
