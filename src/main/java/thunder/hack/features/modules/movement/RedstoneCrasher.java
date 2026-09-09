package thunder.hack.features.modules.movement;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.SearchInvResult;
import thunder.hack.utility.render.BlockAnimationUtility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class RedstoneCrasher extends Module {

    private final Setting<Float> range = new Setting<>("Range", 4.0f, 1.0f, 6.0f);
    private final Setting<Integer> bpt = new Setting<>("BlocksPerTick", 2, 1, 10);
    private final Setting<Switch> autoSwitch = new Setting<>("Switch", Switch.Silent);
    private final Setting<Integer> rotationDelay = new Setting<>("RotationDelay", 1, 0, 15);

    private final Setting<SettingGroup> renderCategory = new Setting<>("Render", new SettingGroup(false, 0));
    private final Setting<Boolean> render = new Setting<>("Render", true).addToGroup(renderCategory);
    private final Setting<BlockAnimationUtility.BlockRenderMode> renderMode = new Setting<>("RenderMode", BlockAnimationUtility.BlockRenderMode.All).addToGroup(renderCategory);
    private final Setting<BlockAnimationUtility.BlockAnimationMode> animationMode = new Setting<>("AnimationMode", BlockAnimationUtility.BlockAnimationMode.Fade).addToGroup(renderCategory);
    private final Setting<ColorSetting> renderFillColor = new Setting<>("RenderFillColor", new ColorSetting(HudEditor.getColor(0))).addToGroup(renderCategory);
    private final Setting<ColorSetting> renderLineColor = new Setting<>("RenderLineColor", new ColorSetting(HudEditor.getColor(0))).addToGroup(renderCategory);
    private final Setting<Integer> renderLineWidth = new Setting<>("RenderLineWidth", 2, 1, 5).addToGroup(renderCategory);

    private enum Switch {
        Normal, Silent, Inventory, None
    }

    // Переменные для ротации
    private float targetYaw = 0f;
    private float targetPitch = 0f;
    private float currentYaw = 0f;
    private float currentPitch = 0f;
    private BlockPos currentTarget = null;
    private int rotationTicks = 0;
    private int prevItem = -1;
    private List<BlockPos> targetQueue = new ArrayList<>();
    private int placedThisTick = 0;

    public RedstoneCrasher() {
        super("RedstoneCrasher", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        currentYaw = mc.player.getYaw();
        currentPitch = mc.player.getPitch();
        currentTarget = null;
        rotationTicks = 0;
        targetQueue.clear();
        placedThisTick = 0;
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        placedThisTick = 0;

        // Проверяем наличие редстоуна
        if (!hasRedstone()) {
            currentTarget = null;
            return;
        }

        // Обновляем очередь целей
        if (currentTarget == null || targetQueue.isEmpty()) {
            targetQueue = getTargets();
            if (targetQueue.isEmpty()) return;
        }

        // Берем первую цель из очереди
        if (currentTarget == null && !targetQueue.isEmpty()) {
            currentTarget = targetQueue.remove(0);
            rotationTicks = 0;

            // Вычисляем целевую ротацию
            Vec3d hitVec = new Vec3d(currentTarget.getX() + 0.5, currentTarget.getY() + 0.5, currentTarget.getZ() + 0.5);
            float[] rotations = InteractionUtility.calculateAngle(hitVec);
            targetYaw = rotations[0];
            targetPitch = rotations[1];

            // Получаем редстоун в руку
            prevItem = prePlace();
            if (prevItem == -1) {
                currentTarget = null;
                return;
            }
        }
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (fullNullCheck()) return;
        if (currentTarget == null) return;

        // Плавно поворачиваемся к цели
        rotateToTarget();

        // Применяем ротацию к игроку
        mc.player.changeLookDirection((currentYaw) - mc.player.getYaw(), 0);
        mc.player.changeLookDirection(0, (currentPitch) - mc.player.getPitch());

        // Проверяем, достигли ли нужной ротации
        if (rotationTicks >= rotationDelay.getValue()) {
            if (isLookingAtTarget() && placedThisTick < bpt.getValue()) {
                placeRedstone(currentTarget, prevItem);
                placedThisTick++;

                // Возвращаем предмет, если был Silent
                postPlace(prevItem);

                // Переходим к следующей цели
                currentTarget = null;
                rotationTicks = 0;
            }
        }
    }

    private void rotateToTarget() {
        // Вычисляем разницу углов
        float deltaYaw = wrapDegrees(targetYaw - currentYaw);
        float deltaPitch = targetPitch - currentPitch;

        // Ограничиваем скорость поворота (чем больше delay, тем медленнее)
        float yawSpeed = rotationDelay.getValue() == 0 ? 180f : 180f / (rotationDelay.getValue() + 1);
        float pitchSpeed = rotationDelay.getValue() == 0 ? 90f : 90f / (rotationDelay.getValue() + 1);

        float yawStep = MathHelper.clamp(deltaYaw, -yawSpeed, yawSpeed);
        float pitchStep = MathHelper.clamp(deltaPitch, -pitchSpeed, pitchSpeed);

        currentYaw = wrapDegrees(currentYaw + yawStep);
        currentPitch = MathHelper.clamp(currentPitch + pitchStep, -90f, 90f);

        rotationTicks++;
    }

    private boolean isLookingAtTarget() {
        if (currentTarget == null) return false;

        // Проверяем, что ротация достаточно близка к цели
        float yawDiff = Math.abs(wrapDegrees(targetYaw - currentYaw));
        float pitchDiff = Math.abs(targetPitch - currentPitch);

        return yawDiff < 1f && pitchDiff < 1f;
    }

    private void placeRedstone(BlockPos pos, int prevItem) {
        BlockPos floor = pos.down();
        Vec3d hitVec = new Vec3d(floor.getX() + 0.5, floor.getY() + 1.0, floor.getZ() + 0.5);
        BlockHitResult bhr = new BlockHitResult(hitVec, Direction.UP, floor, false);

        boolean sneak = InteractionUtility.needSneak(mc.world.getBlockState(floor).getBlock()) && !mc.player.isSneaking();


        // Отправляем актуальную ротацию
        sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), currentYaw, currentPitch, mc.player.isOnGround(), mc.player.horizontalCollision));

        // Ставим блок
        mc.interactionManager.interactBlock(mc.player, prevItem == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND, bhr);
        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(prevItem == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND));


        // Рендер
        if (render.getValue()) {
            BlockAnimationUtility.renderBlock(pos, renderLineColor.getValue().getColorObject(), renderLineWidth.getValue(), renderFillColor.getValue().getColorObject(), animationMode.getValue(), renderMode.getValue());
        }
    }

    private List<BlockPos> getTargets() {
        List<BlockPos> positions = new ArrayList<>();
        int r = (int) Math.ceil(range.getValue());
        BlockPos playerPos = mc.player.getBlockPos();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);

                    if (mc.player.squaredDistanceTo(pos.toCenterPos()) > range.getValue() * range.getValue()) continue;

                    if (mc.world.getBlockState(pos).isReplaceable() && mc.world.getBlockState(pos.down()).isSolidBlock(mc.world, pos.down())) {
                        positions.add(pos);
                    }
                }
            }
        }
        positions.sort(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p.toCenterPos())));
        return positions;
    }

    private boolean hasRedstone() {
        if (mc.player.getOffHandStack().getItem() == Items.REDSTONE || mc.player.getMainHandStack().getItem() == Items.REDSTONE) return true;
        if (autoSwitch.getValue() == Switch.None) return false;

        SearchInvResult hotbar = InventoryUtility.findInHotBar(i -> i.getItem() == Items.REDSTONE);
        if (autoSwitch.getValue() == Switch.Normal || autoSwitch.getValue() == Switch.Silent) return hotbar.found();

        SearchInvResult inv = InventoryUtility.findInInventory(i -> i.getItem() == Items.REDSTONE);
        return inv.found();
    }

    private int prePlace() {
        if (mc.player.getOffHandStack().getItem() == Items.REDSTONE) return -2;
        if (mc.player.getMainHandStack().getItem() == Items.REDSTONE) return mc.player.getInventory().getSelectedSlot();

        int prevSlot = mc.player.getInventory().getSelectedSlot();

        SearchInvResult hotbarResult = InventoryUtility.findInHotBar(i -> i.getItem() == Items.REDSTONE);
        SearchInvResult invResult = InventoryUtility.findInInventory(i -> i.getItem() == Items.REDSTONE);

        switch (autoSwitch.getValue()) {
            case Inventory -> {
                if (invResult.found()) {
                    prevSlot = invResult.slot();
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, prevSlot, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player);
                    sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                }
            }
            case Normal, Silent -> {
                if (hotbarResult.found()) hotbarResult.switchTo();
            }
        }
        return prevSlot;
    }

    private void postPlace(int prevSlot) {
        if (prevSlot == -1 || prevSlot == -2) return;

        switch (autoSwitch.getValue()) {
            case Inventory -> {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, prevSlot, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player);
                sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
            }
            case Silent -> InventoryUtility.switchTo(prevSlot);
        }
    }
}