package ru.expensive.mixins.accessors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ResourceReloadLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.net.Proxy;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("currentFps")
    static int getFps() {
        return 0;
    }

    @Accessor("itemUseCooldown")
    int getItemUseCooldown();

    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int val);

    @Accessor("attackCooldown")
    int getAttackCooldown();

    @Accessor("attackCooldown")
    void setAttackCooldown(int val);

    @Accessor("networkProxy")
    Proxy getProxy();

    @Accessor("resourceReloadLogger")
    ResourceReloadLogger getResourceReloadLogger();

    @Invoker("doAttack")
    boolean leftClick();
}