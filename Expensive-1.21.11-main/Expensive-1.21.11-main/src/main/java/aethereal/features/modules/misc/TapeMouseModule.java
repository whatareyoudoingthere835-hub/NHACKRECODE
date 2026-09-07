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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

@Aliases(aliases = {"Tape Mouse", "Auto Click", "Click Assist", "Clicker", "Mouse Automation", "Auto Tap", "Hand Clicker", "Auto Hand Use"})
public class TapeMouseModule extends Module {
    public final ModeSetting<TapeMouseHand> handMode;
    public final NumberSetting delay;
    public final Stopwatch stopwatch;
    public boolean usePressed;

    public TapeMouseModule() {
        super(ModuleTab.MISC, "Tape Mouse");
        this.handMode = new ModeSetting(Lang.TAPEMOUSE_HANDMODE).values(TapeMouseHand.class);
        this.delay = new NumberSetting(Lang.TAPEMOUSE_DELAY).currentValue(1.0f).range(0.0f, 30.0f).step(1.0f).unit(SettingUnit.SECONDS);
        this.stopwatch = new Stopwatch(false);
        this.usePressed = false;
        addSettings(this.handMode, this.delay);
        register(PlayerTickEvent.class, class130Var -> {
            Mc class815Var= Mc.INSTANCE;
            if (isState() && class815Var.isWorldLoaded() && class130Var.isPre()) {
                GameOptions gameOptions= class815Var.getGameOptions();
                MinecraftClient minecraftClient= MinecraftClient.getInstance();
                if (this.handMode.isSelected(TapeMouseHand.LEFT)) {
                    if (this.stopwatch.hasElapsed((long) this.delay.currentValue(), TimeUnit.SECONDS)) {
                        minecraftClient.doAttack();
                        this.stopwatch.reset();
                    }
                } else if (this.stopwatch.hasElapsed((long) this.delay.currentValue(), TimeUnit.SECONDS)) {
                    gameOptions.useKey.setPressed(true);
                    this.usePressed = true;
                    this.stopwatch.reset();
                }
                if (this.usePressed) {
                    gameOptions.useKey.setPressed(false);
                    this.usePressed = false;
                }
            }
        });
    }

    @Override
    public void deactivate() {
        Mc class815Var= Mc.INSTANCE;
        if (class815Var.isWorldLoaded() && this.usePressed) {
            class815Var.getGameOptions().useKey.setPressed(false);
            this.usePressed = false;
        }
        super.deactivate();
    }
}
