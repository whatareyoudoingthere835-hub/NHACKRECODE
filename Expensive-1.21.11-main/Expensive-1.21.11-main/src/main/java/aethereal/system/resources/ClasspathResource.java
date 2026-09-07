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
import java.net.URL;
import java.net.URLConnection;

public class ClasspathResource implements ResourceSource {
    public final String path;
    static final String assetPrefix = "/assets/expensive/";

    @Override
    public InputStream stream() {
        try {
            String strSubstring= this.path;
            if (strSubstring.startsWith("/")) {
                strSubstring = strSubstring.substring(1);
            }
            if (!strSubstring.startsWith("assets/expensive/")) {
                strSubstring = "assets/expensive/" + strSubstring;
            }
            String str= "/" + strSubstring;
            Expensive.LOGGER.debug("Trying to load classpath resource: {}", str);
            URL resource= ClasspathResource.class.getResource(str);
            if (resource == null) {
                resource = ClasspathResource.class.getClassLoader().getResource(strSubstring);
            }
            if (resource == null) {
                resource = Thread.currentThread().getContextClassLoader().getResource(strSubstring);
            }
            if (resource == null) {
                throw new IllegalStateException("Classpath resource not found: " + str);
            }
            Expensive.LOGGER.debug("Found resource URL: {}", resource);
            URLConnection uRLConnectionOpenConnection= resource.openConnection();
            uRLConnectionOpenConnection.setUseCaches(false);
            return uRLConnectionOpenConnection.getInputStream();
        } catch (Exception e) {
            Expensive.LOGGER.error("Failed to open classpath resource: {}", this.path, e);
            throw new RuntimeException("Failed to open classpath resource: " + this.path, e);
        }
    }

    public ClasspathResource(String str) {
        this.path = str;
    }
}
