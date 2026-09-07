package ru.expensive.implement.features.modules.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.render.DrawEvent;
import ru.expensive.implement.events.render.WorldRenderEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Показывает сундуки в городе варденов (FunTime): свободные — боксом,
 * занятые — плашкой с таймером возрождения (время читается с неймплейтов
 * стоек для брони рядом с сундуком).
 */
public class WardenESPModule extends Module {
    // Границы города варденов на FunTime (как в оригинале).
    private static final int MIN_X = -2070, MAX_X = -1921;
    private static final int MIN_Z = -2076, MAX_Z = -1929;
    private static final int MIN_Y = -60, MAX_Y = -35;
    private static final long SCAN_INTERVAL_MS = 1000L;
    private static final double STAND_SCAN_RADIUS = 256.0;

    private static final Pattern TIMER_PATTERN = Pattern.compile("(\\d{2}):(\\d{2})");

    private final Map<BlockPos, Long> timers = new HashMap<>();
    private final List<BlockPos> chests = new ArrayList<>();
    private long lastScan;

    public WardenESPModule() {
        super("WardenESP", "Warden ESP", ModuleCategory.RENDER);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        if (mc.world.getRegistryKey() != World.OVERWORLD) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastScan >= SCAN_INTERVAL_MS) {
            lastScan = now;
            scanChests();
            scanTimers(now);
        }
        timers.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        if (mc.world.getRegistryKey() != World.OVERWORLD) {
            return;
        }
        long now = System.currentTimeMillis();
        for (BlockPos chest : chests) {
            Long endsAt = timers.get(chest);
            if (endsAt != null && endsAt > now) {
                drawTimerLabel(drawEvent, chest, endsAt - now);
            } else {
                drawFreeBox(drawEvent, chest);
            }
        }
    }

    private void scanChests() {
        chests.clear();
        for (BlockEntity blockEntity : mc.world.getBlockEntities()) {
            if (blockEntity.isRemoved()) {
                continue;
            }
            if (!(blockEntity instanceof ChestBlockEntity)) {
                continue;
            }
            BlockPos pos = blockEntity.getPos();
            if (pos.getY() >= MIN_Y && pos.getY() <= MAX_Y
                    && pos.getX() >= MIN_X && pos.getX() <= MAX_X
                    && pos.getZ() >= MIN_Z && pos.getZ() <= MAX_Z) {
                chests.add(pos.toImmutable());
            }
        }
    }

    private void scanTimers(long now) {
        for (ArmorStandEntity stand : mc.world.getEntitiesByClass(ArmorStandEntity.class,
                mc.player.getBoundingBox().expand(STAND_SCAN_RADIUS), e -> true)) {
            Matcher matcher = TIMER_PATTERN.matcher(stand.getName().getString());
            if (!matcher.find()) {
                continue;
            }
            long remaining = (Integer.parseInt(matcher.group(1)) * 60L
                    + Integer.parseInt(matcher.group(2))) * 1000L;
            BlockPos nearest = findChestByColumn(chests, stand.getBlockPos());
            if (nearest == null) {
                continue;
            }
            Long current = timers.get(nearest);
            long currentRemaining = current == null ? -1L : Math.max(0L, current - now);
            if (current == null || Math.abs(remaining - currentRemaining) > 5000L) {
                timers.put(nearest, now + remaining);
            }
        }
    }

    private static BlockPos findChestByColumn(List<BlockPos> chests, BlockPos standPos) {
        for (BlockPos chest : chests) {
            if (standPos.getX() == chest.getX() && standPos.getZ() == chest.getZ()) {
                return chest;
            }
        }
        return null;
    }

    private void drawTimerLabel(DrawEvent drawEvent, BlockPos chest, long remainingMs) {
        Vector2f projection = ProjectionUtil.project(chest.getX() + 0.5, chest.getY() + 1.0, chest.getZ() + 0.5);
        if (!ProjectionUtil.isValidProjection(projection)) {
            return;
        }

        int totalSec = (int) (remainingMs / 1000L);
        String text = String.format(Locale.US, "%02d:%02d", totalSec / 60, totalSec % 60);
        float textWidth = Fonts.getSize(13).getStringWidth(text);
        float width = textWidth + 18.0F;
        float x = projection.x() - width / 2.0F;
        float y = projection.y() - 6.0F;

        MatrixStack stack = new MatrixStack();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        rectangle.render(ShapeProperties.create(matrix, x, y, width, 12.0F)
                .round(3.0F)
                .thickness(1.5F)
                .softness(1.0F)
                .outlineColor(0xFF060712)
                .color(0xB2060712)
                .build());

        DrawContext context = drawEvent.getDrawContext();
        context.drawItem(new ItemStack(Items.CHEST), (int) (x + 1.0F), (int) (y - 2.0F));
        Fonts.getSize(13).drawString(stack, text, x + 15.0F, y + 2.5F, -1);
    }

    private void drawFreeBox(DrawEvent drawEvent, BlockPos chest) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;

        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dz = 0; dz <= 1; dz++) {
                    Vector2f projection = ProjectionUtil.project(
                            chest.getX() + dx, chest.getY() + dy, chest.getZ() + dz);
                    if (!ProjectionUtil.isValidProjection(projection)) {
                        return;
                    }
                    minX = Math.min(minX, projection.x());
                    minY = Math.min(minY, projection.y());
                    maxX = Math.max(maxX, projection.x());
                    maxY = Math.max(maxY, projection.y());
                }
            }
        }

        MatrixStack stack = new MatrixStack();
        rectangle.render(ShapeProperties.create(stack.peek().getPositionMatrix(),
                        minX, minY, maxX - minX, maxY - minY)
                .thickness(1.5F)
                .outlineColor(0xFFFF6464)
                .color(0x00000000)
                .build());
    }
}
