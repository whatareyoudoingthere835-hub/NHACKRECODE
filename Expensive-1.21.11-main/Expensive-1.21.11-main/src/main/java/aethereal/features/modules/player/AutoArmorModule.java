package aethereal.features.modules.player;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;

@Aliases(aliases = {"Auto Armor", "Auto Equip", "Equip Armor", "Armor Manager", "Armor Swap"})
public class AutoArmorModule extends Module {
    public final Mc mc;
    public final Stopwatch swapStopwatch;

    public AutoArmorModule() {
        super(ModuleTab.PLAYER, "Auto Armor");
        this.mc = Mc.INSTANCE;
        this.swapStopwatch = new Stopwatch();
        addSettings(new Setting[0]);
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                InventoryItemFinder class123VarSearcher= Expensive.INSTANCE.inventoryService().searcher();
                ArrayList<Runnable> arrayList= new ArrayList<>();
                if (this.mc.getCurrentScreen() == null || (this.mc.getCurrentScreen() instanceof InventoryScreen)) {
                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                            ItemStack armorStack= EquipmentUtil.armorStack(player, equipmentSlot.getEntitySlotId());
                            Optional<SlotSearchResult2> optionalMax= class123VarSearcher.findAllItems(itemStack -> {
                                EquippableComponent equippable= itemStack.get(DataComponentTypes.EQUIPPABLE);
                                return equippable != null && equippable.slot() == equipmentSlot && !hasBindingCurse(itemStack);
                            }, InventoryScope.ALL).stream().max(Comparator.comparingDouble(class329Var -> {
                                return getArmorScore(class329Var.stack());
                            }));
                            if (optionalMax.isPresent()) {
                                SlotSearchResult2 class329Var2= optionalMax.get();
                                PlayerInventoryUtils class103Var= PlayerInventoryUtils.INSTANCE;
                                if (class329Var2.found() && isBetterArmor(class329Var2.stack(), armorStack)) {
                                    InventorySlotRef class246VarSlotReference= class329Var2.slotReference();
                                    int iSlot= class246VarSlotReference.slot();
                                    int armorSlotInventoryIndex= class103Var.getArmorSlotInventoryIndex(equipmentSlot);
                                    if (class246VarSlotReference.scope() == InventoryScope.INVENTORY) {
                                        arrayList.add(() -> {
                                            class103Var.swapTo(iSlot, armorSlotInventoryIndex);
                                        });
                                    } else if (class246VarSlotReference.scope() == InventoryScope.HOTBAR) {
                                        arrayList.add(() -> {
                                            class103Var.windowClick(SlotActionType.SWAP, armorSlotInventoryIndex, iSlot, true);
                                        });
                                    }
                                    this.swapStopwatch.reset();
                                }
                            }
                        }
                    }
                    if (arrayList.isEmpty() || !GrimDelayHandler.script.isFinished()) {
                        return;
                    }
                    SwapUtil.swapAction(() -> {
                        arrayList.forEach((v0) -> {
                            v0.run();
                        });
                        PlayerActionUtil.INSTANCE.updateSlots(false);
                    });
                }
            }
        });
    }

    public float getArmorScore(ItemStack itemStack) {
        Registry orThrow= this.mc.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        EquippableComponent equippable= itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) {
            return 0.0f;
        }
        AttributeModifiersComponent modifiers= itemStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        double armor= modifiers.applyOperations(EntityAttributes.ARMOR, 0.0, equippable.slot());
        double toughness= modifiers.applyOperations(EntityAttributes.ARMOR_TOUGHNESS, 0.0, equippable.slot());
        return (float) (armor + toughness) + EnchantmentHelper.getLevel((RegistryEntry) orThrow.getEntry(Enchantments.PROTECTION.getValue()).orElseThrow(), itemStack) + (EnchantmentHelper.getLevel((RegistryEntry) orThrow.getEntry(Enchantments.UNBREAKING.getValue()).orElseThrow(), itemStack) * 0.1f) + (EnchantmentHelper.getLevel((RegistryEntry) orThrow.getEntry(Enchantments.MENDING.getValue()).orElseThrow(), itemStack) * 0.2f);
    }

    public boolean hasBindingCurse(ItemStack itemStack) {
        Registry orThrow= this.mc.getPlayer().getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry entry= orThrow.getEntry((Enchantment) orThrow.get(Enchantments.BINDING_CURSE));
        return entry != null && EnchantmentHelper.getLevel(entry, itemStack) > 0;
    }

    public boolean isBetterArmor(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack2.isEmpty()) {
            return true;
        }
        EquippableComponent candidate= itemStack.get(DataComponentTypes.EQUIPPABLE);
        EquippableComponent equipped= itemStack2.get(DataComponentTypes.EQUIPPABLE);
        if (candidate == null || equipped == null || candidate.slot() != equipped.slot()) {
            return false;
        }
        return getArmorScore(itemStack) > getArmorScore(itemStack2);
    }
}
