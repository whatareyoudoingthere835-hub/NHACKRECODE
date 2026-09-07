package ru.expensive.implement.features.modules.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.implement.events.render.DrawEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Плашки над сущностями: имя + хп, ряд брони у игроков,
 * эффекты под ногами, названия предметов/стрел.
 */
public class EntityESPModule extends Module {
    private static final float PAD = 2.0F;
    private static final float NAME_BOX_H = 11.0F;
    private static final float ARMOR_SIZE = 10.0F;
    private static final float ARMOR_SPACING = 2.0F;

    private final MultiSelectSetting trackedSetting = new MultiSelectSetting("Tracked", "Entities to track")
            .value("Игроки", "Животные", "Мобы", "Предметы");

    private final List<Tracker> trackers = new ArrayList<>();

    public EntityESPModule() {
        super("EntityESP", "Entity ESP", ModuleCategory.RENDER);
        trackedSetting.setSelected(new ArrayList<>(List.of("Игроки")));
        setup(trackedSetting);
    }

    public List<Tracker> getTrackers() {
        return trackers;
    }

    @EventHandler
    public void onDraw(DrawEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        float tickProgress = event.getPartialTicks();
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) {
                continue;
            }
            String key = categoryOf(entity);
            if (key == null || !trackedSetting.isSelected(key)) {
                continue;
            }

            int bg = (entity instanceof PlayerEntity
                    && FriendRepository.isFriend(entity.getName().getString()))
                    ? 0x99006400 : 0x50000000;

            var interpolated = entity.getLerpedPos(tickProgress);
            Vector2f head = ProjectionUtil.project(
                    interpolated.x, interpolated.y + entity.getHeight() + 0.25, interpolated.z);
            if (!ProjectionUtil.isValidProjection(head)) {
                continue;
            }

            if (entity instanceof ItemEntity || entity instanceof ArrowEntity) {
                drawItemLabel(event, entity, head, bg);
            } else if (entity instanceof LivingEntity living) {
                drawNameTag(event, living, head, bg);
                Vector2f feet = ProjectionUtil.project(
                        interpolated.x, interpolated.y - 0.25, interpolated.z);
                if (ProjectionUtil.isValidProjection(feet)) {
                    drawEffects(event, living, feet, bg);
                }
            }
        }
    }

    private static String categoryOf(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return "Игроки";
        }
        if (entity instanceof HostileEntity) {
            return "Мобы";
        }
        if (entity instanceof AnimalEntity || entity instanceof ShulkerEntity || entity instanceof VillagerEntity) {
            return "Животные";
        }
        if (entity instanceof ItemEntity || entity instanceof ArrowEntity) {
            return "Предметы";
        }
        return null;
    }

    private void drawNameTag(DrawEvent event, LivingEntity entity, Vector2f head, int bg) {
        FontRenderer font = Fonts.getSize(13);
        String name = entity.getName().getString();

        String displayName = entity.getDisplayName() != null ? entity.getDisplayName().getString() : name;
        String prefix = "";
        int nameIndex = displayName.indexOf(name);
        if (nameIndex > 0) {
            prefix = displayName.substring(0, nameIndex);
        }

        int hp = (int) (entity.getHealth() + entity.getAbsorptionAmount());
        String hpText = " " + hp;

        float prefixWidth = prefix.isEmpty() ? 0 : font.getStringWidth(prefix) + 3;
        float nameWidth = font.getStringWidth(name);
        float hpWidth = font.getStringWidth(hpText);
        float boxW = prefixWidth + nameWidth + hpWidth + PAD * 2 + 4;
        float x = head.x() - boxW / 2.0F;
        float y = head.y();

        MatrixStack stack = new MatrixStack();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        rectangle.render(ShapeProperties.create(matrix, x, y, boxW, NAME_BOX_H)
                .round(3.0F)
                .thickness(1.5F)
                .softness(1.0F)
                .outlineColor(0xFF060712)
                .color(bg)
                .build());

        float textX = x + PAD + 2;
        float textY = y + 1.5F;
        if (!prefix.isEmpty()) {
            font.drawString(stack, prefix, textX, textY, 0xFF55FF55);
            textX += prefixWidth;
        }
        font.drawString(stack, name, textX, textY, 0xFFFFFFFF);
        font.drawString(stack, hpText, textX + nameWidth, textY, 0xFFFF5555);

        drawArmor(event, entity, x, y, boxW);
    }

    private void drawArmor(DrawEvent event, LivingEntity entity, float boxX, float boxY, float boxW) {
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }
        List<ItemStack> gear = new ArrayList<>();
        if (!player.getMainHandStack().isEmpty()) gear.add(player.getMainHandStack());
        if (!player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) gear.add(player.getEquippedStack(EquipmentSlot.HEAD));
        if (!player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) gear.add(player.getEquippedStack(EquipmentSlot.CHEST));
        if (!player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) gear.add(player.getEquippedStack(EquipmentSlot.LEGS));
        if (!player.getEquippedStack(EquipmentSlot.FEET).isEmpty()) gear.add(player.getEquippedStack(EquipmentSlot.FEET));
        if (!player.getOffHandStack().isEmpty()) gear.add(player.getOffHandStack());
        if (gear.isEmpty()) {
            return;
        }

        float rowW = gear.size() * ARMOR_SIZE + (gear.size() - 1) * ARMOR_SPACING;
        float startX = boxX + (boxW - rowW) / 2.0F;
        float rowY = boxY - ARMOR_SIZE - 3.0F;

        MatrixStack stack = new MatrixStack();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        DrawContext context = event.getDrawContext();
        float scale = ARMOR_SIZE / 16.0F;

        float itemX = startX;
        for (ItemStack gearStack : gear) {
            rectangle.render(ShapeProperties.create(matrix, itemX, rowY, ARMOR_SIZE, ARMOR_SIZE)
                    .round(2.0F)
                    .color(0x80000000)
                    .build());
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(itemX, rowY);
            context.getMatrices().scale(scale, scale);
            context.drawItem(gearStack, 0, 0);
            context.getMatrices().popMatrix();
            itemX += ARMOR_SIZE + ARMOR_SPACING;
        }
    }

    private void drawEffects(DrawEvent event, LivingEntity living, Vector2f feet, int bg) {
        List<StatusEffectInstance> effects = resolveEffects(living);
        if (effects.isEmpty()) {
            return;
        }

        FontRenderer font = Fonts.getSize(12);
        List<String> lines = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        float maxWidth = 0;
        for (StatusEffectInstance effect : effects) {
            String line = effectLine(effect);
            lines.add(line);
            colors.add(0xFF000000 | effect.getEffectType().value().getColor());
            maxWidth = Math.max(maxWidth, font.getStringWidth(line));
        }

        float lineH = 10.0F;
        float boxW = maxWidth + PAD * 2;
        float x = feet.x() - boxW / 2.0F;
        float y = feet.y() + PAD;

        MatrixStack stack = new MatrixStack();
        rectangle.render(ShapeProperties.create(stack.peek().getPositionMatrix(),
                        x, y, boxW, lines.size() * lineH + PAD)
                .round(3.0F)
                .thickness(1.5F)
                .softness(1.0F)
                .outlineColor(0xFF060712)
                .color(bg)
                .build());

        float lineY = y + 1.0F;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            font.drawString(stack, line, feet.x() - font.getStringWidth(line) / 2.0F, lineY, colors.get(i));
            lineY += lineH;
        }
    }

    private static String effectLine(StatusEffectInstance effect) {
        String name = net.minecraft.text.Text.translatable(
                effect.getEffectType().value().getTranslationKey()).getString();
        String level = toRoman(effect.getAmplifier() + 1);
        if (effect.getDuration() > 1000000) {
            return name + " " + level + " ∞";
        }
        int seconds = effect.getDuration() / 20;
        return name + " " + level + " - " + (seconds / 60) + ":" + String.format("%02d", seconds % 60);
    }

    private List<StatusEffectInstance> resolveEffects(LivingEntity living) {
        List<StatusEffectInstance> effects = new ArrayList<>();
        List<Tracker> matching = new ArrayList<>();
        for (int i = trackers.size() - 1; i >= 0; i--) {
            Tracker entry = trackers.get(i);
            if (entry.getEntityId() == living.getId()) {
                if (living.age < entry.getAge()) {
                    trackers.remove(i);
                } else {
                    matching.add(entry);
                }
            }
        }
        if (!matching.isEmpty()) {
            for (Tracker tracker : matching) {
                for (StatusEffectInstance instance : tracker.getEffects()) {
                    int remaining = instance.getDuration() - Math.max(0, living.age - tracker.getAge());
                    if (remaining <= 0) {
                        continue;
                    }
                    StatusEffectInstance current = instance.getDuration() > 1000000
                            ? instance
                            : new StatusEffectInstance(instance.getEffectType(), remaining, instance.getAmplifier());
                    StatusEffectInstance existing = null;
                    for (StatusEffectInstance added : effects) {
                        if (added.getEffectType().equals(instance.getEffectType())) {
                            existing = added;
                            break;
                        }
                    }
                    if (existing == null) {
                        effects.add(current);
                    } else if (current.getAmplifier() > existing.getAmplifier()
                            || (current.getAmplifier() == existing.getAmplifier()
                            && current.getDuration() > existing.getDuration())) {
                        effects.remove(existing);
                        effects.add(current);
                    }
                }
            }
            if (effects.isEmpty()) {
                effects.addAll(living.getStatusEffects());
            }
        } else {
            effects.addAll(living.getStatusEffects());
        }
        return effects;
    }

    private void drawItemLabel(DrawEvent event, Entity entity, Vector2f head, int bg) {
        FontRenderer font = Fonts.getSize(13);
        String text = entity.getName().getString();
        if (entity instanceof ItemEntity item && item.getStack().getCount() > 1) {
            text += " x" + item.getStack().getCount();
        }

        float textWidth = font.getStringWidth(text);
        float boxW = textWidth + PAD * 2 + 4;
        float x = head.x() - boxW / 2.0F;
        float y = head.y();

        MatrixStack stack = new MatrixStack();
        rectangle.render(ShapeProperties.create(stack.peek().getPositionMatrix(), x, y, boxW, NAME_BOX_H)
                .round(3.0F)
                .thickness(1.5F)
                .softness(1.0F)
                .outlineColor(0xFF060712)
                .color(bg)
                .build());
        font.drawString(stack, text, x + PAD + 2, y + 1.5F, 0xFFFFFFFF);
    }

    private static String toRoman(int number) {
        if (number <= 0) {
            return "";
        }
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[Math.min(number / 1000, 3)]
                + hundreds[(number % 1000) / 100]
                + tens[(number % 100) / 10]
                + ones[number % 10];
    }

    public static final class Tracker {
        private final List<StatusEffectInstance> effects;
        private final int entityId;
        private final int age;

        public Tracker(List<StatusEffectInstance> effects, int id, int age) {
            this.effects = effects;
            this.entityId = id;
            this.age = age;
        }

        public List<StatusEffectInstance> getEffects() {
            return effects;
        }

        public int getEntityId() {
            return entityId;
        }

        public int getAge() {
            return age;
        }
    }
}
