package at.minecraft.pugna.game;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.ChatConfig;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.teams.Team;
import at.minecraft.pugna.utils.*;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameTimer extends BukkitRunnable {
    private final WorldManager worldManager;
    private final GameManager gameManager;
    private int seconds;

    private final int netherStartSeconds;
    private final int borderShrinkStartSeconds;
    private final int borderShrinkEndSeconds;
    private final int netherEndSeconds;
    private final int enemyRevealSeconds;
    private final int gameEndSeconds;

    private final int borderShrinkWarnStartSeconds;
    private final int netherEndWarnStartSeconds;
    private final int enemyRevealWarnStartSeconds;
    private final int gameEndWarnStartSeconds;

    public GameTimer(WorldManager worldManager, GameManager gameManager) {
        this.worldManager = worldManager;
        this.gameManager = gameManager;
        this.seconds = 0;

        this.netherStartSeconds = GameConfig.getNetherStartSeconds();
        this.borderShrinkStartSeconds = GameConfig.getBorderShrinkStartSeconds();
        this.borderShrinkEndSeconds = GameConfig.getBorderShrinkEndSeconds();
        this.netherEndSeconds = GameConfig.getNetherEndSeconds();
        this.enemyRevealSeconds = GameConfig.getEnemyRevealSeconds();
        this.gameEndSeconds = GameConfig.getGameEndSeconds();

        this.borderShrinkWarnStartSeconds = GameConfig.getBorderShrinkWarnStartSeconds();
        this.netherEndWarnStartSeconds = GameConfig.getNetherEndWarnStartSeconds();
        this.enemyRevealWarnStartSeconds = GameConfig.getEnemyRevealWarnStartSeconds();
        this.gameEndWarnStartSeconds = GameConfig.getGameEndWarnStartSeconds();
    }

    /* === Operations === */

    public void start() {
        runTaskTimer(Pugna.getInstance(), 0L, 20L);
    }

    @Override
    public void run() {
        if (seconds % 10 == 0) {
            handleForbiddenItemRemove();
        }
        else if (seconds == netherStartSeconds) {
            handleNetherStart();
        } else if (seconds >= borderShrinkWarnStartSeconds && seconds < borderShrinkStartSeconds) {
            handleBorderShrinkWarning(seconds);
        } else if (seconds == borderShrinkStartSeconds) {
            handleBorderShrinkStart();
        } else if (seconds == borderShrinkEndSeconds) {
            handleBorderShrinkEnd();
        } else if (seconds >= netherEndWarnStartSeconds && seconds < netherEndSeconds) {
            handleNetherEndWarning(seconds);
        } else if (seconds == netherEndSeconds) {
            handleNetherEnd();
        } else if (seconds >= enemyRevealWarnStartSeconds && seconds < enemyRevealSeconds) {
            handleEnemyRevealWarning(seconds);
        } else if (seconds == enemyRevealSeconds) {
            handleEnemyReveal();
        } else if (seconds >= gameEndWarnStartSeconds && seconds < gameEndSeconds) {
            handleGameEndWarning(seconds);
        } else if (seconds == gameEndSeconds) {
            handleGameEnd();
            cancel();
        }

        if (GameConfig.isLongGame()) {
            if (PlayerUtils.areEnoughPlayersOnline()) {
                seconds++;
            }
        } else {
            seconds++;
        }
    }

    private void handleForbiddenItemRemove() {
        for (Player player : PlayerUtils.getAllOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();
            int removed = 0;

            ItemStack[] contents = inventory.getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack itemStack = contents[i];
                if (itemStack != null && itemStack.getType() != Material.AIR && ItemUtils.shouldBlock(itemStack)) {
                    removed += itemStack.getAmount();
                    contents[i] = null;
                }
            }
            inventory.setContents(contents);

            ItemStack[] armor = inventory.getArmorContents();
            for (int i = 0; i < armor.length; i++) {
                ItemStack itemStack = armor[i];
                if (itemStack != null && itemStack.getType() != Material.AIR && ItemUtils.shouldBlock(itemStack)) {
                    removed += itemStack.getAmount();
                    armor[i] = null;
                }
            }
            inventory.setArmorContents(armor);

            ItemStack cursor = player.getItemOnCursor();
            if (cursor != null && cursor.getType() != Material.AIR && ItemUtils.shouldBlock(cursor)) {
                removed += cursor.getAmount();
                player.setItemOnCursor(null);
            }

            if (removed > 0) {
                String message = ChatConfig.getChatMessage(Message.FORBIDDEN_ITEMS_REMOVED).count(removed).toString();
                player.sendMessage(message);
                player.updateInventory();
            }
        }
    }

    private void handleNetherStart() {
        gameManager.setAllowNether(true);
        String message = ChatConfig.getMessage(Message.NETHER_START);
        ChatUtils.broadcast(message);
        SoundUtils.broadcast(Sound.NOTE_PLING, 1.0f, 2.0f);
    }

    private void handleBorderShrinkWarning(int seconds) {
        int remaining = borderShrinkStartSeconds - seconds;
        if (CountdownUtils.shouldAnnounce(remaining)) {
            String time = CountdownUtils.getTime(remaining);
            String unit = CountdownUtils.getUnit(remaining);
            String message = ChatConfig.getChatMessage(Message.BORDER_SHRINK_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
        }
    }

    private void handleBorderShrinkStart() {
        Location pugnaWorldSpawn = worldManager.getPugnaWorldSpawn();
        if (pugnaWorldSpawn == null) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "GameTimer.handleBorderShrinkStart: Pugna world spawn is null.");
            return;
        }

        World pugnaWorld = pugnaWorldSpawn.getWorld();
        if (pugnaWorld == null) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "GameTimer.handleBorderShrinkStart: Pugna world is null.");
            return;
        }

        Location pugnaNetherWorldSpawn = worldManager.getPugnaNetherWorldSpawn();
        if (pugnaNetherWorldSpawn == null) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "GameTimer.handleBorderShrinkStart: Pugna nether world spawn is null.");
            return;
        }

        World pugnaNetherWorld = pugnaNetherWorldSpawn.getWorld();
        if (pugnaNetherWorld == null) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "GameTimer.handleBorderShrinkStart: Pugna nether world is null.");
            return;
        }

        WorldBorder pugnaWorldBorder = pugnaWorld.getWorldBorder();
        if (pugnaWorldBorder == null) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "GameTimer.handleBorderShrinkStart: Pugna world border is null.");
            return;
        }

        WorldBorder pugnaNetherWorldBorder = pugnaNetherWorld.getWorldBorder();
        if (pugnaNetherWorldBorder == null) {
            Bukkit.getLogger().warning(ChatConfig.getRawPrefix() + "GameTimer.handleBorderShrinkStart: Pugna nether world border is null.");
            return;
        }

        pugnaWorldBorder.setCenter(pugnaWorldSpawn);
        pugnaNetherWorldBorder.setCenter(pugnaNetherWorldSpawn);

        long duration = Math.max(1, borderShrinkEndSeconds - borderShrinkStartSeconds);

        pugnaWorldBorder.setSize(GameConfig.getBorderEndSize(), duration);
        pugnaNetherWorldBorder.setSize(GameConfig.getBorderEndSize(), duration);

        String message = ChatConfig.getMessage(Message.BORDER_SHRINK_START);
        ChatUtils.broadcast(message);
        SoundUtils.broadcast(Sound.WITHER_SPAWN, 1.0f, 1.0f);
    }

    private void handleBorderShrinkEnd() {
        String message = ChatConfig.getMessage(Message.BORDER_SHRINK_END);
        ChatUtils.broadcast(message);
        SoundUtils.broadcast(Sound.NOTE_PLING, 1.0f, 2.0f);
    }

    private void handleNetherEndWarning(int seconds) {
        int remaining = netherEndSeconds - seconds;
        if (CountdownUtils.shouldAnnounce(remaining)) {
            String time = CountdownUtils.getTime(remaining);
            String unit = CountdownUtils.getUnit(remaining);
            String message = ChatConfig.getChatMessage(Message.NETHER_END_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
        }
    }

    private void handleNetherEnd() {
        gameManager.setAllowNether(false);
        NetherUtils.teleportPlayersToOverworld();
        String message = ChatConfig.getMessage(Message.NETHER_END);
        ChatUtils.broadcast(message);
        SoundUtils.broadcast(Sound.WITHER_DEATH, 1.0f, 1.0f);
    }

    private void handleEnemyRevealWarning(int seconds) {
        int remaining = enemyRevealSeconds - seconds;
        if (CountdownUtils.shouldAnnounce(remaining)) {
            String time = CountdownUtils.getTime(remaining);
            String unit = CountdownUtils.getUnit(remaining);
            String message = ChatConfig.getChatMessage(Message.ENEMY_REVEAL_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
        }
    }

    private void handleEnemyReveal() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null || !player.isOnline() || PlayerUtils.isSpectator(player)) {
                continue;
            }

            Player closestEnemy = PlayerUtils.getClosestEnemy(player);
            if (closestEnemy != null) {
                Location location = closestEnemy.getLocation();
                double distance = player.getLocation().distanceSquared(location);
                int meters = (int) Math.round(Math.sqrt(distance));
                String message = ChatConfig.getChatMessage(Message.ENEMY_REVEAL)
                        .player(closestEnemy.getName())
                        .meters(meters)
                        .x(location.getBlockX())
                        .y(location.getBlockY())
                        .z(location.getBlockZ())
                        .toString();
                player.sendMessage(message);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 2.0f);
            } else {
                String message = ChatConfig.getMessage(Message.ENEMY_REVEAL_NO_ENEMY);
                player.sendMessage(message);
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0f, 1.0f);
            }
        }
    }

    private void handleGameEndWarning(int seconds) {
        int remaining = gameEndSeconds - seconds;
        if (CountdownUtils.shouldAnnounce(remaining)) {
            String time = CountdownUtils.getTime(remaining);
            String unit = CountdownUtils.getUnit(remaining);
            String message = ChatConfig.getChatMessage(Message.GAME_END_COUNTDOWN).time(time).unit(unit).toString();
            ChatUtils.broadcast(message);
            SoundUtils.broadcast(Sound.NOTE_BASS, 1.0f, 1.0f);
            if (seconds == gameEndWarnStartSeconds) {
                String explanation = ChatConfig.getMessage(Message.GAME_END_EXPLANATION);
                ChatUtils.broadcast(explanation);
            }
        }
    }

    private void handleGameEnd() {
        Location center = worldManager.getPugnaWorldSpawn();
        if (center == null) {
            gameManager.setState(GameState.GAME_RESTARTING);
            cancel();
            return;
        }

        Team winner = null;
        double smallestTeamDistance = Double.MAX_VALUE;

        for (Team team : gameManager.getTeams()) {
            double smallestMemberDistance = Double.MAX_VALUE;
            for (UUID uuid : team.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline() || PlayerUtils.isSpectator(player) || !player.getWorld().getName().equals(GameConfig.getPugnaWorldName())) {
                    continue;
                }

                double distance = center.distanceSquared(player.getLocation());
                if (distance < smallestMemberDistance) {
                    smallestMemberDistance = distance;
                }
            }

            if (smallestMemberDistance < smallestTeamDistance) {
                smallestTeamDistance = smallestMemberDistance;
                winner = team;
            } else if (smallestMemberDistance == smallestTeamDistance && winner != null && team.getId() < winner.getId()) {
                winner = team;
            }
        }

        if (winner != null) {
            final Team winnerTeam = winner;
            gameManager.getTeams().removeIf(team -> team != winnerTeam);
            gameManager.handleElimination();
        } else {
            String message = ChatConfig.getMessage(Message.GAME_END_NO_WINNER);
            ChatUtils.broadcast(message);
        }
    }
}
