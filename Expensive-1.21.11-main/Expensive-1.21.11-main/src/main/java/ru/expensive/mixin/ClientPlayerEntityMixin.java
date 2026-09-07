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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Portal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Unique;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow
    protected int ticksLeftToDoubleTapSprint;

    @Shadow
    public Input input;

    @Shadow
    private boolean inSneakingPose;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    private int ticksToNextAutoJump;

    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    @Shadow
    private boolean falling;

    @Shadow
    private int underwaterVisibilityTicks;

    @Shadow
    private int mountJumpTicks;

    @Shadow
    private float mountJumpStrength;

    public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
    }

    @Shadow
    protected abstract void tickNausea(boolean z);

    @Shadow
    protected abstract void pushOutOfBlocks(double d, double d2);

    @Shadow
    public abstract Portal.Effect getCurrentPortalEffect();

    @Shadow
    public abstract boolean shouldSlowDown();

    @Shadow
    protected abstract boolean canStartSprinting();

    @Shadow
    public abstract boolean canSprint(boolean allowTouchingWater);

    @Shadow
    protected abstract boolean isCamera();

    @Shadow
    @Nullable
    public abstract JumpingMount getJumpingMount();

    @Shadow
    protected abstract boolean shouldStopSprinting();

    @Shadow
    public abstract float getMountJumpStrength();

    @Shadow
    protected abstract void startRidingJump();

    @Inject(at = {@At("TAIL")}, method = {"<init>"})
    private void onInit(MinecraftClient minecraftClient, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler, StatHandler statHandler, ClientRecipeBook clientRecipeBook, PlayerInput lastPlayerInput, boolean lastSneaking, CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new PlayerInitEvent());
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")}, cancellable = true)
    private void tick(CallbackInfo callbackInfo) {
        PlayerTickEvent class130Var = new PlayerTickEvent(TickStage.PRE);
        Expensive.INSTANCE.eventDispatcher().dispatch(class130Var);
        if (class130Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At("RETURN")}, cancellable = true)
    private void postTickHook(CallbackInfo callbackInfo) {
        PlayerTickEvent class130Var = new PlayerTickEvent(TickStage.POST);
        Expensive.INSTANCE.eventDispatcher().dispatch(class130Var);
        if (class130Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"requestRespawn"}, at = {@At("HEAD")})
    private void requestRespawn(CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new RespawnEvent());
    }

    @Inject(method = {"move"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V")}, cancellable = true)
    public void move(MovementType movementType, Vec3d vec3d, CallbackInfo callbackInfo) {
        PlayerMoveEvent class039Var = new PlayerMoveEvent(vec3d);
        Expensive.INSTANCE.eventDispatcher().dispatch(class039Var);
        if (class039Var.isCancelled()) {
            super.move(movementType, class039Var.movement());
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
    public void pushOutOfBlocks(double d, double d2, CallbackInfo callbackInfo) {
        PushEvent class231Var = new PushEvent(PushType.BLOCKS);
        Expensive.INSTANCE.eventDispatcher().dispatch(class231Var);
        if (class231Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"closeHandledScreen"}, at = {@At("HEAD")}, cancellable = true)
    private void closeHandledScreenHook(CallbackInfo callbackInfo) {
        CloseScreenEvent class270Var = new CloseScreenEvent(this.client.currentScreen);
        Expensive.INSTANCE.eventDispatcher().dispatch(class270Var);
        if (class270Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tickMovement"}, at = {@At("HEAD")}, cancellable = true)
    public void tickMovement(CallbackInfo callbackInfo) {
        if (this.ticksLeftToDoubleTapSprint > 0) {
            this.ticksLeftToDoubleTapSprint--;
        }
        if (!(this.client.currentScreen instanceof LevelLoadingScreen)) {
            tickNausea(getCurrentPortalEffect() == Portal.Effect.CONFUSION);
            tickPortalCooldown();
        }
        boolean z = this.input.playerInput.jump() && !((ElytraHelperModule) Expensive.INSTANCE.moduleRepository().get(ElytraHelperModule.class)).canStart() && !((ElytraRecastModule) Expensive.INSTANCE.moduleRepository().get(ElytraRecastModule.class)).shouldResetJump();
        boolean zSneak = this.input.playerInput.sneak();
        boolean zMethod_20623 = this.input.hasForwardMovement();
        PlayerAbilities abilities = getAbilities();
        this.inSneakingPose = (abilities.flying || isSwimming() || hasVehicle() || !canChangeIntoPose(EntityPose.CROUCHING) || (!isSneaking() && (isSleeping() || canChangeIntoPose(EntityPose.STANDING)))) ? false : true;
        this.input.tick();
        this.client.getTutorialManager().onMovement(this.input);
        if (shouldStopSprinting()) {
            setSprinting(false);
        }
        if (isUsingItem() && !hasVehicle()) {
            MovementInputEvent3 class289Var = new MovementInputEvent3(this.input.getMovementInput().y, this.input.getMovementInput().x);
            Expensive.INSTANCE.eventDispatcher().dispatch(class289Var);
            if (class289Var.isCancelled()) {
                InputMixin inputAccessor = (InputMixin) this.input;
                inputAccessor.expensive$setMovementVector(inputAccessor.expensive$getMovementVector().multiply(5.0f));
            } else {
                this.ticksLeftToDoubleTapSprint = 0;
            }
        }
        if (shouldSlowDown()) {
            float attributeValue = (float) getAttributeValue(EntityAttributes.SNEAKING_SPEED);
            InputMixin inputAccessor = (InputMixin) this.input;
            inputAccessor.expensive$setMovementVector(inputAccessor.expensive$getMovementVector().multiply(attributeValue));
        }
        boolean z2 = false;
        if (this.ticksToNextAutoJump > 0) {
            this.ticksToNextAutoJump--;
            z2 = true;
            this.input.jump();
        }
        if (!this.noClip) {
            pushOutOfBlocks(getX() - (((double) getWidth()) * 0.35d), getZ() + (((double) getWidth()) * 0.35d));
            pushOutOfBlocks(getX() - (((double) getWidth()) * 0.35d), getZ() - (((double) getWidth()) * 0.35d));
            pushOutOfBlocks(getX() + (((double) getWidth()) * 0.35d), getZ() - (((double) getWidth()) * 0.35d));
            pushOutOfBlocks(getX() + (((double) getWidth()) * 0.35d), getZ() + (((double) getWidth()) * 0.35d));
        }
        if (zSneak) {
            this.ticksLeftToDoubleTapSprint = 0;
        }
        MovementUpdateEvent class308Var = new MovementUpdateEvent(DirectionalInput.fromInput(this.input.playerInput), this.client.options.sprintKey.isPressed(), MovementUpdateSource.MOVEMENT_TICK);
        Expensive.INSTANCE.eventDispatcher().dispatch(class308Var);
        boolean zMethod_48300 = canStartSprinting();
        boolean zIsOnGround = hasVehicle() ? getVehicle().isOnGround() : isOnGround();
        boolean z3 = (zSneak || zMethod_20623) ? false : true;
        if ((zIsOnGround || isSubmergedInWater()) && z3 && zMethod_48300) {
            if (this.ticksLeftToDoubleTapSprint > 0 || class308Var.isSprint()) {
                setSprinting(true);
            } else {
                this.ticksLeftToDoubleTapSprint = 7;
            }
        }
        if ((!isTouchingWater() || isSubmergedInWater()) && zMethod_48300 && class308Var.isSprint()) {
            setSprinting(true);
        }
        if (isSprinting()) {
            MovementUpdateEvent class308Var2 = new MovementUpdateEvent(DirectionalInput.fromInput(this.input.playerInput), canSprint(abilities.flying), MovementUpdateSource.MOVEMENT_TICK);
            Expensive.INSTANCE.eventDispatcher().dispatch(class308Var2);
            boolean z4 = (this.input.hasForwardMovement() && class308Var2.isSprint()) ? false : true;
            boolean z5 = z4 || (this.horizontalCollision && !this.collidedSoftly) || (isTouchingWater() && !isSubmergedInWater());
            if (isSwimming()) {
                if ((!isOnGround() && !this.input.playerInput.sneak() && z4) || !isTouchingWater()) {
                    setSprinting(false);
                }
            } else if (z5) {
                setSprinting(false);
            }
        }
        boolean z6 = false;
        if (abilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!abilities.flying) {
                    abilities.flying = true;
                    z6 = true;
                    sendAbilitiesUpdate();
                }
            } else if (!z && this.input.playerInput.jump() && !z2) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!isSwimming()) {
                    abilities.flying = !abilities.flying;
                    if (abilities.flying && isOnGround()) {
                        jump();
                    }
                    z6 = true;
                    sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }
        if (this.input.playerInput.jump() && !z6 && !z && !isClimbing() && checkGliding()) {
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
        this.falling = isGliding();
        if (isTouchingWater() && this.input.playerInput.sneak() && shouldSwimInFluids()) {
            knockDownwards();
        }
        if (isSubmergedIn(FluidTags.WATER)) {
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + (isSpectator() ? 10 : 1), 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }
        if (abilities.flying && isCamera()) {
            int i = 0;
            if (this.input.playerInput.sneak()) {
                i = 0 - 1;
            }
            if (this.input.playerInput.jump()) {
                i++;
            }
            if (i != 0) {
                setVelocity(getVelocity().add(0.0d, i * abilities.getFlySpeed() * 3.0f, 0.0d));
            }
        }
        JumpingMount jumpingMountMethod_45773 = getJumpingMount();
        if (jumpingMountMethod_45773 == null || jumpingMountMethod_45773.getJumpCooldown() != 0) {
            this.mountJumpStrength = 0.0f;
        } else {
            if (this.mountJumpTicks < 0) {
                this.mountJumpTicks++;
                if (this.mountJumpTicks == 0) {
                    this.mountJumpStrength = 0.0f;
                }
            }
            if (z && !this.input.playerInput.jump()) {
                this.mountJumpTicks = -10;
                jumpingMountMethod_45773.setJumpStrength(MathHelper.floor(getMountJumpStrength() * 100.0f));
                startRidingJump();
            } else if (!z && this.input.playerInput.jump()) {
                this.mountJumpTicks = 0;
                this.mountJumpStrength = 0.0f;
            } else if (z) {
                this.mountJumpTicks++;
                if (this.mountJumpTicks < 10) {
                    this.mountJumpStrength = this.mountJumpTicks * 0.1f;
                } else {
                    this.mountJumpStrength = 0.8f + ((2.0f / (this.mountJumpTicks - 9)) * 0.1f);
                }
            }
        }
        super.tickMovement();
        if (isOnGround() && abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            abilities.flying = false;
            sendAbilitiesUpdate();
        }
        callbackInfo.cancel();
    }

    @ModifyExpressionValue(method = {"canSprint"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasBlindnessEffect()Z")})
    private boolean hookSprintIgnoreBlindness(boolean z) {
        OverlayEffectEvent class069Var = new OverlayEffectEvent(OverlayEffectType.BLIDNESS);
        Expensive.INSTANCE.eventDispatcher().dispatch(class069Var);
        return !class069Var.isCancelled() && z;
    }

    @ModifyExpressionValue(method = {"canSprint"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canSprintOrFly()Z")})
    private boolean canSprintHook(boolean original) {
        OverlayEffectEvent class069Var = new OverlayEffectEvent(OverlayEffectType.HUNGER);
        Expensive.INSTANCE.eventDispatcher().dispatch(class069Var);
        if (class069Var.isCancelled()) {
            return true;
        }
        return original;
    }

    @Inject(method = {"sendMovementPackets"}, at = {@At("HEAD")}, cancellable = true)
    private void sendMovementPackets(CallbackInfo callbackInfo) {
        PlayerPositionEvent class316Var = new PlayerPositionEvent(PlayerPositionStage.PRE, getX(), getY(), getZ(), getYaw(), getPitch(), isOnGround());
        Expensive.INSTANCE.eventDispatcher().dispatch(class316Var);
        if (class316Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F")})
    private float hookSilentRotationYaw(float f) {
        Rotation currentRotationOriginal = PlayerRotationManager.INSTANCE.getCurrentRotationOriginal();
        return currentRotationOriginal == null ? f : currentRotationOriginal.getYaw();
    }

    @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F")})
    private float hookSilentRotationPitch(float f) {
        Rotation currentRotationOriginal = PlayerRotationManager.INSTANCE.getCurrentRotationOriginal();
        return currentRotationOriginal == null ? f : currentRotationOriginal.getPitch();
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", shift = At.Shift.AFTER)})
    private void postSendMovementPackets(CallbackInfo callbackInfo) {
        Expensive.INSTANCE.eventDispatcher().dispatch(new PlayerPositionEvent(PlayerPositionStage.POST, getX(), getY(), getZ(), getYaw(), getPitch(), isOnGround()));
    }

    public boolean clipAtLedge() {
        ClipAtLedgeEvent class250Var = new ClipAtLedgeEvent(super.clipAtLedge());
        Expensive.INSTANCE.eventDispatcher().dispatch(class250Var);
        return class250Var.isClip();
    }
}
