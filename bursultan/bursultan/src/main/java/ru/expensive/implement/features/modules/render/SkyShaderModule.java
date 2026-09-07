package ru.expensive.implement.features.modules.render;

import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.system.shader.implement.SkyShaderRenderer;
import ru.expensive.implement.events.render.WorldRenderEvent;

public class SkyShaderModule extends Module {
    private final SelectSetting modeSetting = new SelectSetting("Mode", "Shader style")
            .value("Шейдер", "Шейдер 2", "Caustic", "Nebula", "Space", "Galaxy");
    private final ValueSetting strengthSetting = new ValueSetting("Strength", "Shader strength")
            .range(0.1F, 3.0F).increment(0.1F).setValue(1.0F);
    private final ValueSetting speedSetting = new ValueSetting("Speed", "Shader speed")
            .range(0.1F, 5.0F).increment(0.1F).setValue(1.0F);
    private final ColorSetting colorSetting = new ColorSetting("Color", "Tint color")
            .value(0xC878B4FF);
    private final ValueSetting opacitySetting = new ValueSetting("Opacity", "Overlay opacity")
            .range(0.1F, 1.0F).increment(0.05F).setValue(0.8F);

    private final SkyShaderRenderer shaderRenderer = new SkyShaderRenderer();

    public SkyShaderModule() {
        super("SkyShader", "Sky Shader", ModuleCategory.RENDER);
        setup(modeSetting, strengthSetting, speedSetting, colorSetting, opacitySetting);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        int color = colorSetting.getColor();
        float alpha = ((color >>> 24) & 0xFF) / 255.0F * opacitySetting.getValue();
        float[] rgba = {
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                alpha
        };
        int modeIndex = Math.max(0, modeSetting.getList().indexOf(modeSetting.getSelected()));
        shaderRenderer.render(rgba, strengthSetting.getValue(), speedSetting.getValue(), (float) modeIndex);
    }
}
