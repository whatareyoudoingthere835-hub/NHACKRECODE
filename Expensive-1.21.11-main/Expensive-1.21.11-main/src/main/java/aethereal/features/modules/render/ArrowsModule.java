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

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Aliases(aliases = {"Arrows", "Triangles", "Tracers", "Indicators"})
public class ArrowsModule extends Module {
    public final GlTextureObject filledArrowTexture;
    public final GlTextureObject arrowTexture;
    public final NumberSetting radius;
    public final NumberSetting scale;
    public final BooleanSetting showDistance;
    public final BooleanSetting filled;
    public final BooleanSetting onlyFriends;
    public final BooleanSetting ignoreNaked;
    public final ColorSetting color;
    public float cameraYaw;
    public float animatedRadius;
    public int gpsX;
    public int gpsZ;
    public boolean gpsEnabled;
    public float alpha;
    public boolean deactivating;

    public ArrowsModule() {
        super(ModuleTab.RENDER, "Arrows");
        this.filledArrowTexture = new GlTextureObject(new ClasspathResource("/textures/filled_arrow.png")).minFilter(9729).magFilter(9729);
        this.arrowTexture = new GlTextureObject(new ClasspathResource("/textures/arrow.png")).minFilter(9729).magFilter(9729);
        this.radius = new NumberSetting(Lang.ARROWS_RADIUS, Lang.ARROWS_RADIUS_DESC).range(1.0f, 15.0f).currentValue(8.0f).step(0.05f).unit(SettingUnit.PIXELS);
        this.scale = new NumberSetting(Lang.ARROWS_SCALE, Lang.ARROWS_SCALE_DESC).range(0.1f, 1.0f).currentValue(0.4f).step(0.05f);
        this.showDistance = new BooleanSetting(Lang.ARROWS_SHOW_DISTANCE, Lang.ARROWS_SHOW_DISTANCE_DESC);
        this.filled = new BooleanSetting(Lang.ARROWS_FILLED, Lang.ARROWS_FILLED_DESC);
        this.onlyFriends = new BooleanSetting(Lang.ARROWS_ONLY_FRIEND, Lang.ARROWS_ONLY_FRIEND_DESC);
        this.ignoreNaked = new BooleanSetting(Lang.ATTACKAURA_IGNORE_NAKED);
        this.color = new ColorSetting(Lang.ARROWS_COLOR, Lang.ARROWS_COLOR_DESC);
        this.gpsX = 100;
        this.gpsZ = 100;
        this.gpsEnabled = false;
        this.alpha = 1.0f;
        this.deactivating = false;
        addSettings(this.radius, this.scale, this.showDistance, this.filled, this.onlyFriends, this.ignoreNaked, this.color);
        register(Render2DEvent.class, class311Var -> {
            int iDistanceTo;
            Mc class815Var= Mc.INSTANCE;
            if (!class815Var.isWorldLoaded() || !class311Var.isPre()) {
                return;
            }
            updateAnimation(class815Var.getCurrentScreen());
            boolean z= isState() || this.deactivating;
            boolean z2= this.gpsEnabled;
            if (z || z2) {
                MatrixStack matrixStack= class311Var.matrixStack();
                Vec3d pos= class815Var.getCamera().getCameraPos();
                Window window= class815Var.getWindow();
                GraphicsDrawEngine class154VarDrawEngine= Expensive.INSTANCE.drawEngine();
                ClientPlayerEntity player= class815Var.getPlayer();
                PaletteColorStack class115VarColorStack= class154VarDrawEngine.colorStack();
                class154VarDrawEngine.begin();
                int i= this.filled.isValue() ? 80 : 85;
                int i2= i / 2;
                float tickDelta= class815Var.getTickDelta();
                this.cameraYaw = Mc.INSTANCE.getEntityRenderDispatcher().camera.getYaw();
                int iBindTexture= this.filled.isValue() ? class154VarDrawEngine.bindTexture(this.filledArrowTexture.textureWithSTB()) : class154VarDrawEngine.bindTexture(this.arrowTexture.textureWithSTB());
                class115VarColorStack.push();
                if (z) {
                    Stream<Entity> streamFilter= IteratorUtil.toList(class815Var.getWorld().getEntities().iterator()).stream().filter((v0) -> {
                        return v0.isAlive();
                    }).filter(entity -> {
                        return (entity instanceof PlayerEntity) && entity != player;
                    });
                    Objects.requireNonNull(player);
                    Entity entity2= null;
                    for (Entity entity3 : streamFilter.sorted(Comparator.comparingDouble(player::distanceTo)).toList()) {
                        if (entity2 == null || ((int) entity2.getX()) != ((int) entity3.getX()) || ((int) entity2.getZ()) != ((int) entity3.getZ())) {
                            if (!this.onlyFriends.isValue() || FriendManager.isFriend(entity3.getName().getString())) {
                                if (!this.ignoreNaked.isValue() || !(entity3 instanceof PlayerEntity) || !EquipmentUtil.armor((PlayerEntity) entity3).stream().allMatch((v0) -> {
                                    return v0.isEmpty();
                                })) {
                                    entity2 = entity3;
                                    double x= ((entity3.getX() + ((entity3.getX() - entity3.lastX) * ((double) tickDelta))) - pos.getX()) * 0.01d;
                                    double z3= ((entity3.getZ() + ((entity3.getZ() - entity3.lastZ) * ((double) tickDelta))) - pos.getZ()) * 0.01d;
                                    double dCos= Math.cos(((double) Mc.INSTANCE.getEntityRenderDispatcher().camera.getYaw()) * 0.017453292519943295d);
                                    double dSin= Math.sin(((double) Mc.INSTANCE.getEntityRenderDispatcher().camera.getYaw()) * 0.017453292519943295d);
                                    double d= -((z3 * dCos) - (x * dSin));
                                    double d2= -((x * dCos) + (z3 * dSin));
                                    double d3= -d;
                                    double d4= -d2;
                                    double d5= (this.animatedRadius * 2.0f) + this.animatedRadius;
                                    double framebufferWidth= ((window.getFramebufferWidth() / 2.0f) - this.animatedRadius) - (this.animatedRadius / 2.0f);
                                    double framebufferHeight= ((window.getFramebufferHeight() / 2.0f) - this.animatedRadius) - (this.animatedRadius / 2.0f);
                                    double dAtan2= (Math.atan2(d, d2) * 180.0d) / 3.141592653589793d;
                                    double dCos2= ((d5 / 2.0d) * Math.cos(Math.toRadians(dAtan2))) + framebufferWidth + (d5 / 2.0d);
                                    double dSin2= ((d5 / 2.0d) * Math.sin(Math.toRadians(dAtan2))) + framebufferHeight + (d5 / 2.0d);
                                    if (MathHelper.sqrt((float) ((d4 * d4) + (d3 * d3))) < d5 / 2.0d && (iDistanceTo = (int) player.distanceTo(entity3)) > 0) {
                                        matrixStack.push();
                                        matrixStack.translate(dCos2, dSin2, 0.0d);
                                        matrixStack.scale(this.scale.currentValue(), this.scale.currentValue(), 0.0f);
                                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) dAtan2));
                                        int color= FriendManager.isFriend(entity3.getName().getString()) ? 8519559 : this.color.getColor();
                                        class115VarColorStack.alpha(this.alpha);
                                        class154VarDrawEngine.texture(matrixStack.peek().getPositionMatrix(), -i2, -i2, i, i, iBindTexture, class115VarColorStack.computeColor(color));
                                        matrixStack.pop();
                                        if (this.showDistance.isValue()) {
                                            String str= "%sm".formatted(FastMathUtils.decimalFormat(Integer.valueOf(iDistanceTo), 0));
                                            MsdfFont class161Var= Fonts.INTER_EXTRA_BOLD.get();
                                            class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), class161Var, str, ((float) dCos2) - (class161Var.getWidth(str, 9.0f) / 2.0f), (float) (dSin2 + ((double) (18.0f * this.scale.currentValue()))), 9.0f, 0.0f, class115VarColorStack.white());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                class115VarColorStack.pop();
                if (z2) {
                    double x2= (((double) this.gpsX) - pos.getX()) * 0.01d;
                    double z4= (((double) this.gpsZ) - pos.getZ()) * 0.01d;
                    double dCos3= Math.cos(((double) Mc.INSTANCE.getEntityRenderDispatcher().camera.getYaw()) * 0.017453292519943295d);
                    double dSin3= Math.sin(((double) Mc.INSTANCE.getEntityRenderDispatcher().camera.getYaw()) * 0.017453292519943295d);
                    double dAtan3= (Math.atan2(-((z4 * dCos3) - (x2 * dSin3)), -((x2 * dCos3) + (z4 * dSin3))) * 180.0d) / 3.141592653589793d;
                    double framebufferWidth2= ((double) window.getFramebufferWidth()) / 2.0d;
                    double scaledHeight= ((double) window.getScaledHeight()) / 4.0d;
                    double dHorizontalLength= new Vec3d(new BlockPos(this.gpsX, player.getBlockY(), this.gpsZ)).subtract(player.getEntityPos()).horizontalLength();
                    if (dHorizontalLength > 0.0d) {
                        String str2= "%sm".formatted(FastMathUtils.decimalFormat(Double.valueOf(dHorizontalLength), 0));
                        matrixStack.push();
                        matrixStack.translate(framebufferWidth2, scaledHeight, 0.0d);
                        matrixStack.scale(this.scale.currentValue(), this.scale.currentValue(), 0.0f);
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) dAtan3));
                        class154VarDrawEngine.texture(matrixStack.peek().getPositionMatrix(), -i2, -i2, i, i, iBindTexture, -1);
                        matrixStack.pop();
                        class154VarDrawEngine.msdfFontHorizontalC(matrixStack.peek().getPositionMatrix(), Fonts.INTER_EXTRA_BOLD.get(), str2, (float) framebufferWidth2, (float) (scaledHeight + ((double) (23.0f * this.scale.currentValue()))), 12.0f, 0.0f, -1);
                    }
                }
                class154VarDrawEngine.end();
            }
        });
    }

    @Override
    public void deactivate() {
        this.deactivating = true;
        super.deactivate();
    }

    public void updateAnimation(Screen screen) {
        float fCurrentValue;
        if (screen instanceof HandledScreen) {
            HandledScreen handledScreen= (HandledScreen) screen;
            fCurrentValue = Math.max(handledScreen.backgroundHeight, handledScreen.backgroundWidth);
        } else {
            fCurrentValue = 35.0f + (this.radius.currentValue() * 2.0f);
        }
        float f= fCurrentValue;
        if (isState()) {
            this.animatedRadius = lerpTowards(this.animatedRadius, f, 15.0f);
            this.alpha = lerpTowards(this.alpha, 1.0f, 15.0f);
            this.deactivating = false;
        } else if (this.deactivating) {
            this.animatedRadius = lerpTowards(this.animatedRadius, 15.0f, 15.0f);
            this.alpha = lerpTowards(this.alpha, 0.0f, 15.0f);
            if (this.alpha < 0.01f) {
                this.deactivating = false;
            }
        }
    }

    public float lerpTowards(float f, float f2, float f3) {
        return ((1.0f - FastMathUtils.clamp((float) (FastMathUtils.deltaTime() * ((double) f3)), 0.0f, 1.0f)) * f) + (FastMathUtils.clamp((float) (FastMathUtils.deltaTime() * ((double) f3)), 0.0f, 1.0f) * f2);
    }

    public void setGPS(int i, int i2) {
        this.gpsEnabled = true;
        this.gpsX = i;
        this.gpsZ = i2;
    }

    public void disableGPS() {
        this.gpsEnabled = false;
    }
}
