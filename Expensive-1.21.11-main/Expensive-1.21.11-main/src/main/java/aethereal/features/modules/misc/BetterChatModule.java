package aethereal.features.modules.misc;
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
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

@Aliases(aliases = {"Better Chat", "Chat History", "Anti Spam", "Infinite Chat"})
public class BetterChatModule extends Module {
    public static final String externalSuffix = "-external";
    public final MultiSelectSetting<BetterChatOption> chatImprovements;
    public final Map<String, TimestampedCounter> messageCounters;
    public final long spamWindowMs = 10000;

    public BetterChatModule() {
        super(ModuleTab.MISC, "Better Chat");
        this.chatImprovements = new MultiSelectSetting(Lang.BETTERCHAT_CHAT_IMPROVEMENTS).values(BetterChatOption.class);
        this.messageCounters = new HashMap();
        addSettings(this.chatImprovements);
        register(ChatReceiveEvent.class, this::onChatReceive);
        register(ChatLimitEvent.class, this::onChatLimit);
    }

    public void onChatReceive(ChatReceiveEvent class066Var) {
        Mc class815Var= Mc.INSTANCE;
        if (isState() && class815Var.isWorldLoaded() && class066Var.type() != ChatMessageType.DISGUISED_CHAT_MESSAGE && this.chatImprovements.isSelected(BetterChatOption.ANTI_SPAM)) {
            InGameHud inGameHud= class815Var.getInGameHud();
            class066Var.cancel();
            String str= TextVisitFactory.removeFormattingCodes(class066Var.textData()) + "-external";
            ChatHud chatHud= inGameHud.getChatHud();
            long jCurrentTimeMillis= System.currentTimeMillis();
            pruneExpiredCounters(jCurrentTimeMillis);
            incrementCounter(str, jCurrentTimeMillis);
            int i= this.messageCounters.get(str).count;
            MutableText mutableTextLiteral= Text.literal("");
            mutableTextLiteral.append(class066Var.applyChatDecoration().apply(class066Var.textData()));
            if (i > 1) {
                mutableTextLiteral.append(Text.literal(" ").formatted(Formatting.GRAY).append("[x" + i + "]"));
            }
            ChatStackEntry class085Var= new ChatStackEntry(false, str, true, i);
            if (class085Var.remove() && StringUtils.isNotEmpty(class085Var.id())) {
                ChatUtil.removeMessage(chatHud, class085Var.id());
            }
            ChatUtil.addMessage(chatHud, mutableTextLiteral, class085Var.id());
        }
    }

    public void pruneExpiredCounters(long j) {
        this.messageCounters.entrySet().removeIf(entry -> {
            return j - ((TimestampedCounter) entry.getValue()).timestamp > 10000;
        });
    }

    public void incrementCounter(String str, long j) {
        this.messageCounters.compute(str, (str2, class482Var) -> {
            if (class482Var == null) {
                return new TimestampedCounter(1, j);
            }
            class482Var.count++;
            class482Var.timestamp = j;
            return class482Var;
        });
    }

    public void onChatLimit(ChatLimitEvent class056Var) {
        if (isState() && Mc.INSTANCE.isWorldLoaded() && class056Var != null) {
            ChatLimitType type= class056Var.getType();
            if (type != null && ((type.isHistory() && this.chatImprovements.isSelected(BetterChatOption.ANTI_CLEAR)) || (type.isLimit() && this.chatImprovements.isSelected(BetterChatOption.INFINITY)))) {
                class056Var.cancel();
            }
        }
    }
}
