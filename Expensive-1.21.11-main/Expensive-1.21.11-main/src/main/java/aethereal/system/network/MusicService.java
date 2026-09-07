package aethereal.system.network;
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
import dev.redstones.mediaplayerinfo.impl.win.WindowsMediaPlayerInfo;
import dev.redstones.mediaplayerinfo.impl.win.WindowsMediaSession;
import net.minecraft.client.MinecraftClient;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicService {
    public static final MusicService INSTANCE = new MusicService();

    private boolean playing = false;
    private long positionMs = 0;
    private double durationSecs = 0;
    
    private long lastKnownSmtcPosition = -1;
    private long lastSyncTime = 0;

    private long lastInteractionTime = 0;

    private String currentTitle = "No Session";
    private String currentAuthor = "";
    
    private GlTextureObject coverTexture;
    private byte[] lastThumbnailBytes;

    private AtomicBoolean started = new AtomicBoolean(false);
    private WindowsMediaSession currentSession;

    private MusicService() {
    }

    public void ensureStarted() {
        if (started.compareAndSet(false, true)) {
            Thread t= new Thread(this::pollSmtc, "SMTC-JNI-Poller");
            t.setDaemon(true);
            t.start();
        }
    }

    private void pollSmtc() {
        while (true) {
            try {
                LinkedList<WindowsMediaSession> sessions= WindowsMediaPlayerInfo.getMediaSessions();
                if (sessions != null && !sessions.isEmpty()) {
                    currentSession = sessions.get(0);
                    MediaInfo info= currentSession.getMediaInfo();
                    if (info != null) {
                        this.currentTitle = info.title != null ? info.title : "";
                        this.currentAuthor = info.artist != null ? info.artist : "";
                        
                        double newDurSec= (info.duration >= 100000) ? (info.duration / 1000.0d) : info.duration;
                        double newPosSec= (info.position >= 100000) ? (info.position / 1000.0d) : info.position;
                        
                        this.durationSecs = newDurSec;
                        
                        if (System.currentTimeMillis() - this.lastInteractionTime > 2500) {
                            this.playing = info.playing;
                        }
                        
                        if (info.position != this.lastKnownSmtcPosition) {
                            this.lastKnownSmtcPosition = info.position;
                            this.positionMs = (long) (newPosSec * 1000.0d); // store internally as ms for positionSeconds()
                            this.lastSyncTime = System.currentTimeMillis();
                        }
                        
                        if (info.artworkPng != null && info.artworkPng.length > 0) {
                            if (this.lastThumbnailBytes == null || !java.util.Arrays.equals(this.lastThumbnailBytes, info.artworkPng)) {
                                this.lastThumbnailBytes = info.artworkPng.clone();
                                updateTexture(this.lastThumbnailBytes);
                            }
                        } else {
                            if (lastThumbnailBytes != null) {
                                lastThumbnailBytes = null;
                                updateTexture(null);
                            }
                        }
                    }
                } else {
                    currentSession = null;
                    this.playing = false;
                    this.currentTitle = "No Session";
                    this.currentAuthor = "";
                    this.lastKnownSmtcPosition = -1;
                    this.positionMs = 0;
                    this.durationSecs = 0;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private void updateTexture(byte[] imageBytes) {
        MinecraftClient.getInstance().execute(() -> {
            if (imageBytes != null) {
                try {
                    if (coverTexture != null) {
                        coverTexture.free();
                    }
                    ByteBuffer buffer= BufferUtils.createByteBuffer(imageBytes.length);
                    buffer.put(imageBytes);
                    buffer.flip();
                    // Load texture from memory buffer
                    ByteArrayResource res= new ByteArrayResource(imageBytes);
                    coverTexture = new GlTextureObject(res);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                if (coverTexture != null) {
                    coverTexture.free();
                    coverTexture = null;
                }
            }
        });
    }

    public void togglePlay() {
        if (currentSession != null) {
            boolean newState= !playing;
            this.playing = newState; // optimistic local update
            this.lastInteractionTime = System.currentTimeMillis();
            Thread t= new Thread(() -> {
                try {
                    currentSession.playPause();
                } catch (Throwable ex) {
                    try {
                        if (newState) {
                            currentSession.play();
                        } else {
                            currentSession.pause();
                        }
                    } catch (Throwable ignored) {}
                }
            }, "MusicService-Action");
            t.setDaemon(true);
            t.start();
        }
    }

    public void previousTrack() {
        if (currentSession != null) {
            this.lastInteractionTime = System.currentTimeMillis();
            Thread t= new Thread(() -> {
                try {
                    currentSession.previous();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }, "MusicService-Action");
            t.setDaemon(true);
            t.start();
        }
    }

    public void nextTrack() {
        if (currentSession != null) {
            this.lastInteractionTime = System.currentTimeMillis();
            Thread t= new Thread(() -> {
                try {
                    currentSession.next();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }, "MusicService-Action");
            t.setDaemon(true);
            t.start();
        }
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public String title() {
        return this.currentTitle;
    }

    public String author() {
        return this.currentAuthor;
    }

    public double durationSeconds() {
        return this.durationSecs;
    }

    public double positionSeconds() {
        double baseSecs= positionMs / 1000.0d; // milliseconds
        if (this.playing && this.lastSyncTime > 0) {
            double elapsedSinceSync= (System.currentTimeMillis() - this.lastSyncTime) / 1000.0d;
            double current= baseSecs + elapsedSinceSync;
            return Math.min(current, durationSecs > 0 ? durationSecs : current);
        }
        return baseSecs;
    }

    public float progress() {
        double pos= positionSeconds();
        double dur= durationSeconds();
        if (dur <= 0) return 0.0f;
        float p= (float) (pos / dur);
        return Math.max(0.0f, Math.min(1.0f, p));
    }

    public GlTextureObject cover() {
        return coverTexture;
    }
}
