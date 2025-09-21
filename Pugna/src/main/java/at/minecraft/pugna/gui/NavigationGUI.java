package at.minecraft.pugna.gui;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public final class NavigationGUI {
    private NavigationGUI() {}

    private static Pugna plugin = null;
    private static PugnaConfig pugnaConfig = null;

    /* === Getters === */

    private static Pugna getPlugin() {
        if (plugin == null) {
            plugin = Pugna.getInstance();
        }

        return plugin;
    }

    private static PugnaConfig getPugnaConfig() {
        if (pugnaConfig == null) {
            pugnaConfig = getPlugin().getPugnaConfig();
        }

        return pugnaConfig;
    }

    /* === Operations === */

    public static void openFor(Player player) {
        List<Player> players = PlayerUtils.getOnlineAlivePlayers();

        int size = Math.max(9, ((players.size() + 8) / 9) * 9);
        size = Math.min(size, 72);

        Inventory inventory = Bukkit.createInventory(player, size, ChatColor.stripColor(getPugnaConfig().getNavigationItemName()));

        for (int i = 0; i < players.size() && i < size; i++) {
            inventory.setItem(i, createPlayerHead(players.get(i)));
        }

        player.openInventory(inventory);
    }

    private static ItemStack createPlayerHead(Player target) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(target.getName());
        meta.setDisplayName("§e" + target.getName() + "§r");
        head.setItemMeta(meta);
        return head;
    }
}
