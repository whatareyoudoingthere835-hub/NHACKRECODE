package aethereal.system.config;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

public class AutoBuyDataStorage {
    public static final AutoBuyDataStorage INSTANCE = new AutoBuyDataStorage();

    private final List<AutoBuyTarget> targets = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean loaded = false;

    private Path getPath() {
        return Path.of("expensive", "autobuy.json");
    }

    public synchronized void load() {
        if (loaded) return;
        Path file= getPath();
        try {
            if (!Files.isRegularFile(file)) {
                loaded = true;
                return;
            }
            String json= Files.readString(file, StandardCharsets.UTF_8);
            if (json == null || json.isBlank()) {
                loaded = true;
                return;
            }
            JsonElement root= gson.fromJson(json, JsonElement.class);
            if (root == null || !root.isJsonArray()) return;

            targets.clear();
            for (JsonElement value : root.getAsJsonArray()) {
                if (!value.isJsonObject()) continue;
                JsonObject obj= value.getAsJsonObject();
                String itemId= string(obj, "item");
                if (itemId.isBlank()) continue;

                ItemStack stack= Registries.ITEM.get(Identifier.of(itemId)).getDefaultStack();
                if (!stack.isEmpty()) {
                    AutoBuyTarget target= new AutoBuyTarget(
                        stack,
                        string(obj, "name"),
                        bool(obj, "enabled", true),
                        string(obj, "price"),
                        integer(obj, "minCount", 1),
                        integer(obj, "minDurability", 0)
                    );
                    target.setSellPriceText(string(obj, "sellPrice"));
                    target.setSellLotSize(integer(obj, "sellLotSize", stack.getMaxCount()));
                    target.setBuyThorns(bool(obj, "buyThorns", false));
                    target.setOnlyOriginal(bool(obj, "onlyOriginal", false));
                    if (targets.stream().noneMatch(existing -> existing.equalParameters(target))) {
                        targets.add(target);
                    }
                }
            }
            loaded = true;
        } catch (Exception ignored) {
            loaded = true;
        }
    }

    public synchronized void save() {
        Path file= getPath();
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            JsonArray array= new JsonArray();
            for (AutoBuyTarget target : targets) {
                JsonObject obj= new JsonObject();
                obj.addProperty("item", Registries.ITEM.getId(target.getStack().getItem()).toString());
                obj.addProperty("name", target.getName());
                obj.addProperty("enabled", target.isEnabled());
                obj.addProperty("price", target.getPriceText());
                obj.addProperty("minCount", target.getMinCount());
                obj.addProperty("minDurability", target.getMinDurability());
                obj.addProperty("sellPrice", target.getSellPriceText());
                obj.addProperty("sellLotSize", target.getSellLotSize());
                obj.addProperty("buyThorns", target.isBuyThorns());
                obj.addProperty("onlyOriginal", target.isOnlyOriginal());
                array.add(obj);
            }
            Files.writeString(file, gson.toJson(array), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public List<AutoBuyTarget> getTargets() {
        if (!loaded) load();
        return targets;
    }

    public void addTarget(AutoBuyTarget target) {
        if (!loaded) load();
        targets.add(target);
        save();
    }

    public void removeTarget(AutoBuyTarget target) {
        if (!loaded) load();
        targets.remove(target);
        save();
    }

    private static String string(JsonObject object, String key) {
        JsonElement value= object == null ? null : object.get(key);
        return value == null || value.isJsonNull() ? "" : value.getAsString();
    }

    private static boolean bool(JsonObject object, String key, boolean fallback) {
        JsonElement value= object == null ? null : object.get(key);
        return value == null || value.isJsonNull() ? fallback : value.getAsBoolean();
    }

    private static int integer(JsonObject object, String key, int fallback) {
        JsonElement value= object == null ? null : object.get(key);
        return value == null || value.isJsonNull() ? fallback : value.getAsInt();
    }
}
