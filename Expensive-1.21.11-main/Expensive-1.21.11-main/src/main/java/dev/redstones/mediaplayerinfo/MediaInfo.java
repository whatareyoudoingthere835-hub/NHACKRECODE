package dev.redstones.mediaplayerinfo;
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

public class MediaInfo {
    public final String title;
    public final String artist;
    public final byte[] artworkPng;
    public final long position;
    public final long duration;
    public final boolean playing;

    public MediaInfo(String title, String artist, byte[] artworkPng, long position, long duration, boolean playing) {
        this.title = title;
        this.artist = artist;
        this.artworkPng = artworkPng;
        this.position = position;
        this.duration = duration;
        this.playing = playing;
    }
}
