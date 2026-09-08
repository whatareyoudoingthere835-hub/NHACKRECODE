package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;

import java.util.concurrent.ThreadLocalRandom;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class PacketTeleport extends Module {

    private final Setting<String> xPos = new Setting<>("X", "0.0");
    private final Setting<String> yPos = new Setting<>("Y", "100.0");
    private final Setting<String> zPos = new Setting<>("Z", "0.0");
    private final Setting<Boolean> bypass = new Setting<>("Bypass", false); // Попробуй сначала без bypass
    private final Setting<Integer> packets = new Setting<>("Packets", 1, 1, 20); // Количество повторений пакета
    private final Setting<Boolean> confirmation = new Setting<>("Confirmation", true); // Подтверждение телепорта

    private int teleportId = -1;

    public PacketTeleport() {
        super("PacketTeleport", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            disable();
            return;
        }

        double x, y, z;

        try {
            x = Double.parseDouble(xPos.getValue().replace(",", ".").trim());
            y = Double.parseDouble(yPos.getValue().replace(",", ".").trim());
            z = Double.parseDouble(zPos.getValue().replace(",", ".").trim());
        } catch (NumberFormatException e) {
            Managers.NOTIFICATION.publicity(
                    "PacketTeleport",
                    isRu() ? "Ошибка парсинга координат!" : "Coordinate parsing error!",
                    3,
                    Notification.Type.ERROR
            );
            disable();
            return;
        }

        // Проверка на валидность координат
        if (Math.abs(x) > 30000000 || Math.abs(z) > 30000000 || y < -64 || y > 320) {
            Managers.NOTIFICATION.publicity(
                    "PacketTeleport",
                    isRu() ? "Координаты вне границ мира!" : "Coordinates out of world bounds!",
                    3,
                    Notification.Type.ERROR
            );
            disable();
            return;
        }

        // Сохраняем старые координаты для диагностики
        double oldX = mc.player.getX();
        double oldY = mc.player.getY();
        double oldZ = mc.player.getZ();

        if (bypass.getValue()) {
            sendBypassTeleport(x, y, z);
        } else {
            sendNormalTeleport(x, y, z);
        }

        Managers.NOTIFICATION.publicity(
                "PacketTeleport",
                String.format(isRu() ? "TP: %.1f %.1f %.1f → %.1f %.1f %.1f" : "TP: %.1f %.1f %.1f → %.1f %.1f %.1f",
                        oldX, oldY, oldZ, x, y, z),
                4,
                Notification.Type.SUCCESS
        );

        disable();
    }

    private void sendNormalTeleport(double x, double y, double z) {
        // Отправляем несколько пакетов для надежности
        for (int i = 0; i < packets.getValue(); i++) {
            sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
        }

        // Подтверждаем телепорт если нужно
        if (confirmation.getValue() && teleportId != -1) {
            sendPacket(new TeleportConfirmC2SPacket(teleportId + 1));
        }

        // Локально перемещаем игрока
        mc.player.setPosition(x, y, z);
        mc.player.setVelocity(0, 0, 0);
    }

    private void sendBypassTeleport(double x, double y, double z) {
        // Основной пакет телепорта
        for (int i = 0; i < packets.getValue(); i++) {
            sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
        }

        // Rubberbanding трюк
        int border = getWorldBorder();
        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x + border,
                y,
                z + border,
                true
        ));

        // Возврат на целевую позицию
        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));

        // Подтверждение
        if (teleportId != -1) {
            sendPacket(new TeleportConfirmC2SPacket(teleportId + 1));
        }

        // Локальное перемещение
        mc.player.setPosition(x, y, z);
        mc.player.setVelocity(0, 0, 0);
    }

    private int getWorldBorder() {
        if (mc.isInSingleplayer()) return 10000; // Для синглплеера меньше
        int n = ThreadLocalRandom.current().nextInt(1000000, 29000000);
        return ThreadLocalRandom.current().nextBoolean() ? n : -n;
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket pac) {
            teleportId = pac.getTeleportId();

            // Диагностика
            if (isEnabled()) {
                Managers.NOTIFICATION.publicity(
                        "PacketTeleport",
                        isRu() ? "Получен S2C телепорт ID: " + teleportId : "Received S2C teleport ID: " + teleportId,
                        2,
                        Notification.Type.INFO
                );
            }
        }
    }
}