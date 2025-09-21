package at.minecraft.pugna.loot;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class LootManager {
    private LootManager() {}

    private static final int MIN_TOTAL_ITEMS = 16;
    private static final int MAX_TOTAL_ITEMS = 24;

    private static final List<ItemStack> BLOCK_ITEM_POOL = Arrays.asList(
            new ItemStack(Material.GRASS, 1),
            new ItemStack(Material.DIRT, 1),
            new ItemStack(Material.GRAVEL, 1)
    );

    private static final int MIN_RARE_ITEMS = 0;
    private static final int MAX_RARE_ITEMS = 1;
    private static final List<ItemStack> RARE_ITEM_POOL = Arrays.asList(
            new ItemStack(Material.GOLD_SWORD),
            new ItemStack(Material.WOOD_SWORD),
            new ItemStack(Material.WOOD_SPADE),
            new ItemStack(Material.WOOD_AXE),
            new ItemStack(Material.LEATHER_HELMET),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_BOOTS)
    );

    private static final int MIN_FOOD_ITEMS = 2;
    private static final int MAX_FOOD_ITEMS = 8;
    private static final List<ItemStack> FOOD_ITEM_POOL = Arrays.asList(
            new ItemStack(Material.APPLE, 1),
            new ItemStack(Material.BREAD, 1),
            new ItemStack(Material.PORK, 1),
            new ItemStack(Material.RAW_FISH, 1, (byte) 2),
            new ItemStack(Material.RAW_FISH, 1, (byte) 3),
            new ItemStack(Material.COOKIE, 1),
            new ItemStack(Material.MELON, 1),
            new ItemStack(Material.COOKED_BEEF, 1),
            new ItemStack(Material.COOKED_CHICKEN, 1),
            new ItemStack(Material.ROTTEN_FLESH, 1),
            new ItemStack(Material.CARROT_ITEM, 1),
            new ItemStack(Material.POTATO_ITEM, 1),
            new ItemStack(Material.BAKED_POTATO, 1)
    );

    private static final int MIN_COMMON_ITEMS = 0;
    private static final int MAX_COMMON_ITEMS = 6;
    private static final List<ItemStack> COMMON_ITEM_POOL = Arrays.asList(
            new ItemStack(Material.LONG_GRASS, 1, (byte) 1),
            new ItemStack(Material.YELLOW_FLOWER, 1),
            new ItemStack(Material.RED_ROSE, 1),
            new ItemStack(Material.BROWN_MUSHROOM, 1),
            new ItemStack(Material.RED_MUSHROOM, 1),
            new ItemStack(Material.WATER_LILY, 1),
            new ItemStack(Material.PAPER, 1),
            new ItemStack(Material.BONE, 1),
            new ItemStack(Material.STICK, 1),
            new ItemStack(Material.STRING, 1),
            new ItemStack(Material.FEATHER, 1),
            new ItemStack(Material.SEEDS, 1),
            new ItemStack(Material.LEATHER, 1),
            new ItemStack(Material.SUGAR_CANE, 1),
            new ItemStack(Material.INK_SACK, 1)
    );

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    /* === Operations === */

    public static void fillChest(Inventory inventory) {
        List<ItemStack> lootItems = generateLootItems();
        Collections.shuffle(lootItems, new Random(random.nextLong()));

        int size = inventory.getSize();
        List<Integer> emptySlots = new ArrayList<>(size);
        for (int slot = 0; slot < size; slot++) {
            ItemStack existing = inventory.getItem(slot);
            if (existing == null || existing.getType() == Material.AIR) {
                emptySlots.add(slot);
            }
        }

        Collections.shuffle(emptySlots, new Random(random.nextLong()));

        Iterator<Integer> slotIt = emptySlots.iterator();
        for (ItemStack item : lootItems) {
            if (!slotIt.hasNext()) {
                break;
            }
            ItemStack single = item.clone();
            single.setAmount(1);
            inventory.setItem(slotIt.next(), single);
        }
    }

    private static List<ItemStack> generateLootItems() {
        int remainingSlots = randomBetween(MIN_TOTAL_ITEMS, MAX_TOTAL_ITEMS);

        int rareCount = clamp(randomBetween(MIN_RARE_ITEMS, MAX_RARE_ITEMS), 0, remainingSlots);
        remainingSlots -= rareCount;

        int foodCount = clamp(randomBetween(MIN_FOOD_ITEMS, MAX_FOOD_ITEMS), 0, remainingSlots);
        remainingSlots -= foodCount;

        int commonCount = clamp(randomBetween(MIN_COMMON_ITEMS, MAX_COMMON_ITEMS), 0, remainingSlots);
        remainingSlots -= commonCount;

        int blockCount = remainingSlots;

        List<ItemStack> result = new ArrayList<>(rareCount + foodCount + commonCount + blockCount);

        addRandomItems(result, RARE_ITEM_POOL, rareCount);
        addRandomItems(result, FOOD_ITEM_POOL, foodCount);
        addRandomItems(result, COMMON_ITEM_POOL, commonCount);
        addRandomItems(result, BLOCK_ITEM_POOL, blockCount);

        return result;
    }

    private static void addRandomItems(List<ItemStack> target, List<ItemStack> pool, int amount) {
        for (int i = 0; i < amount; i++) {
            target.add(randomFrom(pool));
        }
    }

    /* === Helpers === */

    private static ItemStack randomFrom(List<ItemStack> pool) {
        return pool.get(random.nextInt(pool.size()));
    }

    private static int randomBetween(int min, int maxInclusive) {
        return random.nextInt(min, maxInclusive + 1);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
