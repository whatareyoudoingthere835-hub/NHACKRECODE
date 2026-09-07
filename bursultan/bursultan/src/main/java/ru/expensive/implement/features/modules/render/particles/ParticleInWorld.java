package ru.expensive.implement.features.modules.render.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.expensive.api.feature.module.setting.implement.*;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.render.WorldRenderEvent;
import ru.expensive.implement.features.modules.render.ParticlesModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleInWorld {
    private static final float SPAWN_RADIUS = 15.0f;
    private static final float MAX_HEIGHT = 15.0f;
    private static final float MIN_PARTICLE_SIZE = 0.02f;
    private static final float MAX_PARTICLE_SIZE = 0.06f;
    private static final int SPAWN_RATE = 20;
    private static final int PARTICLES_PER_SPAWN = 3;


    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Random random = new Random();
    private final ParticlesModule module;
    private final List<WorldParticle> particles = new ArrayList<>();
    private long lastSpawnTime;

    public ParticleInWorld(ParticlesModule module) {
        this.module = module;
    }

    public void onRender(WorldRenderEvent event) {
        if (mc.player == null) return;
        Vec3d playerPos = mc.player.getEntityPos();

        particles.removeIf(particle -> particle.position.distanceTo(playerPos) > SPAWN_RADIUS);

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_RATE) {
            ValueSetting particleCountSetting = (ValueSetting) module.getWorldSettings().getSubSetting("Particle Count");
            int maxParticles = (int) particleCountSetting.getValue();

            for (int i = 0; i < PARTICLES_PER_SPAWN && particles.size() < maxParticles; i++) {
                spawnParticle(playerPos);
            }
            lastSpawnTime = currentTime;
        }

        renderParticles(event.getStack());
    }

    private void spawnParticle(Vec3d playerPos) {
        if (mc.player == null || mc.world == null) return;

        double angle = random.nextDouble() * Math.PI * 2;
        double radius = SPAWN_RADIUS * Math.sqrt(random.nextDouble());

        double offsetX = Math.cos(angle) * radius;
        double offsetZ = Math.sin(angle) * radius;

        double x = playerPos.x + offsetX;
        double y = playerPos.y + random.nextDouble() * MAX_HEIGHT - (MAX_HEIGHT / 2);
        double z = playerPos.z + offsetZ;

        if (!mc.world.getBlockState(new BlockPos((int)x, (int)y, (int)z)).isAir()) {
            return;
        }

        List<String> selectedParticles = module.getParticlesSetting().getSelected();
        if (selectedParticles.isEmpty()) return;

        ValueSetting spreadStrengthSetting = (ValueSetting) module.getWorldSettings().getSubSetting("Spread Strength");
        ValueSetting gravityStrengthSetting = (ValueSetting) module.getWorldSettings().getSubSetting("Gravity Strength");

        String particleType = selectedParticles.get(random.nextInt(selectedParticles.size()));

        Vec3d particlePos = new Vec3d(x, y, z);
        if (particlePos.distanceTo(playerPos) <= SPAWN_RADIUS) {
            particles.add(new WorldParticle(
                    particlePos,
                    particleType,
                    random,
                    spreadStrengthSetting.getValue(),
                    gravityStrengthSetting.getValue()
            ));
        }
    }

    private void renderParticles(MatrixStack matrixStack) {
        ValueSetting lifetimeSetting = (ValueSetting) module.getWorldSettings().getSubSetting("Lifetime");
        ColorSetting colorSetting = (ColorSetting) module.getWorldSettings().getSubSetting("Particle Color");

        particles.removeIf(particle -> {
            float age = (System.currentTimeMillis() - particle.creationTime) / 1000f;
            if (age > lifetimeSetting.getValue()) return true;

            particle.update();
            renderParticle(matrixStack, particle, age, colorSetting.getColor());
            return false;
        });
    }

    private void renderParticle(MatrixStack matrixStack, WorldParticle particle, float age, int color) {
        // Движок рисует только в экранных координатах: проецируем точку и рисуем
        // квадрат в пикселях. Мировые матрицы сюда передавать нельзя — будет мусор.
        Vector2f projection = ProjectionUtil.project(particle.position.x, particle.position.y, particle.position.z);
        if (!ProjectionUtil.isValidProjection(projection)) {
            return;
        }

        ValueSetting lifetimeSetting = (ValueSetting) module.getWorldSettings().getSubSetting("Lifetime");
        float lifeProgress = age / lifetimeSetting.getValue();

        ColorSetting colorSetting = (ColorSetting) module.getWorldSettings().getSubSetting("Particle Color");
        int currentColor = colorSetting.interpolateColor(lifeProgress);

        float alpha = age > lifetimeSetting.getValue() - 0.2f ?
                1.0f - (age - (lifetimeSetting.getValue() - 0.2f)) / 0.2f : 1.0f;

        int finalColor = (((int)(alpha * ((currentColor >> 24) & 0xFF)) & 0xFF) << 24) | (currentColor & 0x00FFFFFF);

        boolean bloomEnabled = ((BooleanSetting)module.getTrailsSettings().getSubSetting("Bloom")).isValue();

        float distance = (float) mc.player.getEntityPos().distanceTo(particle.position);
        float size = MathHelper.clamp(30.0F / Math.max(distance, 0.5F), 2.0F, 12.0F);

        MatrixStack identity = new MatrixStack();
        QuickImports.image.setMatrixStack(identity)
                .setTexture("images/particles/" + particle.particleType.toLowerCase() + ".png")
                .render(ShapeProperties.create(identity.peek().getPositionMatrix(),
                                projection.x() - size / 2, projection.y() - size / 2, size, size)
                        .color(finalColor)
                        .bloom(bloomEnabled)
                        .build());
    }

    private static class WorldParticle {
        Vec3d position;
        final String particleType;
        final long creationTime;
        float size;
        double velocityX, velocityY, velocityZ;
        final float gravityStrength;

        WorldParticle(Vec3d position, String type, Random random, float spreadStrength, float gravityStrength) {
            this.position = position;
            this.particleType = type;
            this.creationTime = System.currentTimeMillis();
            this.size = MIN_PARTICLE_SIZE + random.nextFloat() * (MAX_PARTICLE_SIZE - MIN_PARTICLE_SIZE);
            this.gravityStrength = gravityStrength;

            float spreadFactor = spreadStrength * 0.001f;
            this.velocityX = (random.nextFloat() - 0.5f) * spreadFactor;
            this.velocityY = (random.nextFloat() - 0.5f) * spreadFactor;
            this.velocityZ = (random.nextFloat() - 0.5f) * spreadFactor;
        }

        void update() {
            position = position.add(velocityX, velocityY, velocityZ);
            velocityY -= 0.00001f * gravityStrength;
        }
    }
}