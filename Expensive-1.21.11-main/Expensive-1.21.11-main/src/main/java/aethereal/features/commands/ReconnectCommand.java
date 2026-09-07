package aethereal.features.commands;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReconnectCommand implements ClientCommand {
    public ReconnectTask activeTask;
    public final List<ReconnectHandler> handlers = new ArrayList();

    public final Mc mc = Mc.INSTANCE;

    public ReconnectCommand() {
        this.handlers.add(new FunTimeServerHandler());
        this.handlers.add(new HolyWorldServerHandler());
        this.handlers.add(new ReallyWorldServerHandler());
        Expensive.INSTANCE.eventDispatcher().register(PlayerTickEvent.class, class130Var -> {
            if (this.activeTask != null) {
                try {
                    this.activeTask.tick();
                } catch (ReconnectException e) {
                    ChatUtil.addChatMessage((Text) Text.literal(Lang.COMMAND_ERROR_PREFIX.effective().replace("{error}", e.getMessage())).formatted(Formatting.RED));
                }
                if (this.activeTask.isComplete()) {
                    this.activeTask = null;
                }
            }
        });
        Expensive.INSTANCE.eventDispatcher().register(HandledScreenRenderEvent.class, class015Var -> {
            if (this.activeTask != null) {
                try {
                    this.activeTask.handledScreenTick();
                } catch (ReconnectException e) {
                    ChatUtil.addChatMessage((Text) Text.literal(Lang.COMMAND_ERROR_PREFIX.effective().replace("{error}", e.getMessage())).formatted(Formatting.RED));
                }
            }
        });
    }

    @Override
    public void execute(CommandContext class392Var) throws TranslatedException {
        if (!this.mc.isWorldLoaded()) {
            throw new TranslatedException(Lang.COMMAND_WORLD_NOT_LOADED);
        }
        if (this.mc.isSingleplayer()) {
            throw new TranslatedException(Lang.COMMAND_SINGLEPLAYER_ONLY);
        }
        for (ReconnectHandler class036Var : this.handlers) {
            Iterator<String> it= class036Var.getServerIds().iterator();
            while (it.hasNext()) {
                if (ServerUtil.isConnectedToServer(it.next())) {
                    this.activeTask = class036Var.createProcess();
                    return;
                }
            }
        }
        throw new TranslatedException(Lang.COMMAND_UNSUPPORTED_SERVER);
    }

    @Override
    public String getName() {
        return "rct";
    }

    @Override
    public Translation getDescription() {
        return Lang.COMMAND_RCT_DESC;
    }

    @Override
    public String getUsage() {
        return ".rct";
    }

    @Override
    public List<String> getAliases() {
        return List.of("reconnect");
    }

    @Override
    public List<String> getSuggestions(String[] strArr, int i) {
        return List.of();
    }
}
