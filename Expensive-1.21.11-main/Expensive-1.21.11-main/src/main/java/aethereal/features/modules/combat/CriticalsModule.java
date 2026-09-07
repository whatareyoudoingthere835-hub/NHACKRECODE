package aethereal.features.modules.combat;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CriticalsModule extends Module {
    public ModeSetting<GrimAdvancedMode> modeSetting;

    public CriticalsModule() {
        super(ModuleTab.COMBAT, "Criticals");
        this.modeSetting = new ModeSetting(Lang.MODE).values(GrimAdvancedMode.class);
        addSettings(this.modeSetting);
        register(AttackEntityEvent.class, class144Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                ServerUtil.valid1_17().ifPresentOrElse(num -> {
                    Rotation class007VarRandom= PlayerRotationManager.INSTANCE.getCurrentRotation().random(0.001f);
                    PlayerActionUtil.INSTANCE.moveBypass$$$(FastMathUtils.getRandom(0.003d, 0.004d), class007VarRandom.random(1.0f), false);
                    PlayerActionUtil.INSTANCE.moveBypass$$$(-FastMathUtils.getRandom(0.001d, 0.002d), class007VarRandom.random(0.001f), false);
                    Mc.INSTANCE.getPlayer().fallDistance = 1.0E-4f;
                }, () -> {
                    Expensive.INSTANCE.notificationRepository().post(NotificationType.ERROR, Text.of("[Criticals] Нужна версия " + String.valueOf(Formatting.RED) + "1.17-" + (ServerUtil.isConnectedToServer("holyworld") ? "1.18.2" : "1.20.6")), 3L, TimeUnit.SECONDS);
                    switchState();
                });
            }
        });
    }

    @Override
    public void activate() {
        if (ServerUtil.valid1_17().isEmpty()) {
            Expensive.INSTANCE.notificationRepository().post(NotificationType.ERROR, Text.of("[Criticals] Нужна версия " + String.valueOf(Formatting.RED) + "1.17-" + (ServerUtil.isConnectedToServer("holyworld") ? "1.18.2" : "1.20.6")), 3L, TimeUnit.SECONDS);
            switchState();
        }
        super.activate();
    }
}
