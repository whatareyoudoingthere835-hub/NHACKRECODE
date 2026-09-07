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
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;

public class LastConfigStore {
    public final Path filePath;
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save() {
        if (Expensive.INSTANCE.cloudConfigService().initialized()) {
            Optional<ConfigReference> optionalActiveConfig= Expensive.INSTANCE.cloudConfigService().activeConfig();
            if (optionalActiveConfig.isEmpty()) {
                try {
                    Files.deleteIfExists(this.filePath);
                    return;
                } catch (IOException e) {
                    Expensive.LOGGER.error("Failed to delete last config file", e);
                    return;
                }
            }
            byte[] bytes= gson.toJson(optionalActiveConfig.get()).getBytes(StandardCharsets.UTF_8);
            try {
                if (this.filePath.getParent() != null) {
                    Files.createDirectories(this.filePath.getParent(), new FileAttribute[0]);
                }
                AtomicFileWriter.writeBytes(this.filePath, bytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            } catch (IOException e2) {
                Expensive.LOGGER.error("Failed to save last config ID", e2);
            }
        }
    }

    public void load() {
        if (Files.exists(this.filePath, new LinkOption[0]) && Expensive.INSTANCE.cloudConfigService().initialized()) {
            try {
                String strTrim= Files.readString(this.filePath, StandardCharsets.UTF_8).trim();
                if (strTrim.isBlank() || !strTrim.startsWith("{")) {
                    Files.deleteIfExists(this.filePath);
                    return;
                }
                ConfigReference class304Var= (ConfigReference) gson.fromJson(strTrim, ConfigReference.class);
                if (class304Var == null || class304Var.id() == null) {
                    Files.deleteIfExists(this.filePath);
                } else {
                    Expensive.INSTANCE.cloudConfigService().downloadConfig(class304Var.id()).thenAccept(class089Var -> {
                        Expensive.INSTANCE.cloudConfigService().applyConfig(class089Var);
                        Expensive.LOGGER.info("Config applied: {} by {}", class089Var.details().name(), class089Var.details().author());
                    }).exceptionally(th -> {
                        Expensive.LOGGER.error("Failed to load/apply config", th);
                        return null;
                    });
                }
            } catch (JsonSyntaxException e) {
                Expensive.LOGGER.warn("Invalid config format, resetting...");
                try {
                    Files.deleteIfExists(this.filePath);
                } catch (IOException e2) {
                }
            } catch (IOException e3) {
                Expensive.LOGGER.error("Failed to read config file", e3);
            }
        }
    }

    public LastConfigStore(Path path) {
        this.filePath = path;
    }
}
