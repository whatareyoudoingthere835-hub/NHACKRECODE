package dev.redstones.mediaplayerinfo.impl.win;
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

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

public class WindowsMediaPlayerInfo {
    static {
        try {
            File tempDll = File.createTempFile("MediaPlayerInfo", ".dll");
            tempDll.deleteOnExit();
            InputStream is = WindowsMediaPlayerInfo.class.getResourceAsStream("/assets/expensive/MediaPlayerInfo.dll");
            if (is != null) {
                Files.copy(is, tempDll.toPath(), StandardCopyOption.REPLACE_EXISTING);
                is.close();
                System.load(tempDll.getAbsolutePath());
            } else {
                System.err.println("Could not find MediaPlayerInfo.dll in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static native LinkedList<WindowsMediaSession> getMediaSessions();
}
