package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import thunder.hack.events.impl.EventMove;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.MovementUtility;

public class StrafeV2 extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.Vanilla);
    private final Setting<Boolean> autoJump = new Setting<>("AutoJump", false);
    private final Setting<Boolean> speedBuff = new Setting<>("SpeedBuff", true);

    private double currentSpeed = 0.0;
    private int stage = 1;

    public StrafeV2() {
        super("StrafeV2", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        currentSpeed = MovementUtility.getSpeed();
        stage = 1;
    }

    @EventHandler
    public void onMove(EventMove event) {
        if (fullNullCheck()) return;

        // Если мы не нажимаем кнопки ходьбы - стоим на месте
        if (!MovementUtility.isMoving()) {
            currentSpeed = 0.0;
            event.setX(0.0);
            event.setZ(0.0);
            return;
        }

        switch (mode.getValue()) {
            case Vanilla -> {
                // Ванильный стрейф - просто задаем одинаковую скорость во все стороны
                currentSpeed = getBaseSpeed();
                double[] dir = MovementUtility.forward(currentSpeed);
                event.setX(dir[0]);
                event.setZ(dir[1]);

                if (autoJump.getValue() && mc.player.isOnGround()) {
                    mc.player.jump();
                    event.setY(mc.player.getVelocity().y);
                }
            }
            case NCP -> {
                // Продвинутый стрейф с распрыжкой (NCP Bypass / Momentum)
                if (mc.player.isOnGround()) {
                    stage = 2;
                }

                if (stage == 1 && MovementUtility.isMoving()) {
                    currentSpeed = 1.35 * getBaseSpeed() - 0.01;
                } else if (stage == 2 && MovementUtility.isMoving()) {
                    if (autoJump.getValue() || mc.options.jumpKey.isPressed()) {
                        event.setY(0.4f); // Высота ванильного прыжка
                        mc.player.setVelocity(mc.player.getVelocity().x, 0.4f, mc.player.getVelocity().z);
                        currentSpeed *= speedBuff.getValue() ? 2.149 : 1.6;
                    }
                } else if (stage == 3) {
                    double difference = 0.66 * (currentSpeed - getBaseSpeed());
                    currentSpeed = currentSpeed - difference;
                } else {
                    if (mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)).iterator().hasNext() || mc.player.verticalCollision) {
                        stage = 1;
                    }
                    currentSpeed = currentSpeed - currentSpeed / 159.0;
                }

                currentSpeed = Math.max(currentSpeed, getBaseSpeed());

                double[] dir = MovementUtility.forward(currentSpeed);
                event.setX(dir[0]);
                event.setZ(dir[1]);

                stage++;
            }
        }
    }

    // Рассчитываем базовую скорость в зависимости от эффектов (Спешка / Speed)
    private double getBaseSpeed() {
        double baseSpeed = 0.2873; // Стандартная скорость бега в майнкрафте
        if (mc.player != null && mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public enum Mode {
        Vanilla, NCP
    }
}