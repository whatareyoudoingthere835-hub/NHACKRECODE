package aethereal.features.modules.render;
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

import java.util.List;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@Aliases(aliases = {"World Tweaks", "Ambience", "Fog Color", "Sky Color", "World Time"})
public class WorldTweaksModule extends Module {
    public final ExpandableSetting changeTime;
    public final ModeSetting<WorldTime> timeOfDay;
    public final ExpandableSetting changeFogColor;
    public final NumberSetting fogDistance;
    public final Mc mc;
    public final ColorSetting fogColor;

    public WorldTweaksModule() {
        super(ModuleTab.RENDER, "World Tweaks");
        this.changeTime = new ExpandableSetting(Lang.WORLD_TWEAKS_CHANGE_TIME, Lang.WORLD_TWEAKS_CHANGE_TIME_DESC);
        this.timeOfDay = new ModeSetting(Lang.WORLD_TWEAKS_TIME_OF_DAY).values(WorldTime.class).currentValue(WorldTime.NIGHT);
        this.changeFogColor = new ExpandableSetting(Lang.WORLD_TWEAKS_FOG_COLOR, Lang.WORLD_TWEAKS_FOG_COLOR_DESC);
        this.fogDistance = new NumberSetting(Lang.WORLD_TWEAKS_FOG_COLOR_DISTANCE, Lang.WORLD_TWEAKS_FOG_COLOR_DISTANCE_DESC).currentValue(90.0f).range(10.0f, 256.0f).step(1.0f);
        this.mc = Mc.INSTANCE;
        this.fogColor = new ColorSetting(Lang.WORLD_TWEAKS_FOG_COLOR_COLOR, Lang.WORLD_TWEAKS_FOG_COLOR_COLOR_DESC).setColor(7238883);
        this.changeTime.setSubSettings(List.of(this.timeOfDay));
        this.changeFogColor.setSubSettings(List.of(this.fogColor, this.fogDistance));
        addSettings(this.changeTime, this.changeFogColor);
        register(PacketReceiveEvent.class, class051Var -> {
            if (isState() && this.mc.isWorldLoaded() && this.changeTime.isValue()) {
                if ((class051Var.getPacket()) instanceof WorldTimeUpdateS2CPacket packet ) {
                    WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket= packet;
                    WorldTime class637Var= (WorldTime) this.timeOfDay.currentValue();
                    if (class637Var.ticks() != -1) {
                        worldTimeUpdateS2CPacket.timeOfDay = class637Var.ticks();
                    }
                }
            }
        });
    }

    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    public void reload() {
        WorldRenderer worldRenderer;
        if (Mc.INSTANCE.isWorldLoaded() && (worldRenderer = Mc.INSTANCE.getMinecraft().worldRenderer) != null && isState()) {
            worldRenderer.reload();
        }
    }

    public ExpandableSetting changeFogColor() {
        return this.changeFogColor;
    }

    public NumberSetting fogDistance() {
        return this.fogDistance;
    }

    public ColorSetting fogColor() {
        return this.fogColor;
    }
}
