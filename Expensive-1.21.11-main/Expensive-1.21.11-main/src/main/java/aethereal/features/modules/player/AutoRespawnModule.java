package aethereal.features.modules.player;
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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;

@Aliases(aliases = {"Auto Respawn", "Automatic Respawn", "Fast Respawn", "Quick Respawn", "Auto Revive"})
public class AutoRespawnModule extends Module {
    public final NumberSetting respawnDelay;

    public AutoRespawnModule() {
        super(ModuleTab.PLAYER, "AutoRespawn");
        this.respawnDelay = new NumberSetting(Lang.AUTORESPAWN_DELAY).currentValue(20.0f).range(0.0f, 70.0f).step(1.0f).unit(SettingUnit.TICKS);
        addSettings(this.respawnDelay);
        register(DeathTickEvent.class, class276Var -> {
            Mc class815Var;
            ClientPlayerEntity player;
            if (!isState() || class276Var.ticksSinceDeath() <= Math.round(this.respawnDelay.currentValue()) || (player = (class815Var = Mc.INSTANCE).getPlayer()) == null) {
                return;
            }
            player.requestRespawn();
            class815Var.getMinecraft().setScreen((Screen) null);
        });
    }
}
