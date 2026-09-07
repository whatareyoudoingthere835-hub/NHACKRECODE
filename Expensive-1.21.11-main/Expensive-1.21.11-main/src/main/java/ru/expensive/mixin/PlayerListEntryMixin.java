package ru.expensive.mixin;
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

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.AssetInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerListEntry.class})
public class PlayerListEntryMixin {

    @Shadow
    @Final
    private GameProfile profile;

    @Unique
    private Identifier capeTexture = Identifier.of("expensive", "textures/cape.png");

    @Inject(method = {"getSkinTextures"}, at = {@At("RETURN")}, cancellable = true)
    private void injectCapeCosmetic(CallbackInfoReturnable<SkinTextures> callbackInfoReturnable) {
        if (this.capeTexture == null || this.profile == null || !this.profile.name().equals(Mc.INSTANCE.getSession().getUsername())) {
            return;
        }
        SkinTextures skinTextures = (SkinTextures) callbackInfoReturnable.getReturnValue();
        callbackInfoReturnable.setReturnValue(new SkinTextures(skinTextures.body(), new AssetInfo.TextureAssetInfo(this.capeTexture), skinTextures.elytra(), skinTextures.model(), skinTextures.secure()));
    }
}
