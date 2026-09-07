package aethereal.core.models;
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


import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ThemeData {

    public static StylePalette createPalette(String accentHex, String accentBrightHex) {
        return new StylePalette(
            ColorValue.fromHex(accentHex),
            ColorValue.fromHex(accentBrightHex),
            ColorValue.fromHex("FDC95A"),
            ColorValue.fromHex("151617"),
            new ColorToneScale(Map.ofEntries(
                Map.entry(900, ColorValue.fromHex("313133")),
                Map.entry(800, ColorValue.fromHex("505155")),
                Map.entry(700, ColorValue.fromHex("606166")),
                Map.entry(600, ColorValue.fromHex("76777E")),
                Map.entry(500, ColorValue.fromHex("868791")),
                Map.entry(400, ColorValue.fromHex("B4B5BA")),
                Map.entry(300, ColorValue.fromHex("C5C6C8")),
                Map.entry(200, ColorValue.fromHex("DADCE2")),
                Map.entry(100, ColorValue.fromHex("E3E4E7")),
                Map.entry(50, ColorValue.fromHex("F0F1F4"))
            )),
            new ColorToneScale(Map.ofEntries(
                Map.entry(900, ColorValue.fromHex("ED4561")),
                Map.entry(500, ColorValue.fromHex("EE5871")),
                Map.entry(300, ColorValue.fromHex("EF6179"))
            )),
            new ColorToneScale(Map.ofEntries(
                Map.entry(700, ColorValue.fromHex("17181A")),
                Map.entry(600, ColorValue.fromHex("1A1B1E")),
                Map.entry(500, ColorValue.fromHex("202123")),
                Map.entry(400, ColorValue.fromHex("222325")),
                Map.entry(300, ColorValue.fromHex("282A2E")),
                Map.entry(50, ColorValue.fromHex("6E7279"))
            )),
            new ColorToneScale(Map.ofEntries(
                Map.entry(900, ColorValue.fromHex("0F1011")),
                Map.entry(801, ColorValue.fromHex("121315")),
                Map.entry(800, ColorValue.fromHex("17181A")),
                Map.entry(700, ColorValue.fromHex("18191A")),
                Map.entry(600, ColorValue.fromHex("1A1B1D")),
                Map.entry(500, ColorValue.fromHex("1E1F22")),
                Map.entry(400, ColorValue.fromHex("26272A")),
                Map.entry(300, ColorValue.fromHex("2D2E31")),
                Map.entry(200, ColorValue.fromHex("565659"))
            ))
        );
    }

    public static StylePalette createDarkPalette() {
        return createPalette("6E74E3", "8186EA");
    }

    public static Theme defaultDark() {
        Instant now= Instant.now();
        return Theme.of("expensive-dark", "Expensive Dark", "Expensive", ConfigOrigin.OFFICIAL, ThemeMode.DARK, createDarkPalette(), now, now);
    }

    public static List<Theme> getDefaultThemes() {
        Instant now= Instant.now();
        return List.of(
            // 1. Expensive Dark (Indigo/Violet Accent)
            Theme.of("expensive-dark", "Expensive Dark", "Expensive", ConfigOrigin.OFFICIAL, ThemeMode.DARK, createPalette("6E74E3", "8186EA"), now, now),
            
            // 2. Expensive Crimson (Crimson Red Accent)
            Theme.of("expensive-crimson", "Expensive Crimson", "Expensive", ConfigOrigin.OFFICIAL, ThemeMode.DARK, createPalette("E11D48", "FB7185"), now, now),

            // 3. Expensive Violet (Purple/Violet Accent)
            Theme.of("expensive-violet", "Expensive Violet", "Expensive", ConfigOrigin.OFFICIAL, ThemeMode.DARK, createPalette("8B5CF6", "A78BFA"), now, now),

            // 4. Expensive Emerald (Emerald Green Accent)
            Theme.of("expensive-emerald", "Expensive Emerald", "Expensive", ConfigOrigin.OFFICIAL, ThemeMode.DARK, createPalette("10B981", "34D399"), now, now),

            // 5. Expensive Frosted (Frosted Slate Accent)
            Theme.of("expensive-frosted", "Expensive Frosted", "Expensive", ConfigOrigin.OFFICIAL, ThemeMode.DARK, createPalette("94A3B8", "CBD5E1"), now, now)
        );
    }
}
