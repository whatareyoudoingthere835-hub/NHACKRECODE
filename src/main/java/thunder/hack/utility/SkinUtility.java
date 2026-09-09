package thunder.hack.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * 1.21.9+ skin lookup: AbstractClientPlayerEntity#getSkinTextures() is gone,
 * textures now live on the PlayerListEntry (record-style, body texture via body().id()).
 */
public final class SkinUtility {
    private SkinUtility() {
    }

    public static Identifier skin(Entity entity) {
        try {
            var handler = MinecraftClient.getInstance().getNetworkHandler();
            if (handler == null || entity == null) return null;
            var entry = handler.getPlayerListEntry(entity.getUuid());
            if (entry == null) return null;
            return entry.getSkinTextures().body().id();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
