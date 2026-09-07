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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;

public class ThemeStorage {
    public final Path filePath;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String selectedThemeId = "expensive-dark";
    public Set<String> favoriteThemeIds = new HashSet<>();
    public List<Theme> customThemes = new ArrayList<>();

    public ThemeStorage(Path path) {
        this.filePath = path;
    }

    public static class CustomThemeDto {
        String id;
        String name;
        String owner;
        long createdAt;
        long updatedAt;
        int accent;
        int accentBright;
        int favorite;
        int frameBackground;
        Map<Integer, Integer> textMap = new HashMap<>();
        Map<Integer, Integer> errorMap = new HashMap<>();
        Map<Integer, Integer> outlineMap = new HashMap<>();
        Map<Integer, Integer> surfaceMap = new HashMap<>();

        static CustomThemeDto fromTheme(Theme theme) {
            CustomThemeDto dto= new CustomThemeDto();
            dto.id = theme.id();
            dto.name = theme.name();
            dto.owner = theme.owner();
            dto.createdAt = theme.createdAt() != null ? theme.createdAt().toEpochMilli() : System.currentTimeMillis();
            dto.updatedAt = theme.updatedAt() != null ? theme.updatedAt().toEpochMilli() : System.currentTimeMillis();

            StylePalette p= theme.palette();
            if (p != null) {
                dto.accent = p.accent().argb();
                dto.accentBright = p.accentBright().argb();
                dto.favorite = p.favorite().argb();
                dto.frameBackground = p.frameBackground().argb();

                if (p.text() != null && p.text().all() != null) {
                    p.text().all().forEach((k, v) -> dto.textMap.put(k, v.argb()));
                }
                if (p.error() != null && p.error().all() != null) {
                    p.error().all().forEach((k, v) -> dto.errorMap.put(k, v.argb()));
                }
                if (p.surfaceOutline() != null && p.surfaceOutline().all() != null) {
                    p.surfaceOutline().all().forEach((k, v) -> dto.outlineMap.put(k, v.argb()));
                }
                if (p.surfaceBackground() != null && p.surfaceBackground().all() != null) {
                    p.surfaceBackground().all().forEach((k, v) -> dto.surfaceMap.put(k, v.argb()));
                }
            }
            return dto;
        }

        public Theme toTheme() {
            Map<Integer, ColorValue> tMap = new HashMap<>();
            textMap.forEach((k, v) -> tMap.put(k, new ColorValue(v)));

            Map<Integer, ColorValue> eMap = new HashMap<>();
            errorMap.forEach((k, v) -> eMap.put(k, new ColorValue(v)));

            Map<Integer, ColorValue> oMap = new HashMap<>();
            outlineMap.forEach((k, v) -> oMap.put(k, new ColorValue(v)));

            Map<Integer, ColorValue> sMap = new HashMap<>();
            surfaceMap.forEach((k, v) -> sMap.put(k, new ColorValue(v)));

            StylePalette palette= new StylePalette(
                new ColorValue(accent),
                new ColorValue(accentBright),
                new ColorValue(favorite),
                new ColorValue(frameBackground),
                new ColorToneScale(tMap),
                new ColorToneScale(eMap),
                new ColorToneScale(oMap),
                new ColorToneScale(sMap)
            );

            return Theme.of(
                id,
                name,
                owner != null ? owner : "",
                ConfigOrigin.USER,
                ThemeMode.DARK,
                palette,
                Instant.ofEpochMilli(createdAt),
                Instant.ofEpochMilli(updatedAt)
            );
        }
    }

    public static class StorageData {
        String selectedThemeId = "expensive-dark";
        List<String> favoriteThemeIds = new ArrayList<>();
        List<CustomThemeDto> customThemes = new ArrayList<>();
    }

    public void save() {
        try {
            StorageData data= new StorageData();
            data.selectedThemeId = this.selectedThemeId;
            data.favoriteThemeIds = new ArrayList<>(this.favoriteThemeIds);
            for (Theme t : this.customThemes) {
                data.customThemes.add(CustomThemeDto.fromTheme(t));
            }
            String json= gson.toJson(data);
            AtomicFileWriter.writeBytes(this.filePath, json.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (Exception e) {
            Expensive.LOGGER.error("Failed to save themes config", e);
        }
    }

    public void load() {
        try {
            if (!Files.exists(this.filePath)) {
                return;
            }
            String json= Files.readString(this.filePath, StandardCharsets.UTF_8);
            StorageData data= gson.fromJson(json, StorageData.class);
            if (data != null) {
                if (data.selectedThemeId != null) {
                    this.selectedThemeId = data.selectedThemeId;
                }
                if (data.favoriteThemeIds != null) {
                    this.favoriteThemeIds = new HashSet<>(data.favoriteThemeIds);
                }
                this.customThemes.clear();
                if (data.customThemes != null) {
                    for (CustomThemeDto dto : data.customThemes) {
                        this.customThemes.add(dto.toTheme());
                    }
                }
            }
        } catch (Exception e) {
            Expensive.LOGGER.error("Failed to load themes config", e);
        }
    }
}
