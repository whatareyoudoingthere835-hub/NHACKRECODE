package thunder.hack.utility.player;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

/**
 * 1.21.11 replaced the item class hierarchy (ArmorItem/ElytraItem/SwordItem/PickaxeItem/...) with
 * data-driven items. This helper answers the questions the modules used to ask those classes,
 * using item tags and attribute modifiers.
 */
public final class ItemChecks {

    private static final Identifier ARMOR = Identifier.of("minecraft", "armor");
    private static final Identifier TOUGHNESS = Identifier.of("minecraft", "armor_toughness");

    private ItemChecks() {
    }

    /** LivingEntity#getArmorItems is gone in 1.21.11 - iterate the four armor slots instead */
    public static java.util.List<ItemStack> armorItems(LivingEntity entity) {
        return java.util.List.of(
                entity.getEquippedStack(EquipmentSlot.HEAD),
                entity.getEquippedStack(EquipmentSlot.CHEST),
                entity.getEquippedStack(EquipmentSlot.LEGS),
                entity.getEquippedStack(EquipmentSlot.FEET));
    }

    public static boolean isArmor(ItemStack stack) {
        return armorSlot(stack) != null;
    }

    public static EquipmentSlot armorSlot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.isIn(ItemTags.HEAD_ARMOR)) return EquipmentSlot.HEAD;
        if (stack.isIn(ItemTags.CHEST_ARMOR)) return EquipmentSlot.CHEST;
        if (stack.isIn(ItemTags.LEG_ARMOR)) return EquipmentSlot.LEGS;
        if (stack.isIn(ItemTags.FOOT_ARMOR)) return EquipmentSlot.FEET;
        return null;
    }

    public static boolean isElytra(ItemStack stack) {
        return stack != null && stack.isOf(Items.ELYTRA);
    }

    /** ElytraItem.isUsable equivalent: not broken. */
    public static boolean isElytraUsable(ItemStack stack) {
        return isElytra(stack) && (!stack.isDamageable() || stack.getDamage() < stack.getMaxDamage());
    }

    public static boolean isSword(ItemStack stack) {
        return stack != null && stack.isIn(ItemTags.SWORDS);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack != null && stack.isIn(ItemTags.AXES);
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack != null && stack.isIn(ItemTags.PICKAXES);
    }

    public static boolean isShovel(ItemStack stack) {
        return stack != null && stack.isIn(ItemTags.SHOVELS);
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        return isSword(stack) || isAxe(stack);
    }

    /** armor + toughness points the stack provides while worn (0 for non-armor) */
    public static double protectionPoints(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        double[] out = new double[2];
        stack.applyAttributeModifiers(AttributeModifierSlot.ARMOR, (attribute, modifier) -> {
            if (attribute == null || modifier == null) return;
            if (attribute.matches(ARMOR)) out[0] += modifier.value();
            else if (attribute.matches(TOUGHNESS)) out[1] += modifier.value();
        });
        return out[0] + out[1];
    }

    /** rough attack damage of the stack (sword/axe "base damage" replacement), -1 when unknown */
    public static double attackDamage(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return -1;
        double[] out = new double[]{-1};
        stack.applyAttributeModifiers(AttributeModifierSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute == null || modifier == null) return;
            if (attribute.getValue() != null && "attack_damage".equals(attribute.getValue().getPath())) {
                if (out[0] < 0) out[0] = 0;
                out[0] += modifier.value();
            }
        });
        return out[0];
    }
}
