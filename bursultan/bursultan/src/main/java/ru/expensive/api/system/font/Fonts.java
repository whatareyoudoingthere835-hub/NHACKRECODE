package ru.expensive.api.system.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.expensive.core.Extra;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Fonts {

    private static final Map<String, Font> BASE_FONTS = new java.util.concurrent.ConcurrentHashMap<>();

    private static Font getBaseFont(String name) {
        return BASE_FONTS.computeIfAbsent(name, n -> {
            try {
                String ttfPath = "assets/minecraft/expensive/fonts/" + n + ".ttf";
                try (InputStream inputStream = Extra.class.getClassLoader().getResourceAsStream(ttfPath)) {
                    if (inputStream != null) {
                        return Font.createFont(Font.TRUETYPE_FONT, inputStream);
                    }
                }
                String otfPath = "assets/minecraft/expensive/fonts/" + n + ".otf";
                try (InputStream inputStream = Extra.class.getClassLoader().getResourceAsStream(otfPath)) {
                    if (inputStream != null) {
                        return Font.createFont(Font.TRUETYPE_FONT, inputStream);
                    }
                }
            } catch (Exception ignored) {
            }
            return new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        });
    }

    public static FontRenderer create(float size, String name) {
        Font font = getBaseFont(name).deriveFont(Font.PLAIN, size / 2f);
        return new FontRenderer(font, size / 2f);
    }

    private static final Map<FontKey, FontRenderer> fontCache = new java.util.concurrent.ConcurrentHashMap<>();

    public static void init() {
        for (Type type : Type.values()) {
            getBaseFont(type.getType());
        }
    }

    public static FontRenderer getSize(int size) {
        return getSize(size, Type.BOLD);
    }

    public static FontRenderer getSize(int size, Type type) {
        return fontCache.computeIfAbsent(new FontKey(size, type), k -> create(size, type.getType()));
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        DEFAULT("sfpromedium"),
        BOLD("sfprosemibold");

        private final String type;
    }

    private record FontKey(int size, Type type) {
    }
}