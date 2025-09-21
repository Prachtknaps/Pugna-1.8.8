package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.teams.Team;
import org.bukkit.*;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlayerUtils {
    private PlayerUtils() {}

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

    /* === Queries === */

    public static List<Player> getOnlineAlivePlayers() {
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null && player.isOnline() && !isSpectator(player)) {
                players.add(player);
            }
        }

        return players;
    }

    public static int getAlivePlayersCount() {
        int count = 0;
        for (Team team : getGameManager().getTeams()) {
            count += team.getMembers().size();
        }

        return count;
    }

    public static boolean areEnoughPlayersOnline() {
        GameState state = getGameManager().getState();
        if (state != GameState.GAME_RUNNING && state != GameState.GAME_PAUSED) {
            return true;
        }

        int alivePlayersCount = getAlivePlayersCount();
        int onlineAlivePlayersCount = getOnlineAlivePlayers().size();

        if (alivePlayersCount == 0) {
            return false;
        }

        int required = (alivePlayersCount / 2) + 1;
        return onlineAlivePlayersCount >= required;
    }

    public static boolean isSpectator(Player player) {
        if (player == null || !player.isOnline()) {
            return false;
        }

        GameState state = getGameManager().getState();
        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            return TeamUtils.getTeam(player) == null;
        }

        return false;
    }

    /* === Operations === */

    public static void setupPlayer(Player player) {
        if (player == null || !player.isOnline() || isSpectator(player)) {
            return;
        }

        GameState state = getGameManager().getState();

        if (state == GameState.LOBBY_WAITING || state == GameState.LOBBY_COUNTDOWN) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0f);
            player.setFireTicks(0);
            player.setFallDistance(0.0f);

            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents(new ItemStack[4]);

            ItemStack teamSelectionItem = new ItemStack(Material.BED);
            ItemMeta teamSelectionItemMeta = teamSelectionItem.getItemMeta();
            teamSelectionItemMeta.setDisplayName(getPugnaConfig().getTeamSelectionItemName());
            teamSelectionItem.setItemMeta(teamSelectionItemMeta);

            ItemStack leaveItem = new ItemStack(Material.MAGMA_CREAM);
            ItemMeta leaveItemMeta = leaveItem.getItemMeta();
            leaveItemMeta.setDisplayName(getPugnaConfig().getLeaveItemName());
            leaveItem.setItemMeta(leaveItemMeta);

            inventory.setItem(0, teamSelectionItem);
            inventory.setItem(8, leaveItem);

            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0.0f);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        } else if (state == GameState.GAME_COUNTDOWN) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0f);
            player.setFireTicks(0);
            player.setFallDistance(0.0f);

            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents(new ItemStack[4]);

            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0.0f);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        } else if (state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    public static void setupSpectator(Player spectator) {
        if (spectator == null || !spectator.isOnline() || !isSpectator(spectator)) {
            return;
        }

        GameState state = getGameManager().getState();

        if (state == GameState.GAME_COUNTDOWN || state == GameState.GAME_RUNNING || state == GameState.GAME_PAUSED) {
            spectator.setGameMode(GameMode.SURVIVAL);
            spectator.setAllowFlight(true);
            spectator.setFlying(true);

            spectator.setHealth(20.0);
            spectator.setFoodLevel(20);
            spectator.setSaturation(20.0f);
            spectator.setFireTicks(0);
            spectator.setFallDistance(0.0f);

            PlayerInventory inventory = spectator.getInventory();
            inventory.clear();
            inventory.setArmorContents(new ItemStack[4]);

            ItemStack navigationItem = new ItemStack(Material.COMPASS);
            ItemMeta navigationItemMeta = navigationItem.getItemMeta();
            navigationItemMeta.setDisplayName(getPugnaConfig().getNavigationItemName());
            navigationItem.setItemMeta(navigationItemMeta);
            inventory.setItem(0, navigationItem);
        }
    }

    public static void handleVisibility() {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (isSpectator(player)) {
                        other.hidePlayer(player);
                    } else {
                        other.showPlayer(player);
                    }
                }
            }
        }, 10L);
    }

    public static void showAllPlayers() {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.equals(player)) {
                        continue;
                    }

                    player.showPlayer(other);
                }
            }
        }, 10L);
    }

    public static void kickAllPlayers(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(message);
        }
    }

    public static void clearSpectators() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isSpectator(player)) {
                setupPlayer(player);
            }
        }

        showAllPlayers();
    }

    public static void simulatePlayerDeath(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Location location = player.getLocation();
        if (location == null) {
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR && item.getAmount() > 0) {
                world.dropItemNaturally(location, item.clone());
            }
        }

        for (ItemStack armor : inventory.getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR && armor.getAmount() > 0) {
                world.dropItemNaturally(location, armor.clone());
            }
        }

        int xpToDrop = Math.min(player.getTotalExperience(), player.getLevel() * 7);
        while (xpToDrop > 0) {
            int split = Math.min(xpToDrop, 32767);
            ExperienceOrb orb = world.spawn(location, ExperienceOrb.class);
            orb.setExperience(split);
            xpToDrop -= split;
        }

        inventory.clear();
        inventory.setArmorContents(new ItemStack[4]);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0.0f);
    }

    public static Player getClosestEnemy(Player player) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        World world = player.getWorld();
        if (world == null) {
            return null;
        }

        Player closest = null;
        double smallestDistance = Double.MAX_VALUE;

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player) || isSpectator(other) || !other.getWorld().getName().equals(world.getName()) || Objects.equals(TeamUtils.getTeam(other), TeamUtils.getTeam(player))) {
                continue;
            }

            double distance = player.getLocation().distanceSquared(other.getLocation());
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closest = other;
            }
        }

        return closest;
    }
}
