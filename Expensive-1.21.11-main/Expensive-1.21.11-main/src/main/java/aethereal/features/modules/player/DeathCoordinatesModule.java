package aethereal.features.modules.player;
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

import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

@Aliases(aliases = {"Death Coords", "Death Coordinates", "Point Death"})
public class DeathCoordinatesModule extends Module {
    public static final String deathCoordsFormat = "%s: " + String.valueOf(Formatting.RED) + "X: " + String.valueOf(Formatting.RESET) + "%d" + String.valueOf(Formatting.RED) + " Y: " + String.valueOf(Formatting.RESET) + "%d" + String.valueOf(Formatting.RED) + " Z: " + String.valueOf(Formatting.RESET) + "%d.";
    public boolean alreadyPosted;

    public DeathCoordinatesModule() {
        super(ModuleTab.PLAYER, "Death Coordinates");
        register(PlayerInitEvent.class, class125Var -> {
            this.alreadyPosted = false;
        });
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && class130Var.isPre()) {
                Mc class815Var= Mc.INSTANCE;
                ClientPlayerEntity player= class815Var.getPlayer();
                if (!(class815Var.getCurrentScreen() instanceof DeathScreen) || this.alreadyPosted) {
                    return;
                }
                BlockPos blockPos= player.getBlockPos();
                String str= deathCoordsFormat.formatted(Lang.DEATHCOORDINATES_DEATH_TEXT.effective(), Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()));
                Expensive.INSTANCE.notificationRepository().post(NotificationType.INFO, (Text) Text.literal(str), 5L, TimeUnit.SECONDS);
                ChatUtil.addChatMessage(str);
                this.alreadyPosted = true;
            }
        });
    }
}
