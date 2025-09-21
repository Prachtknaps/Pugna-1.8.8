package at.minecraft.pugna.game.timers;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.Message;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.game.events.*;
import at.minecraft.pugna.utils.ItemUtils;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class GameTimer extends BukkitRunnable {
    private final MessageConfig messageConfig;
    private final GameConfig gameConfig;
    private final WorldManager worldManager;
    private final GameManager gameManager;
    private int seconds;

    private final List<UUID> blockedGUIPlayers;
    private final List<GameEvent> events;
    private boolean running = false;

    public GameTimer(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameConfig gameConfig, WorldManager worldManager, GameManager gameManager) {
        this.messageConfig = messageConfig;
        this.gameConfig = gameConfig;
        this.worldManager = worldManager;
        this.gameManager = gameManager;
        this.seconds = 0;

        this.blockedGUIPlayers = new ArrayList<>();

        this.events = new ArrayList<>(Arrays.asList(
                new NetherStartEvent(messageConfig, pugnaConfig.getNetherStartCountdownStartSeconds(), pugnaConfig.getNetherStartSeconds()),
                new BorderShrinkEvent(messageConfig, pugnaConfig.getBorderShrinkStartCountdownStartSeconds(), pugnaConfig.getBorderShrinkStartSeconds(), pugnaConfig.getBorderShrinkEndSeconds()),
                new NetherEndEvent(messageConfig, pugnaConfig.getNetherEndCountdownStartSeconds(), pugnaConfig.getNetherEndSeconds()),
                new EnemyRevealEvent(messageConfig, pugnaConfig.getEnemyRevealCountdownStartSeconds(), pugnaConfig.getEnemyRevealSeconds()),
                new GameEndEvent(pugnaConfig, messageConfig, worldManager, gameManager, pugnaConfig.getGameEndCountdownStartSeconds(), pugnaConfig.getGameEndSeconds())
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
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            handleGUI();
        }

        if (gameManager.getState() == GameState.GAME_RUNNING) {
            if (seconds % 5 == 0) {
                handleForbiddenItemRemove();
            }

            if (seconds % 10 == 0) {
                gameConfig.saveSeconds(seconds);
                World pugnaWorld = worldManager.getPugnaWorld();
                if (pugnaWorld != null) {
                    gameConfig.saveTime(pugnaWorld.getTime());

                    WorldBorder worldBorder = pugnaWorld.getWorldBorder();
                    if (worldBorder != null) {
                        gameConfig.saveCurrentBorderSize(worldBorder.getSize());
                    }
                }
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

            seconds++;
        }
    }

    private void handleGUI() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return;
        }

        final int alivePlayersCount = PlayerUtils.getAlivePlayersCount();
        final int teamsCount = gameManager.getTeams().size();
        final String formattedTime = formatTime(this.seconds);

        double borderSize;
        if (worldManager.getPugnaWorld() != null && worldManager.getPugnaWorld().getWorldBorder() != null) {
            borderSize = worldManager.getPugnaWorld().getWorldBorder().getSize();
        } else {
            borderSize = gameConfig.getCurrentBorderSize();
        }
        final int borderBlocks = (int) Math.round(borderSize);

        final String nextEventLabel;
        final String timeUntilNextEvent;

        GameState currentState = gameManager.getState();
        if (currentState == GameState.GAME_RUNNING || currentState == GameState.GAME_PAUSED) {
            NextEventInfo next = findNextEvent(Math.max(0, this.seconds));
            nextEventLabel = next.getEventName();
            timeUntilNextEvent = (next.getSecondsUntil() >= 0) ? formatTime(next.getSecondsUntil()) : "—";
        } else {
            nextEventLabel = "—";
            timeUntilNextEvent = "—";
        }

        Bukkit.getScheduler().runTask(Pugna.getInstance(), () -> {
            Scoreboard sharedScoreboard = scoreboardManager.getNewScoreboard();
            Objective objective = sharedScoreboard.registerNewObjective("pugna", "dummy");
            objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
            objective.setDisplayName("§6§lPugna");

            int line = 14;

            /* === Time === */
            objective.getScore("§0").setScore(line--);
            objective.getScore("§eZeit                ").setScore(line--);
            objective.getScore("§f" + formattedTime).setScore(line--);

            /* === Teams === */
            objective.getScore("§1").setScore(line--);
            objective.getScore("§eTeams               ").setScore(line--);
            objective.getScore("§f" + teamsCount + " §7(" + alivePlayersCount + " Spieler)").setScore(line--);

            /* === Border === */
            objective.getScore("§2").setScore(line--);
            objective.getScore("§eBorder              ").setScore(line--);
            objective.getScore("§f" + borderBlocks + " §7Blöcke").setScore(line--);

            /* === Next Event === */
            objective.getScore("§3").setScore(line--);
            objective.getScore("§eNächstes Event      ").setScore(line--);
            objective.getScore("§d" + nextEventLabel).setScore(line--);
            objective.getScore("§f" + timeUntilNextEvent).setScore(line--);
            objective.getScore("§4").setScore(line--);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (blockedGUIPlayers.contains(player.getUniqueId())) {
                    player.setScoreboard(scoreboardManager.getNewScoreboard());
                    continue;
                }
                player.setScoreboard(sharedScoreboard);
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
                    String message = messageConfig.getChatMessage(Message.FORBIDDEN_ITEMS_REMOVED).count(removed).toString();
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

    private static final class NextEventInfo {
        private final String eventName;
        private final int secondsUntil;

        private NextEventInfo(String eventName, int secondsUntil) {
            this.eventName = eventName;
            this.secondsUntil = secondsUntil;
        }

        public String getEventName() {
            return eventName;
        }

        public int getSecondsUntil() {
            return secondsUntil;
        }
    }

    private NextEventInfo findNextEvent(int nowSeconds) {
        GameEvent nextEvent = null;
        int nextEventSeconds = Integer.MAX_VALUE;

        for (GameEvent gameEvent : this.events) {
            if (gameEvent == null || gameEvent.isExpired(nowSeconds)) {
                continue;
            }

            int eventSeconds = gameEvent.getEventSeconds();
            if (eventSeconds < nowSeconds) {
                continue;
            }

            if (eventSeconds < nextEventSeconds) {
                nextEvent = gameEvent;
                nextEventSeconds = eventSeconds;
            } else if (eventSeconds == nextEventSeconds && nextEvent != null) {
                String candidateName = String.valueOf(gameEvent.getEventName());
                String currentName   = String.valueOf(nextEvent.getEventName());
                if (candidateName.compareToIgnoreCase(currentName) < 0) {
                    nextEvent = gameEvent;
                }
            }
        }

        if (nextEvent == null) {
            return new NextEventInfo("—", -1);
        }

        int secondsUntil = Math.max(0, nextEventSeconds - nowSeconds);
        return new NextEventInfo(nextEvent.getEventName(), secondsUntil);
    }
}
