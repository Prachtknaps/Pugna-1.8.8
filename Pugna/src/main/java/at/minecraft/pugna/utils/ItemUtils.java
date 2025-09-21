package at.minecraft.pugna.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public final class ItemUtils {
    private ItemUtils() {
    }

    /* === Queries === */

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

    /* === Helpers === */

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
        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;

                if (storageMeta.hasStoredEnchant(Enchantment.FIRE_ASPECT)) {
                    return true;
                }
                if (storageMeta.hasStoredEnchant(Enchantment.THORNS)) {
                    return true;
                }
                if (storageMeta.hasStoredEnchant(Enchantment.DEPTH_STRIDER)) {
                    return true;
                }
                if (storageMeta.hasStoredEnchant(Enchantment.ARROW_FIRE)) {
                    return true;
                }
                if (storageMeta.hasStoredEnchant(Enchantment.ARROW_INFINITE)) {
                    return true;
                }
            }

            if (meta != null) {
                if (meta.hasEnchant(Enchantment.FIRE_ASPECT)) {
                    return true;
                }
                if (meta.hasEnchant(Enchantment.THORNS)) {
                    return true;
                }
                if (meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                    return true;
                }
                if (meta.hasEnchant(Enchantment.ARROW_FIRE)) {
                    return true;
                }
                if (meta.hasEnchant(Enchantment.ARROW_INFINITE)) {
                    return true;
                }
            }

            if (itemStack.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
                return true;
            }
            if (itemStack.getEnchantmentLevel(Enchantment.THORNS) > 0) {
                return true;
            }
            if (itemStack.getEnchantmentLevel(Enchantment.DEPTH_STRIDER) > 0) {
                return true;
            }
            if (itemStack.getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0) {
                return true;
            }
            if (itemStack.getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0) {
                return true;
            }
        }

        /* === Other Items === */
        if (itemStack.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
            return true;
        }
        if (itemStack.getEnchantmentLevel(Enchantment.THORNS) > 0) {
            return true;
        }
        if (itemStack.getEnchantmentLevel(Enchantment.DEPTH_STRIDER) > 0) {
            return true;
        }
        if (itemStack.getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0) {
            return true;
        }
        if (itemStack.getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0) {
            return true;
        }

        return false;
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

            /* === Water Bottles === */
            if (potionType == PotionType.WATER) {
                return true;
            }

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
