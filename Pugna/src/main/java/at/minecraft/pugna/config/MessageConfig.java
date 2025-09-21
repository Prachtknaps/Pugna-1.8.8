package at.minecraft.pugna.config;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.ChatMessage;
import at.minecraft.pugna.chat.Message;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

public class MessageConfig {
    private final Pugna plugin;
    private final String fileName = "messages.yml";

    private final File configFile;
    private FileConfiguration configuration;

    private String prefix;
    private final Map<Message, String> messages = new EnumMap<>(Message.class);

    public MessageConfig(final Pugna plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), this.fileName);
    }

    /* === Getters === */

    public FileConfiguration getConfig() {
        if (this.configuration == null) {
            reload();
        }

        return this.configuration;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRawPrefix() {
        return ChatColor.stripColor(prefix);
    }

    public String getMessage(Message message) {
        return messages.getOrDefault(message, "");
    }

    public ChatMessage getChatMessage(Message message) {
        return new ChatMessage(getMessage(message));
    }

    /* === Operations === */

    public void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder.");
        }

        if (!configFile.exists()) {
            try {
                plugin.saveResource(this.fileName, false);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().severe("Default resource '" + this.fileName + "' not found in JAR. Did you add it to src/main/resources/?");
            }
        }
    }

    public void save() {
        if (this.configuration == null) {
            return;
        }

        try {
            this.configuration.save(this.configFile);
        } catch (Exception exception) {
            plugin.getLogger().severe("Could not save '" + this.fileName + "': " + exception.getMessage());
        }
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);

        try (InputStream defaultStream = plugin.getResource(this.fileName)) {
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));

                this.configuration.setDefaults(defaultConfig);
                this.configuration.options().copyDefaults(true);
                this.configuration.options().copyHeader(true);
                save();
            } else {
                plugin.getLogger().fine("No default resource found for '" + this.fileName + "'.");
            }
        } catch (Exception exception) {
            plugin.getLogger().severe("Could not reload '" + this.fileName + "': " + exception.getMessage());
        }
    }

    /* === Setup === */

    public void setup() {
        prefix = configuration.getString("messages.prefix", "§7[§6Pugna§7]§r");
        messages.clear();

        /* === General === */
        messages.put(Message.INVALID_USAGE, new ChatMessage(configuration.getString("messages.general.invalid_usage", "{prefix} §cUngültige Verwendung des Befehls:§r")).prefix(getPrefix()).toString());
        messages.put(Message.NO_PERMISSION, new ChatMessage(configuration.getString("messages.general.no_permission", "{prefix} §cDu hast keine Berechtigung, diesen Befehl auszuführen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.PLAYERS_ONLY_COMMAND, new ChatMessage(configuration.getString("messages.general.players_only_command", "{prefix} Dieser Befehl kann nur von Spielern ausgeführt werden.")).prefix(getRawPrefix()).toString());
        messages.put(Message.INVALID_NUMBER, new ChatMessage(configuration.getString("messages.general.invalid_number", "{prefix} §cBitte gib eine gültige Zahl ein.§r")).prefix(getPrefix()).toString());
        messages.put(Message.NO_TEAM, new ChatMessage(configuration.getString("messages.general.no_team", "{prefix} §cDu befindest dich in keinem Team.§r")).prefix(getPrefix()).toString());

        /* === Countdown === */
        messages.put(Message.COUNTDOWN_USAGE, new ChatMessage(configuration.getString("messages.countdown.usage", "§7/countdown set <seconds>§r")).prefix(getPrefix()).toString());
        messages.put(Message.COUNTDOWN_NOT_ACTIVE, new ChatMessage(configuration.getString("messages.countdown.not_active", "{prefix} §cAktuell läuft kein Countdown.§r")).prefix(getPrefix()).toString());
        messages.put(Message.COUNTDOWN_UPDATE_SUCCESS, new ChatMessage(configuration.getString("messages.countdown.update_success", "{prefix} §aDer Countdown wurde auf §e{time} §a{unit} gesetzt.§r")).prefix(getPrefix()).toString());

        /* === GUI === */
        messages.put(Message.GUI_USAGE, new ChatMessage(configuration.getString("messages.gui.usage", "§7/gui enable/disable§r")).prefix(getPrefix()).toString());
        messages.put(Message.GUI_ENABLED, new ChatMessage(configuration.getString("messages.gui.enabled", "{prefix} §aDeine GUI wurde aktiviert.§r")).prefix(getPrefix()).toString());
        messages.put(Message.GUI_DISABLED, new ChatMessage(configuration.getString("messages.gui.disabled", "{prefix} §cDeine GUI wurde deaktiviert.§r")).prefix(getPrefix()).toString());

        /* === Team === */
        messages.put(Message.TEAM_USAGE, new ChatMessage(configuration.getString("messages.team.usage", "§7/team list, /team join <name>, /team rename <name>, /team leave§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_JOIN_HINT, new ChatMessage(configuration.getString("messages.team.join_hint", "§aDu kannst einem Team mit §e/team join <name> §abeitreten.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_RENAME_HINT, new ChatMessage(configuration.getString("messages.team.rename_hint", "§aDu kannst dein Team mit §e/team rename <name> §aumbenennen.§r")).prefix(getPrefix()).toString());

        messages.put(Message.TEAM_LIST_HEADER, new ChatMessage(configuration.getString("messages.team.list_header", "{prefix} §aAktuell gibt es folgende Teams:§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_LIST_ENTRY, new ChatMessage(configuration.getString("messages.team.list_entry", "§7- §e{team}§7: §f( {players} )§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_LIST_EMPTY, new ChatMessage(configuration.getString("messages.team.list_empty", "{prefix} §cEs existieren noch keine Teams.§r")).prefix(getPrefix()).toString());

        messages.put(Message.TEAM_JOIN_NOT_ALLOWED, new ChatMessage(configuration.getString("messages.team.join_not_allowed", "{prefix} §cDu kannst keinem Team mehr beitreten.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_JOIN_NOT_FOUND, new ChatMessage(configuration.getString("messages.team.join_not_found", "{prefix} §cEs existiert kein Team mit dem Namen §e{team}§c.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_JOIN_SAME_TEAM, new ChatMessage(configuration.getString("messages.team.join_same_team", "{prefix} §cDu befindest dich bereits in diesem Team.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_JOIN_FULL, new ChatMessage(configuration.getString("messages.team.join_full", "{prefix} §cDieses Team ist bereits voll.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_JOIN_SELF, new ChatMessage(configuration.getString("messages.team.join_self", "{prefix} §aDu bist dem Team §e{team} §abeigetreten.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_JOIN_OTHERS, new ChatMessage(configuration.getString("messages.team.join_others", "{prefix} §e{player} §aist deinem Team beigetreten.§r")).prefix(getPrefix()).toString());

        messages.put(Message.TEAM_LEAVE_NOT_ALLOWED, new ChatMessage(configuration.getString("messages.team.leave_not_allowed", "{prefix} §cDu kannst dein Team nicht mehr verlassen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_LEAVE_SELF, new ChatMessage(configuration.getString("messages.team.leave_self", "{prefix} §cDu hast das Team §e{team} §cverlassen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_LEAVE_OTHERS, new ChatMessage(configuration.getString("messages.team.leave_others", "{prefix} §e{player} §chat dein Team verlassen.§r")).prefix(getPrefix()).toString());

        messages.put(Message.TEAM_RENAME_NOT_ALLOWED, new ChatMessage(configuration.getString("messages.team.rename_not_allowed", "{prefix} §cDu kannst dein Team nicht mehr umbenennen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_RENAME_INVALID, new ChatMessage(configuration.getString("messages.team.rename_invalid", "{prefix} §cEin Team-Name darf nur aus Buchstaben und Zahlen bestehen und zwischen 4 und 16 Zeichen lang sein.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_RENAME_SAME_NAME, new ChatMessage(configuration.getString("messages.team.rename_same_name", "{prefix} §cDein Team heißt bereits §e{team}§c.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_RENAME_TAKEN, new ChatMessage(configuration.getString("messages.team.rename_taken", "{prefix} §cEs existiert bereits ein Team mit diesem Namen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_RENAME_SUCCESS, new ChatMessage(configuration.getString("messages.team.rename_success", "{prefix} §aDein Team wurde in §e{team} §aumbenannt.§r")).prefix(getPrefix()).toString());

        messages.put(Message.TEAM_ASSIGNED, new ChatMessage(configuration.getString("messages.team.assigned", "{prefix} §aDu wurdest dem Team §e{team} §azugewiesen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_NOT_ASSIGNED, new ChatMessage(configuration.getString("messages.team.not_assigned", "{prefix} §cDu konntest keinem Team zugewiesen werden (keine freien Teams verfügbar).§r")).prefix(getPrefix()).toString());

        messages.put(Message.TEAM_TELEPORT, new ChatMessage(configuration.getString("messages.team.teleport", "{prefix} §aDu wurdest teleportiert.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_ELIMINATED, new ChatMessage(configuration.getString("messages.team.eliminated", "§cDas Team §e{team} §cwurde ausgelöscht.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TEAM_WIN, new ChatMessage(configuration.getString("messages.team.win", "{prefix} §aDas Team §e{team} §ahat §ePugna §agewonnen.§r")).prefix(getPrefix()).toString());

        /* === Player === */
        messages.put(Message.PLAYER_JOIN, new ChatMessage(configuration.getString("messages.player.join", "§f» §e{player} §7hat den Server betreten.§r")).prefix(getPrefix()).toString());
        messages.put(Message.PLAYER_QUIT, new ChatMessage(configuration.getString("messages.player.quit", "§f» §e{player} §7hat den Server verlassen.§r")).prefix(getPrefix()).toString());

        messages.put(Message.PLAYER_DEATH_SELF, new ChatMessage(configuration.getString("messages.player.death_self", "§cDu bist gestorben.§r")).prefix(getPrefix()).toString());
        messages.put(Message.PLAYER_DEATH_OTHERS, new ChatMessage(configuration.getString("messages.player.death_others", "§e{player} §7ist gestorben.§r")).prefix(getPrefix()).toString());
        messages.put(Message.PLAYER_KILLED_SELF, new ChatMessage(configuration.getString("messages.player.killed_self", "§cDu wurdest von §e{killer} §7({health}§7) §cgetötet.§r")).prefix(getPrefix()).toString());
        messages.put(Message.PLAYER_KILLED_OTHERS, new ChatMessage(configuration.getString("messages.player.killed_others", "§e{player} §7wurde von §e{killer} §7getötet.§r")).prefix(getPrefix()).toString());

        messages.put(Message.PLAYER_WIN, new ChatMessage(configuration.getString("messages.player.win", "{prefix} §e{player} §ahat §ePugna §agewonnen.§r")).prefix(getPrefix()).toString());

        /* === Phases === */
        messages.put(Message.TELEPORT_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.teleport_countdown", "{prefix} §3Die Spieler werden in §e{time} §3{unit} teleportiert.§r")).prefix(getPrefix()).toString());
        messages.put(Message.TELEPORT_COUNTDOWN_ABORTED, new ChatMessage(configuration.getString("messages.phases.teleport_countdown_aborted", "{prefix} §cDer Countdown wurde abgebrochen - warte auf weitere Spieler.§r")).prefix(getPrefix()).toString());

        messages.put(Message.GAME_START_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.game_start_countdown", "{prefix} §3Das Spiel beginnt in §e{time} §3{unit}.§r")).prefix(getPrefix()).toString());
        messages.put(Message.GAME_START, new ChatMessage(configuration.getString("messages.phases.game_start", "{prefix} §aMögen die Spiele beginnen!§r")).prefix(getPrefix()).toString());
        messages.put(Message.GAME_PAUSED, new ChatMessage(configuration.getString("messages.phases.game_paused", "{prefix} §cDas Spiel ist derzeit pausiert. Bitte warte, bis mehr als die Hälfte der lebenden Spieler online ist.§r")).prefix(getPrefix()).toString());
        messages.put(Message.GAME_RESUMED, new ChatMessage(configuration.getString("messages.phases.game_resumed", "{prefix} §aDas Spiel wird nun wieder fortgesetzt.§r")).prefix(getPrefix()).toString());

        messages.put(Message.FORBIDDEN_ITEMS_REMOVED, new ChatMessage(configuration.getString("messages.phases.forbidden_items_removed", "{prefix} §cEs wurden verbotene Items aus deinem Inventar entfernt §7(§e{count} Stück§7)§c.§r")).prefix(getPrefix()).toString());

        messages.put(Message.NETHER_TELEPORT_NOT_ALLOWED, new ChatMessage(configuration.getString("messages.phases.nether_teleport_not_allowed", "{prefix} §cDer Nether kann derzeit nicht betreten werden.§r")).prefix(getPrefix()).toString());
        messages.put(Message.NETHER_PORTAL_BLOCK_MODIFICATION_NOT_ALLOWED, new ChatMessage(configuration.getString("messages.phases.nether_portal_block_modification_not_allowed", "{prefix} §cDu darfst in der Nähe des Nether-Portals keine Blöcke platzieren oder zerstören.§r")).prefix(getPrefix()).toString());
        messages.put(Message.NETHER_START_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.nether_start_countdown", "{prefix} §3Der Nether kann in §e{time} §3{unit} betreten werden.§r")).prefix(getPrefix()).toString());
        messages.put(Message.NETHER_START, new ChatMessage(configuration.getString("messages.phases.nether_start", "{prefix} §aDer Nether kann nun betreten werden.§r")).prefix(getPrefix()).toString());
        messages.put(Message.NETHER_END_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.nether_end_countdown", "{prefix} §cDer Nether wird in §e{time} §c{unit} geschlossen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.NETHER_END, new ChatMessage(configuration.getString("messages.phases.nether_end", "{prefix} §cDer Nether kann nun nicht mehr betreten werden.§r")).prefix(getPrefix()).toString());

        messages.put(Message.BORDER_SHRINK_START_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.border_shrink_start_countdown", "{prefix} §cDie Border beginnt in §e{time} §c{unit} zu schrumpfen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.BORDER_SHRINK_START, new ChatMessage(configuration.getString("messages.phases.border_shrink_start", "{prefix} §cDie Border verkleinert sich nun.§r")).prefix(getPrefix()).toString());
        messages.put(Message.BORDER_SHRINK_END, new ChatMessage(configuration.getString("messages.phases.border_shrink_end", "{prefix} §aDie Border hat ihre finale Größe erreicht.§r")).prefix(getPrefix()).toString());

        messages.put(Message.ENEMY_REVEAL_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.enemy_reveal_countdown", "{prefix} §cIn §e{time} §c{unit} werden die Koordinaten des nächsten feindlichen Spielers veröffentlicht.§r")).prefix(getPrefix()).toString());
        messages.put(Message.ENEMY_REVEAL_SUCCESS, new ChatMessage(configuration.getString("messages.phases.enemy_reveal_success", "{prefix} §cDer Spieler §e{player} §cist §e{meters} §cBlöcke entfernt: §7(x: {x}, y: {y}, z: {z})§r")).prefix(getPrefix()).toString());
        messages.put(Message.ENEMY_REVEAL_NO_ENEMY_FOUND, new ChatMessage(configuration.getString("messages.phases.enemy_reveal_no_enemy_found", "{prefix} §cEs konnte kein feindlicher Spieler gefunden werden.§r")).prefix(getPrefix()).toString());

        messages.put(Message.GAME_END_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.game_end_countdown", "{prefix} §cDas Spiel endet in §e{time} §c{unit}.§r")).prefix(getPrefix()).toString());
        messages.put(Message.GAME_END_EXPLANATION, new ChatMessage(configuration.getString("messages.phases.game_end_explanation", "§7Das Team, das sich am nächsten beim Portal befindet, gewinnt das Spiel.§r")).prefix(getPrefix()).toString());
        messages.put(Message.GAME_END_NO_WINNER, new ChatMessage(configuration.getString("messages.phases.game_end_no_winner", "{prefix} §cDie Zeit ist abgelaufen. Das Spiel endet unentschieden.§r")).prefix(getPrefix()).toString());

        messages.put(Message.SERVER_RESTART_COUNTDOWN, new ChatMessage(configuration.getString("messages.phases.server_restart_countdown", "{prefix} §cDer Server wird in §e{time} §c{unit} neu gestartet.§r")).prefix(getPrefix()).toString());

        /* === Kick === */
        messages.put(Message.KICK_SERVER_FULL, new ChatMessage(configuration.getString("messages.kick.server_full", "{prefix} §cDer Server ist voll - bitte warte.§r")).prefix(getPrefix()).toString());
        messages.put(Message.KICK_HUB_COMMAND, new ChatMessage(configuration.getString("messages.kick.hub_command", "{prefix} §cDu hast den Server verlassen.§r")).prefix(getPrefix()).toString());
        messages.put(Message.KICK_SERVER_RESTARTING, new ChatMessage(configuration.getString("messages.kick.server_restarting", "{prefix} §cDer Server wird neu gestartet.§r")).prefix(getPrefix()).toString());
    }
}
