package ru.expensive.implement.features.modules.render;

import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.*;
import ru.expensive.implement.events.packet.PacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class AmbienceModule extends Module {

    private final GroupSetting brightnessSettings = new GroupSetting("Brightness", "Settings for world brightness")
            .settings(
                    new ValueSetting("Level", "Adjust world brightness level")
                            .setValue(1.0F)
                            .range(0.0F, 1.0F)
                            .increment(0.1F)
            );

    private final ValueSetting timeSetting = new ValueSetting("Time", "Changes the world time")
            .setValue(6000f)
            .range(0f, 24000f)
            .increment(200f);

    private final SelectSetting weatherSetting = new SelectSetting("Weather", "Changes the weather condition")
            .value("Custom", "Clear", "Rain", "Thunder");

    public AmbienceModule() {
        super("Ambience", ModuleCategory.RENDER);
        setup(brightnessSettings, timeSetting, weatherSetting);
    }

    public float getBrightnessLevel() {
        if (!brightnessSettings.isValue()) return 0.0F;
        return ((ValueSetting) brightnessSettings.getSubSetting("Level")).getValue();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (mc == null || mc.world == null) return;

        if (event.isReceive() && event.getPacket() instanceof WorldTimeUpdateS2CPacket packet) {
            event.cancel();

            // Apply custom time via public ClientWorld API.
            // NOTE: World has no "timeOfDay" field in 1.21.11 (it delegates to
            // ClientWorld$Properties), so the old WorldAccessor mixin crashed at bootstrap.
            long customTime = (long) timeSetting.getValue();
            mc.world.setTime(packet.time(), customTime, false);

            // Apply custom weather
            switch (weatherSetting.getSelected()) {
                case "Clear" -> {
                    mc.world.setRainGradient(0);
                    mc.world.setThunderGradient(0);
                }
                case "Rain" -> {
                    mc.world.setRainGradient(1);
                    mc.world.setThunderGradient(0);
                }
                case "Thunder" -> {
                    mc.world.setRainGradient(1);
                    mc.world.setThunderGradient(1);
                }
            }
        }
    }
}