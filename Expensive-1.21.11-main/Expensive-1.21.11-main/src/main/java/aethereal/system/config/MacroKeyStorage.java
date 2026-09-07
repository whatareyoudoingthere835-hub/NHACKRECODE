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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroKeyStorage {
    public final List<MacroEntry> macroList = new ArrayList();
    public static final Logger logger = LoggerFactory.getLogger(MacroKeyStorage.class);

    public MacroKeyStorage() {
        Expensive.INSTANCE.eventDispatcher().register(KeyInputEvent.class, class049Var -> {
            if (Mc.INSTANCE.isWorldLoaded() && !this.macroList.isEmpty() && class049Var.action() == KeyPressState.PRESS) {
                int iKey= class049Var.key();
                this.macroList.stream().filter(class724Var -> {
                    return class724Var.key() == iKey;
                }).findFirst().ifPresent(class724Var2 -> {
                    try {
                        if (class724Var2.content().startsWith("/")) {
                            Mc.INSTANCE.getNetworkHandler().sendChatCommand(class724Var2.content().substring(1));
                        } else {
                            Mc.INSTANCE.getNetworkHandler().sendChatMessage(class724Var2.content());
                        }
                    } catch (Exception e) {
                        logger.error("Failed to send macro command {}", class724Var2.name(), e);
                        ChatUtil.addChatMessage("Ошибка при отправке команды. См. latest.log");
                    }
                });
            }
        });
    }

    public boolean isEmpty() {
        return this.macroList.isEmpty();
    }

    public void addMacro(String str, String str2, int i) {
        this.macroList.add(new MacroEntry(str, str2, i));
        try {
            Expensive.INSTANCE.configManager().saveMacros();
        } catch (IOException e) {
            logger.error("Failed to persist macros after addMacro(name={})", str, e);
            ChatUtil.addChatMessage(ConfigErrorNotice.withDiscord("сохранении макросов"));
        }
    }

    public boolean hasMacro(String str) {
        Iterator<MacroEntry> it= this.macroList.iterator();
        while (it.hasNext()) {
            if (it.next().name().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public void deleteMacro(String str) {
        if (this.macroList.stream().anyMatch(class724Var -> {
            return class724Var.name().equals(str);
        })) {
            this.macroList.removeIf(class724Var2 -> {
                return class724Var2.name().equalsIgnoreCase(str);
            });
            try {
                Expensive.INSTANCE.configManager().saveMacros();
            } catch (IOException e) {
                logger.error("Failed to persist macros after deleteMacro(name={})", str, e);
                ChatUtil.addChatMessage(ConfigErrorNotice.withDiscord("удалении макроса"));
            }
        }
    }

    public void clearList() {
        if (this.macroList.isEmpty()) {
            return;
        }
        this.macroList.clear();
        try {
            Expensive.INSTANCE.configManager().saveMacros();
        } catch (IOException e) {
            logger.error("Failed to persist macros after clearList()", e);
            ChatUtil.addChatMessage(ConfigErrorNotice.withDiscord("очистке макросов"));
        }
    }

    public List<MacroEntry> getMacroList() {
        return this.macroList;
    }
}
