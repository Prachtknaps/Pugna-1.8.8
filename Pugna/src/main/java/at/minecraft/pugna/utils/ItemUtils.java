package at.minecraft.pugna.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Map;

public final class ItemUtils {
    private ItemUtils() {}

    /* === Operations === */

    public static boolean shouldBlock(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        } else if (isForbiddenItem(itemStack)) {
            return true;
        } else if (hasBannedEnchantments(itemStack)) {
            return true;
        } else if (isPotion(itemStack) && !isAllowedPotion(itemStack)) {
            return true;
        }

        return false;
    }

    private static boolean isForbiddenItem(ItemStack itemStack) {
        if (itemStack.getType() == Material.SADDLE) {
            return true;
        } else if (itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == 1) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasBannedEnchantments(ItemStack itemStack) {
        /* === Enchanted Books === */
        if (itemStack.getType() == Material.ENCHANTED_BOOK && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                for (Enchantment enchantment : ((EnchantmentStorageMeta) meta).getStoredEnchants().keySet()) {
                    if (isBannedEnchantment(enchantment)) {
                        return true;
                    }
                }
            }
        }

        /* === Other Items === */
        if (!itemStack.hasItemMeta()) {
            return false;
        }

        Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
        if (enchantments == null || enchantments.isEmpty()) {
            return false;
        }

        for (Enchantment enchantment : enchantments.keySet()) {
            if (isBannedEnchantment(enchantment)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBannedEnchantment(Enchantment enchantment) {
        return (
            enchantment == Enchantment.THORNS ||
            enchantment == Enchantment.DEPTH_STRIDER ||
            enchantment == Enchantment.FIRE_ASPECT ||
            enchantment == Enchantment.ARROW_FIRE ||
            enchantment == Enchantment.ARROW_INFINITE
        );
    }

    private static boolean isPotion(ItemStack itemStack) {
        return itemStack.getType() == Material.POTION;
    }

    private static boolean isAllowedPotion(ItemStack itemStack) {
        if (!isPotion(itemStack)) {
            return false;
        }

        try {
            Potion potion = Potion.fromItemStack(itemStack);
            PotionType potionType = potion.getType();
            boolean isSplash = potion.isSplash();
            int level = potion.getLevel();

            /* === Potion of Healing (Level I/II, no splash) === */
            if (potionType == PotionType.INSTANT_HEAL) {
                return !isSplash && (level == 1 || level == 2);
            }

            /* === Potion of Weakness === */
            if (potionType == PotionType.WEAKNESS) {
                return true;
            }

            return false;
        } catch (Exception exception) {
            return false;
        }
    }
}
