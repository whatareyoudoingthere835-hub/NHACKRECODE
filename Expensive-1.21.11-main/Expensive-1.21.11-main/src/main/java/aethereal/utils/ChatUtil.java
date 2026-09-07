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

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public final class ChatUtil {
    public static void removeMessage(ChatHud chatHud, String str) {
        if (!runOnClientThread(() -> removeMessage(chatHud, str))) {
            return;
        }
        chatHud.messages.removeIf(chatHudLine -> {
            return str.equals(((ChatLineIdAccessor) ChatLineIdAccessor.class.cast(chatHudLine)).expensive_ru$getId());
        });
        chatHud.visibleMessages.removeIf(visible -> {
            return str.equals(((ChatLineIdAccessor) ChatLineIdAccessor.class.cast(visible)).expensive_ru$getId());
        });
    }

    public static void addMessage(ChatHud chatHud, Text text, String str) {
        if (!runOnClientThread(() -> addMessage(chatHud, text, str))) {
            return;
        }
        ChatHudLine chatHudLine= new ChatHudLine(Mc.INSTANCE.getInGameHud().getTicks(), text, (MessageSignatureData) null, Mc.INSTANCE.isSingleplayer() ? MessageIndicator.singlePlayer() : MessageIndicator.system());
        ((ChatLineIdAccessor) ChatLineIdAccessor.class.cast(chatHudLine)).expensive_ru$setId(str);
        chatHud.logChatMessage(chatHudLine);
        chatHud.addVisibleMessage(chatHudLine);
    }

    public static void addChatMessage(Text text) {
        if (!runOnClientThread(() -> addChatMessage(text))) {
            return;
        }
        MutableText mutableTextAppend= buildPrefix().copy().append(text);
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        if (player != null) {
            player.sendMessage(mutableTextAppend, false);
        }
    }

    public static Text gradientText(String str, int i, int i2) {
        MutableText mutableTextLiteral= Text.literal("");
        int length= str.length();
        for (int i3 = 0; i3 < length; i3++) {
            float f= i3 / (length - 1);
            int i4= (((int) ((((i >> 16) & StencilBufferUtil.STENCIL_MASK) * (1.0f - f)) + (((i2 >> 16) & StencilBufferUtil.STENCIL_MASK) * f))) << 16) | (((int) ((((i >> 8) & StencilBufferUtil.STENCIL_MASK) * (1.0f - f)) + (((i2 >> 8) & StencilBufferUtil.STENCIL_MASK) * f))) << 8) | ((int) (((i & StencilBufferUtil.STENCIL_MASK) * (1.0f - f)) + ((i2 & StencilBufferUtil.STENCIL_MASK) * f)));
            mutableTextLiteral = mutableTextLiteral.copy().append(Text.literal(String.valueOf(str.charAt(i3))).styled(style -> {
                return style.withColor(i4);
            }));
        }
        return mutableTextLiteral;
    }

    public static void addChatMessage(String str, boolean z) {
        if (!runOnClientThread(() -> addChatMessage(str, z))) {
            return;
        }
        MutableText mutableTextAppend= buildPrefix().copy().append(Text.literal(str).styled(style -> {
            return style.withColor(Formatting.WHITE);
        }));
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        if (player != null) {
            player.sendMessage(mutableTextAppend, z);
        }
    }

    public static void addChatMessage(String str) {
        addChatMessage(str, false);
    }

    public static void addChatMessageWithPassword(String str, String str2) {
        if (!runOnClientThread(() -> addChatMessageWithPassword(str, str2))) {
            return;
        }
        MutableText mutableTextAppend= buildPrefix().copy().append(Text.literal(str + ": ").styled(style -> {
            return style.withColor(Formatting.WHITE);
        }));
        mutableTextAppend.append(Text.literal("******").styled(style2 -> {
            return style2.withHoverEvent(new HoverEvent.ShowText(Text.literal(String.valueOf(Formatting.GRAY) + Lang.CHAT_PASSWORD.effective().replace("{password}", str2)))).withColor(TextColor.fromFormatting(Formatting.RED));
        }));
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        if (player != null) {
            player.sendMessage(mutableTextAppend, false);
        }
    }

    public static Text buildPrefix() {
        return Text.literal("").append(gradientText("Expensive Client", 5926655, 10860799)).append(Text.literal(" -> ").styled(style -> {
            return style.withColor(Formatting.DARK_GRAY);
        }));
    }

    private static boolean runOnClientThread(Runnable runnable) {
        MinecraftClient minecraftClient= MinecraftClient.getInstance();
        if (minecraftClient.isOnThread()) {
            return true;
        }
        minecraftClient.execute(runnable);
        return false;
    }

    public ChatUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
