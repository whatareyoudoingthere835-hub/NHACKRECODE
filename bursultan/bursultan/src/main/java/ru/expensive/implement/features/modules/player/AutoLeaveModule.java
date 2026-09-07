package ru.expensive.implement.features.modules.player;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.api.event.EventHandler;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AutoLeaveModule extends Module {

    final ValueSetting distanceSetting = new ValueSetting("Distance", "Specifies range to detect")
            .setValue(100.0F)
            .range(10.0F, 100.0F);

    final SelectSetting leaveAction = new SelectSetting("Leave Action", "Specifies the action to perform on detection")
            .value("/hub", "/spawn", "/clan home", "Disconnect");

    final BooleanSetting ignoreFriendsSetting = new BooleanSetting("Ignore Friends", "Ignores friends when checking for nearby players")
            .setValue(true);

    boolean triggered = false;

    public AutoLeaveModule() {
        super("AutoLeave", "Auto Leave", ModuleCategory.PLAYER);
        setup(distanceSetting, leaveAction, ignoreFriendsSetting);
    }

    @Override
    public void activate() {
        super.activate();
        triggered = false;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null || triggered) {
            return;
        }

        Vec3d playerPos = mc.player.getEntityPos();
        float maxDistance = distanceSetting.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity nearbyPlayer && isValid(nearbyPlayer)) {
                double distance = playerPos.distanceTo(nearbyPlayer.getEntityPos());
                if (distance <= maxDistance) {
                    performLeaveAction(nearbyPlayer.getName().getString(), distance);
                    triggered = true;
                    deactivate();
                    return;
                }
            }
        }
    }

    private boolean isValid(PlayerEntity player) {
        assert mc.player != null;
        if (mc.player.getId() == player.getId()) {
            return false;
        }
        return !ignoreFriendsSetting.isValue() || !FriendRepository.isFriend(player.getName().getString());
    }

    private void performLeaveAction(String playerName, double distance) {
        if (mc.getNetworkHandler() == null) {
            return;
        }
        String action = leaveAction.getSelected();
        switch (action) {
            case "/hub" -> mc.getNetworkHandler().sendChatCommand("hub");
            case "/spawn" -> mc.getNetworkHandler().sendChatCommand("spawn");
            case "/clan home" -> mc.getNetworkHandler().sendChatCommand("clan home");
            case "Disconnect" -> disconnect(playerName, distance);
        }
    }

    private void disconnect(String playerName, double distance) {
        String reason = String.format("AutoLeave\n\nРядом обнаружен игрок %s на расстоянии %.2f блоков. Отключение.\nДля повторного использования модуля AutoLeave, необходимо его включить повторно", playerName, distance);
        Text text = Text.of(reason);
        // mc.execute() НЕ подходит: на render-потоке (а тик игрока — это он)
        // задача выполняется СРАЗУ, мир зануляется посреди MinecraftClient.tick()
        // между проверкой world != null и world.tickBlockEntities() -> NPE и краш.
        // mc.send() всегда кладёт задачу в очередь — она отработает между кадрами,
        // где world == null уже обработан ваниллой.
        mc.send(() -> mc.disconnect(text));
    }
}
