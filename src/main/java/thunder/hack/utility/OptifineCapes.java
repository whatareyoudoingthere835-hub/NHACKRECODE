package thunder.hack.utility;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class OptifineCapes {
    /**
     * author: @dragonostic
     * of-capes
     */

    public interface ReturnCapeTexture {
        void response(Identifier id);
    }

    public static void loadPlayerCape(GameProfile player, ReturnCapeTexture response) {
        try {
            String uuid = player.id().toString();
            NativeImageBackedTexture nIBT = getCapeFromURL(String.format("http://s.optifine.net/capes/%s.png", player.name()));
            Identifier th$id = Identifier.of("thunderhack", "th-cape-" + uuid);
            MinecraftClient.getInstance().getTextureManager().registerTexture(th$id, nIBT);
            Identifier capeTexture = th$id;
            response.response(capeTexture);
        } catch (Exception ignored) {
        }
    }

    public static NativeImageBackedTexture getCapeFromURL(String capeStringURL) {
        try {
            URL capeURL = new URL(capeStringURL);
            return getCapeFromStream(capeURL.openStream());
        } catch (IOException e) {
            return null;
        }
    }

    public static NativeImageBackedTexture getCapeFromStream(InputStream image) {
        NativeImage cape = null;
        try {
            cape = NativeImage.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cape != null) {
            return new NativeImageBackedTexture(() -> "thunderhack:optifine-cape", parseCape(cape));
        }
        return null;
    }

    public static NativeImage parseCape(NativeImage image) {
        // 1.21.11: NativeImage pixel read/write API changed - texture used as-is
        return image;
    }



}
