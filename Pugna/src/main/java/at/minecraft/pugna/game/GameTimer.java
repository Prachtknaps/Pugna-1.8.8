package at.minecraft.pugna.game;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.events.*;
import at.minecraft.pugna.utils.ItemUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class GameTimer extends BukkitRunnable {
    private final WorldManager worldManager;
    private final GameManager gameManager;
    private int seconds;

    private final List<UUID> blockedGUIPlayers;
    private final List<GameEvent> events;
    private boolean running = false;

    public GameTimer(WorldManager worldManager, GameManager gameManager) {
        this.worldManager = worldManager;
        this.gameManager = gameManager;
        this.seconds = 0;

        this.blockedGUIPlayers = new ArrayList<>();

        this.events = new ArrayList<>(Arrays.asList(
            new NetherStartEvent(gameManager, GameConfig.getNetherStartCountdownStartSeconds(), GameConfig.getNetherStartSeconds()),
            new BorderShrinkEvent(worldManager, GameConfig.getBorderShrinkCountdownStartSeconds(), GameConfig.getBorderShrinkStartSeconds(), GameConfig.getBorderShrinkEndSeconds()),
            new NetherEndEvent(gameManager, GameConfig.getNetherEndCountdownStartSeconds(), GameConfig.getNetherEndSeconds()),
            new EnemyRevealEvent(GameConfig.getEnemyRevealCountdownStartSeconds(), GameConfig.getEnemyRevealSeconds()),
            new GameEndEvent(worldManager, gameManager, GameConfig.getGameEndCountdownStartSeconds(), GameConfig.getGameEndSeconds())
        ));
    }

    /* === Operations === */

    public void start() {
        if (running) {
            return;
        }

        this.running = true;
        runTaskTimer(Pugna.getInstance(), 0L, 20L);
    }

    public void setSeconds(int seconds) {
        this.seconds = Math.max(0, seconds);
    }

    @Override
    public void cancel() throws IllegalStateException {
        super.cancel();
        this.running = false;
    }

    @Override
    public void run() {
        handleGUI();

        if (gameManager.getState() == GameState.GAME_RUNNING) {
            if (seconds % 10 == 0) {
                handleForbiddenItemRemove();
            }

            Iterator<GameEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
                GameEvent event = iterator.next();
                if (event.isExpired(seconds)) {
                    iterator.remove();
                } else {
                    event.handle(seconds);
                }
            }

            GameConfig.setSeconds(seconds);
            GameConfig.saveGameTime(worldManager.getPugnaWorld().getTime());
            seconds++;
        }
    }

    private void handleGUI() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return;
        }

        final int playersCount = PlayerUtils.getAlivePlayersCount();
        final int teamsCount = gameManager.getTeams().size();
        final String formattedTime = formatTime(seconds);

        Bukkit.getScheduler().runTask(Pugna.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (blockedGUIPlayers.contains(player.getUniqueId())) {
                    player.setScoreboard(scoreboardManager.getNewScoreboard());
                    continue;
                }

                Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("pugna", "dummy");
                objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
                objective.setDisplayName("§6§lPugna");

                int line = 7;

                objective.getScore("§0").setScore(line--);
                objective.getScore("§eZeit").setScore(line--);
                objective.getScore("§f" + formattedTime).setScore(line--);
                objective.getScore("§1").setScore(line--);

                objective.getScore("§eTeams").setScore(line--);
                objective.getScore("§f" + teamsCount + " §7(" + playersCount + " Spieler)").setScore(line--);
                objective.getScore("§2").setScore(line--);

                player.setScoreboard(scoreboard);
            }
        });
    }

    public void enableGUIFor(Player player) {
        blockedGUIPlayers.remove(player.getUniqueId());
    }

    public void disableGUIFor(Player player) {
        blockedGUIPlayers.add(player.getUniqueId());
    }

    private void handleForbiddenItemRemove() {
        Bukkit.getScheduler().runTask(Pugna.getInstance(), () -> {
            for (Player player : PlayerUtils.getOnlineAlivePlayers()) {
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
                    String message = MessageConfig.getChatMessage(Message.FORBIDDEN_ITEMS_REMOVED).count(removed).toString();
                    player.sendMessage(message);
                    player.updateInventory();
                }
            }
        });
    }

    /* === Helpers === */

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
