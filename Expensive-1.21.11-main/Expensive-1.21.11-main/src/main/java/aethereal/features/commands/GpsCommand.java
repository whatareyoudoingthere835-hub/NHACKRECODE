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

import java.util.List;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GpsCommand implements ClientCommand {
    @Override
    public String getName() {
        return "gps";
    }

    @Override
    public Translation getDescription() {
        return Lang.COMMAND_GPS_DESC;
    }

    @Override
    public String getUsage() {
        return ".gps <x> <z>\n.gps off";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public void execute(CommandContext class392Var) throws TranslatedException {
        String[] strArrArgs= class392Var.args();
        if (!Mc.INSTANCE.isWorldLoaded()) {
            throw new TranslatedException(Lang.COMMAND_WORLD_NOT_LOADED);
        }
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        ArrowsModule class535VarMethod001= getArrowsModule();
        if (strArrArgs.length == 1 && strArrArgs[0].equalsIgnoreCase("off")) {
            class535VarMethod001.disableGPS();
            ChatUtil.addChatMessage((Text) Text.literal(Lang.COMMAND_GPS_DISABLED.effective()).formatted(Formatting.GRAY));
        } else {
            if (strArrArgs.length != 2) {
                throw new TranslatedException(Translation.clearText(Lang.COMMAND_INVALID_ARG_COUNT.effective().replace("{usage}", getUsage())));
            }
            int iMethod002= parseCoordinate(strArrArgs[0], player.getX());
            int iMethod003= parseCoordinate(strArrArgs[1], player.getZ());
            class535VarMethod001.setGPS(iMethod002, iMethod003);
            ChatUtil.addChatMessage((Text) Text.literal(Lang.COMMAND_GPS_ENABLED.effective().replace("{x}", String.valueOf(iMethod002)).replace("{z}", String.valueOf(iMethod003))).formatted(Formatting.GRAY));
        }
    }

    public int parseCoordinate(String str, double d) throws TranslatedException {
        if (str.equals("~")) {
            return (int) Math.floor(d);
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new TranslatedException(Translation.clearText(Lang.COMMAND_INVALID_COORDINATE.effective().replace("{input}", str)));
        }
    }

    public ArrowsModule getArrowsModule() {
        return (ArrowsModule) Expensive.INSTANCE.moduleRepository().get(ArrowsModule.class);
    }

    @Override
    public List<String> getSuggestions(String[] strArr, int i) {
        return (i != 0 || strArr.length > 1) ? List.of("~") : List.of("off", "~");
    }
}
