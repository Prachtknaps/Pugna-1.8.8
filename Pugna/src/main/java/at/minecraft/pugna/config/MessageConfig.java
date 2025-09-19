package at.minecraft.pugna.config;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.chat.ChatMessage;
import at.minecraft.pugna.chat.Message;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.Map;

public final class MessageConfig {
    private MessageConfig() {}

    private static String prefix;
    private static final Map<Message, String> messages = new EnumMap<>(Message.class);

    /* === Setup === */

    public static void setup() {
        FileConfiguration configuration = Pugna.getInstance().getConfig();

        prefix = configuration.getString("chat.prefix", "§7[§6Pugna§7]§r") + " ";

        messages.clear();

        /* === General === */
        messages.put(Message.INVALID_USAGE, getPrefix() + configuration.getString("chat.messages.invalid_usage", "§cUngültige Verwendung des Befehls:§r"));
        messages.put(Message.NO_PERMISSION, getPrefix() + configuration.getString("chat.messages.no_permission", "§cDu hast keine Berechtigung, diesen Befehl auszuführen.§r"));
        messages.put(Message.PLAYERS_ONLY_COMMAND, getRawPrefix() + configuration.getString("chat.messages.players_only_command", "Dieser Befehl kann nur von Spielern ausgeführt werden."));
        messages.put(Message.INVALID_NUMBER, getPrefix() + configuration.getString("chat.messages.invalid_number", "§cBitte gib eine gültige Zahl ein.§r"));
        messages.put(Message.NO_TEAM, getPrefix() + configuration.getString("chat.messages.no_team", "§cDu befindest dich in keinem Team.§r"));

        /* === Countdown === */
        messages.put(Message.COUNTDOWN_USAGE, configuration.getString("chat.messages.countdown_usage", "§7/countdown set <seconds>"));
        messages.put(Message.COUNTDOWN_NOT_ACTIVE, getPrefix() + configuration.getString("chat.messages.countdown_not_active", "§cAktuell läuft kein Countdown.§r"));
        messages.put(Message.COUNTDOWN_UPDATE_SUCCESS, getPrefix() + configuration.getString("chat.messages.countdown_update_success", "§aDer Countdown wurde auf §e{time} §a{unit} gesetzt.§r"));

        /* === GUI === */
        messages.put(Message.GUI_USAGE, configuration.getString("chat.messages.gui_usage", "§7/gui enable/disable§r"));
        messages.put(Message.GUI_ENABLED, getPrefix() + configuration.getString("chat.messages.gui_enabled", "§aDeine GUI wurde aktiviert.§r"));
        messages.put(Message.GUI_DISABLED, getPrefix() + configuration.getString("chat.messages.gui_disabled", "§cDeine GUI wurde deaktiviert.§r"));

        /* === Team === */
        messages.put(Message.TEAM_USAGE, configuration.getString("chat.messages.team_usage", "§7/team list, /team join <name>, /team rename <name>, /team leave§r"));
        messages.put(Message.TEAM_JOIN_HINT, configuration.getString("chat.messages.team_join_hint", "§aDu kannst einem Team mit §e/team join <name> §abeitreten.§r"));
        messages.put(Message.TEAM_RENAME_HINT, configuration.getString("chat.messages.team_rename_hint", "§aDu kannst dein Team mit §e/team rename <name> §aumbenennen.§r"));

        messages.put(Message.TEAM_LIST_HEADER, getPrefix() + configuration.getString("chat.messages.team_list_header", "§aAktuell gibt es folgende Teams:§r"));
        messages.put(Message.TEAM_LIST_ENTRY, configuration.getString("chat.messages.team_list_entry", "§7- §e{team}§7: §f( {players} )§r"));
        messages.put(Message.TEAM_LIST_EMPTY, getPrefix() + configuration.getString("chat.messages.team_list_empty", "§cEs existieren noch keine Teams.§r"));

        messages.put(Message.TEAM_JOIN_NOT_ALLOWED, getPrefix() + configuration.getString("chat.messages.team_join_not_allowed", "§cDu kannst keinem Team mehr beitreten.§r"));
        messages.put(Message.TEAM_JOIN_NOT_FOUND, getPrefix() + configuration.getString("chat.messages.team_join_not_found", "§cEs existiert kein Team mit dem Namen §e{team}§c.§r"));
        messages.put(Message.TEAM_JOIN_SAME_TEAM, getPrefix() + configuration.getString("chat.messages.team_join_same_team", "§cDu befindest dich bereits in diesem Team.§r"));
        messages.put(Message.TEAM_JOIN_FULL, getPrefix() + configuration.getString("chat.messages.team_join_full", "§cDieses Team ist bereits voll.§r"));
        messages.put(Message.TEAM_JOIN_SELF, getPrefix() + configuration.getString("chat.messages.team_join_self", "§aDu bist dem Team §e{team} §abeigetreten.§r"));
        messages.put(Message.TEAM_JOIN_OTHERS, getPrefix() + configuration.getString("chat.messages.team_join_others", "§e{player} §aist deinem Team beigetreten.§r"));

        messages.put(Message.TEAM_LEAVE_NOT_ALLOWED, getPrefix() + configuration.getString("chat.messages.team_leave_not_allowed", "§cDu kannst dein Team nicht mehr verlassen.§r"));
        messages.put(Message.TEAM_LEAVE_SELF, getPrefix() + configuration.getString("chat.messages.team_leave_self", "§cDu hast das Team §e{team} §cverlassen.§r"));
        messages.put(Message.TEAM_LEAVE_OTHERS, getPrefix() + configuration.getString("chat.messages.team_leave_others", "§e{player} §chat dein Team verlassen.§r"));

        messages.put(Message.TEAM_RENAME_NOT_ALLOWED, getPrefix() + configuration.getString("chat.messages.team_rename_not_allowed", "§cDu kannst dein Team nicht mehr umbenennen.§r"));
        messages.put(Message.TEAM_RENAME_INVALID, getPrefix() + configuration.getString("chat.messages.team_rename_invalid", "§cEin Team-Name darf nur aus Buchstaben und Zahlen bestehen und zwischen 4 und 16 Zeichen lang sein.§r"));
        messages.put(Message.TEAM_RENAME_SAME_NAME, getPrefix() + configuration.getString("chat.messages.team_rename_same_name", "§cDein Team heißt bereits §e{team}§c.§r"));
        messages.put(Message.TEAM_RENAME_TAKEN, getPrefix() + configuration.getString("chat.messages.team_rename_taken", "§cEs existiert bereits ein Team mit diesem Namen.§r"));
        messages.put(Message.TEAM_RENAME_SUCCESS, getPrefix() + configuration.getString("chat.messages.team_rename_success", "§aDein Team wurde in §e{team} §aumbenannt.§r"));

        messages.put(Message.TEAM_ASSIGNED, getPrefix() + configuration.getString("chat.messages.team_assigned", "§aDu wurdest dem Team §e{team} §azugewiesen.§r"));
        messages.put(Message.TEAM_NOT_ASSIGNED, getPrefix() + configuration.getString("chat.messages.team_not_assigned", "§cDu konntest keinem Team zugewiesen werden (keine freien Teams verfügbar).§r"));

        messages.put(Message.TEAM_TELEPORT, getPrefix() + configuration.getString("chat.messages.team_teleport", "§aDu wurdest teleportiert.§r"));
        messages.put(Message.TEAM_ELIMINATED, configuration.getString("chat.messages.team_eliminated", "§cDas Team §e{team} §cwurde ausgelöscht.§r"));
        messages.put(Message.TEAM_WIN, getPrefix() + configuration.getString("chat.messages.team_win", "§aDas Team §e{team} §ahat §ePugna §agewonnen.§r"));

        /* === Player === */
        messages.put(Message.PLAYER_JOIN, configuration.getString("chat.messages.player_join", "§f» §e{player} §7hat den Server betreten.§r"));
        messages.put(Message.PLAYER_QUIT, configuration.getString("chat.messages.player_quit", "§f» §e{player} §7hat den Server verlassen.§r"));

        messages.put(Message.PLAYER_DEATH_SELF, configuration.getString("chat.messages.player_death_self", "§cDu bist gestorben.§r"));
        messages.put(Message.PLAYER_DEATH_OTHERS, configuration.getString("chat.messages.player_death_others", "§e{player} §7ist gestorben.§r"));

        messages.put(Message.PLAYER_KILLED_SELF, configuration.getString("chat.messages.player_killed_self", "§cDu wurdest von §e{killer} §7({health}§7) §cgetötet.§r"));
        messages.put(Message.PLAYER_KILLED_OTHERS, configuration.getString("chat.messages.player_killed_others", "§e{player} §7wurde von §e{killer} §7getötet.§r"));

        messages.put(Message.PLAYER_WIN, getPrefix() + configuration.getString("chat.messages.player_win", "§e{player} §ahat §ePugna §agewonnen.§r"));

        /* === Phases === */
        messages.put(Message.TELEPORT_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.teleport_countdown", "§3Die Spieler werden in §e{time} §3{unit} teleportiert.§r"));
        messages.put(Message.TELEPORT_COUNTDOWN_ABORTED, getPrefix() + configuration.getString("chat.messages.teleport_countdown_aborted", "§cDer Countdown wurde abgebrochen - warte auf weitere Spieler.§r"));

        messages.put(Message.GAME_START_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.game_start_countdown", "§3Das Spiel beginnt in §e{time} §3{unit}.§r"));
        messages.put(Message.GAME_START, getPrefix() + configuration.getString("chat.messages.game_start", "§aMögen die Spiele beginnen!§r"));
        messages.put(Message.GAME_PAUSED, getPrefix() + configuration.getString("chat.messages.game_paused", "§cDas Spiel ist derzeit pausiert. Bitte warte, bis mehr als die Hälfte der lebenden Spieler online ist.§r"));
        messages.put(Message.GAME_RESUMED, getPrefix() + configuration.getString("chat.messages.game_resumed", "§aDas Spiel wird nun wieder fortgesetzt.§r"));

        messages.put(Message.FORBIDDEN_ITEMS_REMOVED, getPrefix() + configuration.getString("chat.messages.forbidden_items_removed", "§cEs wurden verbotene Items aus deinem Inventar entfernt §7(§e{count} Stück§7)§c.§r"));

        messages.put(Message.NETHER_TELEPORT_NOT_ALLOWED, getPrefix() + configuration.getString("chat.messages.nether_teleport_not_allowed", "§cDer Nether kann derzeit nicht betreten werden.§r"));
        messages.put(Message.NETHER_PORTAL_BLOCK_MODIFICATION, getPrefix() + configuration.getString("chat.messages.nether_portal_block_modification", "§cDu darfst in der Nähe des Nether-Portals keine Blöcke platzieren oder zerstören.§r"));
        messages.put(Message.NETHER_START_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.nether_start_countdown", "§3Der Nether kann in §e{time} §3{unit} betreten werden.§r"));
        messages.put(Message.NETHER_START, getPrefix() + configuration.getString("chat.messages.nether_start", "§aDer Nether kann nun betreten werden.§r"));
        messages.put(Message.NETHER_END_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.nether_end_countdown", "§cDer Nether wird in §e{time} §c{unit} geschlossen.§r"));
        messages.put(Message.NETHER_END, getPrefix() + configuration.getString("chat.messages.nether_end", "§cDer Nether kann nun nicht mehr betreten werden.§r"));

        messages.put(Message.BORDER_SHRINK_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.border_shrink_countdown", "§cDie Border beginnt in §e{time} §c{unit} zu schrumpfen.§r"));
        messages.put(Message.BORDER_SHRINK_START, getPrefix() + configuration.getString("chat.messages.border_shrink_start", "§cDie Border verkleinert sich nun.§r"));
        messages.put(Message.BORDER_SHRINK_END, getPrefix() + configuration.getString("chat.messages.border_shrink_end", "§aDie Border hat ihre finale Größe erreicht.§r"));

        messages.put(Message.ENEMY_REVEAL_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.enemy_reveal_countdown", "§cIn §e{time} §c{unit} werden die Koordinaten des nächsten feindlichen Spielers veröffentlicht.§r"));
        messages.put(Message.ENEMY_REVEAL_SUCCESS, getPrefix() + configuration.getString("chat.messages.enemy_reveal_success", "§cDer Spieler §e{player} §cist §e{meters} §cBlöcke entfernt: §7(x: {x}, y: {y}, z: {z})§r"));
        messages.put(Message.ENEMY_REVEAL_NO_ENEMY_FOUND, getPrefix() + configuration.getString("chat.messages.enemy_reveal_no_enemy_found", "§cEs konnte kein feindlicher Spieler gefunden werden.§r"));

        messages.put(Message.GAME_END_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.game_end_countdown", "§cDas Spiel endet in §e{time} §c{unit}.§r"));
        messages.put(Message.GAME_END_EXPLANATION, configuration.getString("chat.messages.game_end_explanation", "§7Das Team, das sich am nächsten beim Portal befindet, gewinnt das Spiel.§r"));
        messages.put(Message.GAME_END_NO_WINNER, getPrefix() + configuration.getString("chat.messages.game_end_no_winner", "§cDie Zeit ist abgelaufen. Das Spiel endet unentschieden.§r"));

        messages.put(Message.SERVER_RESTART_COUNTDOWN, getPrefix() + configuration.getString("chat.messages.server_restart_countdown", "§cDer Server wird in §e{time} §c{unit} neu gestartet.§r"));

        /* === Kick === */
        messages.put(Message.KICK_SERVER_FULL, getPrefix() + configuration.getString("chat.messages.kick_server_full", "§cDer Server ist voll - bitte warte.§r"));
        messages.put(Message.KICK_HUB_COMMAND, getPrefix() + configuration.getString("chat.messages.kick_hub_command", "§cDu hast den Server verlassen.§r"));
        messages.put(Message.KICK_SERVER_RESTARTING, getPrefix() + configuration.getString("chat.messages.kick_server_restarting", "§cDer Server wird neu gestartet.§r"));
    }

    /* === Getters === */

    public static String getPrefix() {
        return prefix;
    }

    public static String getRawPrefix() {
        return ChatColor.stripColor(prefix);
    }

    public static String getMessage(Message message) {
        return messages.getOrDefault(message, "");
    }

    public static ChatMessage getChatMessage(Message message) {
        return new ChatMessage(getMessage(message));
    }
}
