package aethereal.system.resources;
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

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

public class UrlResource implements ResourceSource {
    public final String url;

    @Override
    public InputStream stream() {
        try {
            Expensive.LOGGER.debug("Trying to load URL resource: {}", this.url);
            URI uriCreate= URI.create(this.url);
            Expensive.LOGGER.debug("Parsed URI: {}", uriCreate);
            URLConnection uRLConnectionOpenConnection= uriCreate.toURL().openConnection();
            uRLConnectionOpenConnection.setUseCaches(false);
            uRLConnectionOpenConnection.setConnectTimeout(5000);
            uRLConnectionOpenConnection.setReadTimeout(5000);
            uRLConnectionOpenConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            return uRLConnectionOpenConnection.getInputStream();
        } catch (Exception e) {
            Expensive.LOGGER.error("Failed to open URL resource: {}", this.url, e);
            throw new RuntimeException("Failed to open URL resource: " + this.url, e);
        }
    }

    public UrlResource(String str) {
        this.url = str;
    }
}
