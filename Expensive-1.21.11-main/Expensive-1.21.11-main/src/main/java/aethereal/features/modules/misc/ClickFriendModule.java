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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;

@Aliases(aliases = {"Click Friend", "Friend Manager", "Add Friend", "Remove Friend", "Toggle Friend"})
public class ClickFriendModule extends Module {
    public final KeybindSetting keybind;

    public ClickFriendModule() {
        super(ModuleTab.MISC, "Click Friend");
        this.keybind = new KeybindSetting(Lang.CLICKFRIEND_KEY);
        addSettings(this.keybind);
        this.keybind.consumer(class664Var -> {
            Mc class815Var= Mc.INSTANCE;
            if (isState() && class815Var.isWorldLoaded()) {
                if ((class815Var.getCrosshairTarget()) instanceof EntityHitResult crosshairTarget ) {
                    if ((crosshairTarget.getEntity()) instanceof PlayerEntity entity ) {
                        String string= entity.getName().getString();
                        String str= String.valueOf(Formatting.RED) + string + String.valueOf(Formatting.RESET);
                        if (FriendManager.isFriend(string)) {
                            FriendManager.remove(string);
                            Expensive.INSTANCE.notificationRepository().post(NotificationType.INFO, (Text) Text.literal(Lang.CLICKFRIEND_REMOVED.effective().replace("{name}", str)), 3L, TimeUnit.SECONDS);
                        } else {
                            FriendManager.add(string);
                            Expensive.INSTANCE.notificationRepository().post(NotificationType.INFO, (Text) Text.literal(Lang.CLICKFRIEND_ADDED.effective().replace("{name}", str)), 3L, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        });
    }
}
