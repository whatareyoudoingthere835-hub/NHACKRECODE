package ru.expensive.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ZeroHitboxModule extends Module {

    MultiSelectSetting targetSettings = new MultiSelectSetting("Target", "Select which entities should have zero hitbox")
            .value("Players", "Mobs", "Armor Stands");

    public ZeroHitboxModule() {
        super("ZeroHitbox", "Zero Hitbox", ModuleCategory.MISC);
        setup(targetSettings);
    }
}