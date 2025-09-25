package at.minecraft.pugna.game.timers;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;
import at.minecraft.pugna.config.MessageConfig;
import at.minecraft.pugna.config.PugnaConfig;
import at.minecraft.pugna.game.GameManager;
import at.minecraft.pugna.game.GameState;
import at.minecraft.pugna.game.events.*;
import at.minecraft.pugna.utils.PlayerUtils;
import at.minecraft.pugna.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class GameTimer extends BukkitRunnable {
    private final GameConfig gameConfig;
    private final WorldManager worldManager;
    private final GameManager gameManager;
    private int seconds;

    private Scoreboard sharedScoreboard = null;
    private Scoreboard emptyScoreboard = null;
    private Sidebar sidebar = null;
    private boolean sidebarInitialized = false;

    private final List<UUID> blockedGUIPlayers;
    private final List<GameEvent> events;
    private boolean running = false;

    public GameTimer(PugnaConfig pugnaConfig, MessageConfig messageConfig, GameConfig gameConfig, WorldManager worldManager, GameManager gameManager) {
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

    /* === Getters === */

    public List<GameEvent> getEvents() {
        return events;
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
        ensureSidebarInitialized();
        if (!sidebarInitialized) {
            return;
        }

        final int alivePlayersCount = PlayerUtils.getAlivePlayersCount();
        final int teamsCount = gameManager.getTeams().size();
        final String formattedTime = formatTime(this.seconds);

        double borderSizeBlocks;
        if (worldManager.getPugnaWorld() != null && worldManager.getPugnaWorld().getWorldBorder() != null) {
            borderSizeBlocks = worldManager.getPugnaWorld().getWorldBorder().getSize();
        } else {
            borderSizeBlocks = gameConfig.getCurrentBorderSize();
        }
        final int borderBlocks = (int) Math.round(borderSizeBlocks);

        final String nextEventLabel;
        final String timeUntilNextEvent;

        GameState currentState = gameManager.getState();
        if (currentState == GameState.GAME_RUNNING || currentState == GameState.GAME_PAUSED) {
            NextEventInfo nextInfo = findNextEvent(Math.max(0, this.seconds));
            nextEventLabel = nextInfo.getEventName();
            timeUntilNextEvent = (nextInfo.getSecondsUntil() >= 0) ? formatTime(nextInfo.getSecondsUntil()) : "—";
        } else {
            nextEventLabel = "—";
            timeUntilNextEvent = "—";
        }

        sidebar.setLineText("time_value", "§f" + formattedTime);
        sidebar.setLineText("teams_value", "§f" + teamsCount + " §7(" + alivePlayersCount + ")");
        sidebar.setLineText("border_value", "§f" + borderBlocks);
        sidebar.setLineText("next_event_name", "§d" + nextEventLabel);
        sidebar.setLineText("next_event_time", "§f" + timeUntilNextEvent);

        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isBlocked = blockedGUIPlayers.contains(player.getUniqueId());

            if (isBlocked) {
                if (player.getScoreboard() != this.emptyScoreboard) {
                    player.setScoreboard(this.emptyScoreboard);
                }
                continue;
            }

            if (player.getScoreboard() != this.sharedScoreboard) {
                player.setScoreboard(this.sharedScoreboard);
            }
        }
    }

    public void enableGUIFor(Player player) {
        blockedGUIPlayers.remove(player.getUniqueId());
    }

    public void disableGUIFor(Player player) {
        blockedGUIPlayers.add(player.getUniqueId());
    }

    /* === Helpers === */

    private static final class Sidebar {
        private final Scoreboard scoreboard;
        private final Objective objective;

        private final Map<String, Team> teamsByKey = new LinkedHashMap<>();
        private final Map<String, String> entryByKey = new LinkedHashMap<>();

        private final Deque<String> availableEntryTokens = new ArrayDeque<>();

        private Sidebar(Scoreboard scoreboard, Objective objective) {
            this.scoreboard = scoreboard;
            this.objective = objective;

            ChatColor[] palette = new ChatColor[] {
                    ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA,
                    ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY,
                    ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA,
                    ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW
            };

            for (ChatColor chatColor : palette) {
                availableEntryTokens.add(chatColor.toString() + ChatColor.RESET);
            }
        }

        public void registerStaticLine(String key, String initialText, int scoreValue) {
            Team lineTeam = ensureTeamForKey(key);
            String entryToken = ensureEntryForKey(key);

            this.objective.getScore(entryToken).setScore(scoreValue);

            setTeamText(lineTeam, initialText);
        }

        public void registerDynamicLine(String key, int scoreValue) {
            Team lineTeam = ensureTeamForKey(key);
            String entryToken = ensureEntryForKey(key);

            this.objective.getScore(entryToken).setScore(scoreValue);

            setTeamText(lineTeam, "");
        }

        public void setLineText(String key, String text) {
            Team lineTeam = this.teamsByKey.get(key);
            if (lineTeam == null) {
                return;
            }

            if (text == null) {
                text = "";
            }

            setTeamText(lineTeam, text);
        }

        private Team ensureTeamForKey(String key) {
            Team existing = this.teamsByKey.get(key);
            if (existing != null) {
                return existing;
            }

            Team team = this.scoreboard.getTeam(key);
            if (team == null) {
                team = this.scoreboard.registerNewTeam(key);
            }

            this.teamsByKey.put(key, team);
            return team;
        }

        private String ensureEntryForKey(String key) {
            String existing = this.entryByKey.get(key);
            if (existing != null) {
                return existing;
            }

            String token = this.availableEntryTokens.isEmpty() ? (ChatColor.RESET.toString()) : this.availableEntryTokens.removeFirst();

            Team team = ensureTeamForKey(key);
            team.addEntry(token);

            this.entryByKey.put(key, token);
            return token;
        }

        private void setTeamText(Team team, String fullText) {
            String prefix = fullText;
            String suffix = "";

            if (prefix.length() > 16) {
                prefix = fullText.substring(0, 16);
                suffix = fullText.substring(16);
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }

            team.setPrefix(prefix);
            team.setSuffix(suffix);
        }
    }

    private void ensureSidebarInitialized() {
        if (sidebarInitialized) {
            return;
        }

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return;
        }

        this.sharedScoreboard = scoreboardManager.getNewScoreboard();
        this.emptyScoreboard = scoreboardManager.getNewScoreboard();

        Objective sharedObjective = this.sharedScoreboard.getObjective("pugna");
        if (sharedObjective != null) {
            sharedObjective.unregister();
        }

        sharedObjective = this.sharedScoreboard.registerNewObjective("pugna", "dummy");
        sharedObjective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        sharedObjective.setDisplayName("§6§lPugna");

        this.sidebar = new Sidebar(this.sharedScoreboard, sharedObjective);

        int currentScore = 14;

        sidebar.registerStaticLine("sep_time_top", "§0", currentScore--);
        sidebar.registerStaticLine("time_title", "§eZeit                ", currentScore--);
        sidebar.registerDynamicLine("time_value", currentScore--);

        sidebar.registerStaticLine("sep_teams_top", "§1", currentScore--);
        sidebar.registerStaticLine("teams_title", "§eTeams               ", currentScore--);
        sidebar.registerDynamicLine("teams_value", currentScore--);

        sidebar.registerStaticLine("sep_border_top", "§2", currentScore--);
        sidebar.registerStaticLine("border_title", "§eBorder              ", currentScore--);
        sidebar.registerDynamicLine("border_value", currentScore--);

        sidebar.registerStaticLine("sep_event_top", "§3", currentScore--);
        sidebar.registerStaticLine("next_title", "§eNächstes Event      ", currentScore--);
        sidebar.registerDynamicLine("next_event_name", currentScore--);
        sidebar.registerDynamicLine("next_event_time", currentScore--);

        sidebar.registerStaticLine("sep_bottom", "§4", currentScore--);

        sidebarInitialized = true;
    }

    public static String formatTime(int totalSeconds) {
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
