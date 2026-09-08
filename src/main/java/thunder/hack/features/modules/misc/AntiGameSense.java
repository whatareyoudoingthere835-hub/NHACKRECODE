package thunder.hack.features.modules.misc;

import thunder.hack.utility.player.ItemChecks;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import thunder.hack.events.impl.EventSetting;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AntiGameSense extends Module {

    private final Setting<Float> armorDropChance = new Setting<>("ArmorDropChance", 20f, 0f, 100f);
    private final Setting<Float> swordDropChance = new Setting<>("SwordDropChance", 20f, 0f, 100f);
    private final Setting<Float> cameraFlipChance = new Setting<>("CameraFlipChance", 30f, 0f, 100f);
    private final Setting<Integer> minDropDelay = new Setting<>("MinDropDelay", 50, 10, 500);
    private final Setting<Integer> maxDropDelay = new Setting<>("MaxDropDelay", 150, 50, 1000);

    private final Timer dropTimer = new Timer();
    private final Random random = new Random();
    private boolean hasFlippedCamera = false;
    private boolean hasAttacked = false;

    public AntiGameSense() {
        super("AntiGameSense", Category.MISC);
    }

    @Override
    public void onEnable() {
        hasFlippedCamera = false;
        hasAttacked = false;
        dropTimer.reset();
    }

    // Не даём ставить min > max и наоборот.
    // Setting.setValue() отправляет EventSetting через EventBus,
    // ловим его пока модуль включён (подписан на шину).
    @EventHandler
    public void onSettingChange(EventSetting e) {
        if (e.getSetting() == minDropDelay) {
            if (minDropDelay.getValue() > maxDropDelay.getValue())
                maxDropDelay.setValue(minDropDelay.getValue());
        } else if (e.getSetting() == maxDropDelay) {
            if (maxDropDelay.getValue() < minDropDelay.getValue())
                minDropDelay.setValue(maxDropDelay.getValue());
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (fullNullCheck()) return;
        if (!(e.getPacket() instanceof PlayerInteractEntityC2SPacket)) return;

        // Первая атака — шанс перевернуть камеру
        if (!hasAttacked) {
            hasAttacked = true;
            if (rollChance(cameraFlipChance.getValue()) && !hasFlippedCamera) {
                flipCamera();
                hasFlippedCamera = true;
            }
        }

        // Кулдаун между дропами чтобы не спамить
        if (!dropTimer.passedMs(getRandomDelay())) return;

        // При ударе — шанс дропнуть меч
        if (rollChance(swordDropChance.getValue()))
            dropSword();

        // При ударе — шанс дропнуть случайную броню
        if (rollChance(armorDropChance.getValue()))
            dropRandomArmor();

        dropTimer.reset();
    }

    private void dropRandomArmor() {
        List<EquipmentSlot> armorSlots = new ArrayList<>();

        if (!mc.player.getEquippedStack(EquipmentSlot.HEAD).isEmpty())
            armorSlots.add(EquipmentSlot.HEAD);
        if (!mc.player.getEquippedStack(EquipmentSlot.CHEST).isEmpty())
            armorSlots.add(EquipmentSlot.CHEST);
        if (!mc.player.getEquippedStack(EquipmentSlot.LEGS).isEmpty())
            armorSlots.add(EquipmentSlot.LEGS);
        if (!mc.player.getEquippedStack(EquipmentSlot.FEET).isEmpty())
            armorSlots.add(EquipmentSlot.FEET);

        if (armorSlots.isEmpty()) return;

        EquipmentSlot randomSlot = armorSlots.get(random.nextInt(armorSlots.size()));
        int slotId = getArmorSlotId(randomSlot);

        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                slotId,
                0,
                SlotActionType.THROW,
                mc.player
        );
    }

    private void dropSword() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (ItemChecks.isSword(stack)) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        i,
                        0,
                        SlotActionType.THROW,
                        mc.player
                );
                return;
            }
        }
    }

    private void flipCamera() {
        float currentYaw = mc.player.getYaw();
        float newYaw = currentYaw + 180f;
        while (newYaw > 180f) newYaw -= 360f;
        while (newYaw < -180f) newYaw += 360f;
        mc.player.setYaw(newYaw);
        mc.player.prevYaw = newYaw;
    }

    private int getArmorSlotId(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            default -> -1;
        };
    }

    private boolean rollChance(float chance) {
        return ThreadLocalRandom.current().nextFloat() * 100f < chance;
    }

    private long getRandomDelay() {
        int min = minDropDelay.getValue();
        int max = maxDropDelay.getValue();
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextLong(min, max + 1L);
    }
}