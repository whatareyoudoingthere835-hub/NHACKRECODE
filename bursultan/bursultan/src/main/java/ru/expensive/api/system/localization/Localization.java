package ru.expensive.api.system.localization;

import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import net.minecraft.util.Identifier;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Extra;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Localization implements QuickImports {
    private static final Map<Language, Map<String, String>> cache = new ConcurrentHashMap<>();

    public static String get(String key) {
        Language currentLanguage = Extra.getInstance().getLanguage();

        Map<String, String> translations = cache.computeIfAbsent(currentLanguage, Localization::loadTranslations);
        return translations.getOrDefault(key, key);
    }

    @SneakyThrows
    private static Map<String, String> loadTranslations(Language language) {
        Identifier identifier = Identifier.of("minecraft", "expensive/translations/" + language.getFile() + ".json");

        InputStream stream = mc.getResourceManager()
                .getResource(identifier)
                .get()
                .getInputStream();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        return gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
    }
}
