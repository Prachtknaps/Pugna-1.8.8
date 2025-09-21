package at.minecraft.pugna.gui;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TeamSelectionGUI {
    private TeamSelectionGUI() {}

    private static Pugna plugin = null;
    private static PugnaConfig pugnaConfig = null;
    private static GameManager gameManager = null;

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

    private static GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = getPlugin().getGameManager();
        }

        return gameManager;
    }

    /* === Operations === */

    public static void openFor(Player player) {
        int size = ((getGameManager().getTeams().size() + 8) / 9) * 9;
        if (size <= 0) {
            size = 9;
        }

        Inventory inventory = Bukkit.createInventory(player, size, ChatColor.stripColor(getPugnaConfig().getTeamSelectionItemName()));

        for (int i = 0; i < getGameManager().getTeams().size() && i < size; i++) {
            Team team = getGameManager().getTeams().get(i);
            inventory.setItem(i, createTeamItem(player, team));
        }

        player.openInventory(inventory);
    }

    private static ItemStack createTeamItem(Player player, Team team) {
        short color;
        if (team.isEmpty()) {
            color = 0;
        } else if (team.isFull()) {
            color = 14;
        } else {
            color = 1;
        }

        ItemStack wool = new ItemStack(Material.WOOL, 1, color);
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName("§e" + team.getName());

        List<String> lore = new ArrayList<>();
        List<UUID> members = team.getMembers();
        int capacity = team.getCapacity();

        for (int i = 0; i < capacity; i++) {
            if (i < members.size()) {
                UUID uuid = members.get(i);
                Player member = Bukkit.getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    lore.add("§7- §f" + member.getName());
                } else {
                    lore.add("§7-");
                }
            } else {
                lore.add("§7-");
            }
        }

        woolMeta.setLore(lore);

        if (TeamUtils.getTeam(player) == team) {
            woolMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            wool.setItemMeta(woolMeta);
            wool.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            return wool;
        }

        wool.setItemMeta(woolMeta);
        return wool;
    }
}
