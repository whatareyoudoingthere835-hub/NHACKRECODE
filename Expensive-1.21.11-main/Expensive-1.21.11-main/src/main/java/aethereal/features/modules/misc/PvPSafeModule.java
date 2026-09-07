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

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;

@Aliases(aliases = {"PvP Safe", "Command Blocker", "Safe Commands", "Anti Hub", "Block Commands", "Command Guard"})
public class PvPSafeModule extends Module {
    public final BooleanSetting blockHub;
    public final BooleanSetting blockAnarchy;
    static final Pattern hubPattern = Pattern.compile("^(hub|lobby|leave|quit|exit)(\\s.*)?$", 2);
    static final Pattern anarchyPattern = Pattern.compile("^an(archy)?(\\s*\\d+)(\\s.*)?$", 2);

    public PvPSafeModule() {
        super(ModuleTab.MISC, "PvP Safe");
        this.blockHub = new BooleanSetting(Lang.PVPSAFE_BLOCK_HUB, Lang.PVPSAFE_BLOCK_HUB_DESC).setValue(true);
        this.blockAnarchy = new BooleanSetting(Lang.PVPSAFE_BLOCK_ANARCHY, Lang.PVPSAFE_BLOCK_ANARCHY_DESC).setValue(true);
        addSettings(this.blockHub, this.blockAnarchy);
        register(PacketSendEvent.class, class037Var -> {
            if (isState()) {
                if ((class037Var.getPacket()) instanceof CommandExecutionC2SPacket packet ) {
                    try {
                        String strTrim= packet.command().trim();
                        if (isBlockedCommand(strTrim) && PvPModeDetector.isPvPMode()) {
                            class037Var.cancel();
                            Expensive.INSTANCE.notificationRepository().post(NotificationType.WARNING, (Text) Text.literal(Lang.PVPSAFE_BLOCKED_NOTIFICATION.effective().replace("{cmd}", "/" + strTrim.split("\\s+")[0])), 4L, TimeUnit.SECONDS);
                        }
                    } catch (Throwable th) {
                        throw new MatchException(th.toString(), th);
                    }
                }
            }
        });
    }

    public boolean isBlockedCommand(String str) {
        if (this.blockHub.isValue() && hubPattern.matcher(str).matches()) {
            return true;
        }
        return this.blockAnarchy.isValue() && anarchyPattern.matcher(str).matches();
    }
}
