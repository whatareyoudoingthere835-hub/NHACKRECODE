package thunder.hack.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtility;

public class SpinBot extends Module {

    public SpinBot() {
        super("SpinBot", Category.PLAYER);
    }

    // Режимы вращения
    private final Setting<YawMode> yawMode = new Setting<>("YawMode", YawMode.Spin);
    private final Setting<PitchMode> pitchMode = new Setting<>("PitchMode", PitchMode.Down);

    // Тонкая настройка
    private final Setting<Integer> speed = new Setting<>("Speed", 30, 1, 180);
    private final Setting<Integer> yawOffset = new Setting<>("YawOffset", 0, -180, 180);

    // Вспомогательные настройки
    private final Setting<Boolean> bodySync = new Setting<>("BodySync", true);
    private final Setting<Boolean> allowInteract = new Setting<>("AllowInteract", true);

    private float rotationYaw;
    private float rotationPitch;
    private float sinusStep;

    @Override
    public void onEnable() {
        super.onEnable();
        if (fullNullCheck()) return;
        rotationYaw = mc.player.getYaw();
        rotationPitch = mc.player.getPitch();
        sinusStep = 0f;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCalc(PlayerUpdateEvent e) {
        if (fullNullCheck()) return;

        // Если бьем или строим - сброс градусов
        if (allowInteract.getValue() && (mc.options.attackKey.isPressed() || mc.options.useKey.isPressed())) {
            rotationYaw = mc.player.getYaw();
            rotationPitch = mc.player.getPitch();
            return;
        }

        // --- ЛОГИКА ГОРИЗОНТАЛИ (YAW) ---
        switch (yawMode.getValue()) {
            case Spin -> {
                rotationYaw += speed.getValue();
                if (rotationYaw > 360) rotationYaw -= 360;
                if (rotationYaw < 0) rotationYaw += 360;
            }
            case Sinus -> {
                sinusStep += speed.getValue() / 10f;
                rotationYaw = (float) ((mc.player.getYaw() + 180 * Math.sin(sinusStep)) + yawOffset.getValue());
            }
            case Random -> {
                if (mc.player.age % 2 == 0) rotationYaw = MathUtility.random(0, 360) + yawOffset.getValue();
            }
            case Jitter -> {
                rotationYaw = mc.player.getYaw() + MathUtility.random(-speed.getValue(), speed.getValue()) + yawOffset.getValue();
            }
            case Fixed -> {
                rotationYaw = mc.player.getYaw() + yawOffset.getValue();
            }
        }

        // --- ЛОГИКА ВЕРТИКАЛИ (PITCH) ---
        switch (pitchMode.getValue()) {
            case Down -> rotationPitch = 90f;
            case Up -> rotationPitch = -90f;
            case Random -> {
                if (mc.player.age % 2 == 0) rotationPitch = MathUtility.random(-90, 90);
            }
            case Jitter -> rotationPitch = 90f - MathUtility.random(0, speed.getValue());
            case None -> rotationPitch = mc.player.getPitch();
        }
    }

    @EventHandler(priority = 99)
    public void onSync(EventSync e) {
        if (fullNullCheck()) return;
        if (allowInteract.getValue() && (mc.options.attackKey.isPressed() || mc.options.useKey.isPressed())) return;

        // Фикс для обхода античитов (симуляция движения мыши)
        double gcdFix = (Math.pow(mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2, 3.0)) * 1.2;

        float finalYaw = (float) (rotationYaw - (rotationYaw - mc.player.getYaw()) % gcdFix);
        float finalPitch = (float) (rotationPitch - (rotationPitch - mc.player.getPitch()) % gcdFix);

        // Отправка клиенту (Silent от первого лица)
        mc.player.setYaw(finalYaw);
        mc.player.setPitch(finalPitch);

        ModuleManager.rotations.fixRotation = finalYaw;

        // Рендер тела для 3 лица (чтобы ты видел, как крутишься в F5)
        if (bodySync.getValue()) {
            mc.player.setBodyYaw(finalYaw);
            mc.player.setHeadYaw(finalYaw);
        }
    }

    public enum YawMode {
        Spin, Sinus, Random, Jitter, Fixed, None
    }

    public enum PitchMode {
        Down, Up, Random, Jitter, None
    }
}