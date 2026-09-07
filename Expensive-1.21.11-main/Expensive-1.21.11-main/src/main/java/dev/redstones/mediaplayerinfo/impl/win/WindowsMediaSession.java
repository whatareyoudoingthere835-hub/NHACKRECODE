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

import dev.redstones.mediaplayerinfo.MediaInfo;

public class WindowsMediaSession {
    private final MediaInfo media;
    private final String owner;
    private final int index;

    public WindowsMediaSession(MediaInfo media, String owner, int index) {
        this.media = media;
        this.owner = owner;
        this.index = index;
    }

    public MediaInfo getMediaInfo() {
        return this.media;
    }

    public native void play();
    public native void pause();
    public native void playPause();
    public native void previous();
    public native void next();
    public native void stop();
}
