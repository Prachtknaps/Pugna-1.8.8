package at.minecraft.pugna.config;

import at.minecraft.pugna.chat.ChatMessage;
import at.minecraft.pugna.chat.Message;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.Map;

public final class ChatConfig {
    private ChatConfig() {}

    private static String prefix;
    private static final Map<Message, String> messages = new EnumMap<>(Message.class);

    /* === Setup === */

    public static void setup(FileConfiguration configuration) {
        prefix = "§7[§6Pugna§7]§r" + " ";

        messages.clear();

        /* === General === */
        messages.put(Message.INVALID_USAGE, getPrefix() + "§cUngültiger Befehl:§r");
        messages.put(Message.NO_PERMISSION, getPrefix() + "§cDu hast keine Berechtigung, diesen Befehl auszuführen.§r");
        messages.put(Message.PLAYER_ONLY_COMMAND, getRawPrefix() + "Dieser Befehl kann nur von Spielern ausgeführt werden.");

        /* === Countdown === */

        /* === Team === */
        messages.put(Message.TEAM_USAGE, "§7/team list, /team join <name>, /team rename <name>, /team leave§r");
        messages.put(Message.NO_TEAM, getPrefix() + "§cDu befindest dich in keinem Team.§r");
        messages.put(Message.TEAM_JOIN_HINT, getPrefix() + "§7Du kannst einem Team mit §e/team join <name> §7beitreten.§r");
        messages.put(Message.TEAM_RENAME_HINT, "§7Du kannst dein Team mit §e/team rename <name> §7umbenennen.§r");

        messages.put(Message.TEAM_LIST_HEADER, getPrefix() + "§aAktuell gibt es folgende Teams:§r");
        messages.put(Message.TEAM_LIST_ENTRY, "§7- §f{players}§r");
        messages.put(Message.TEAM_LIST_EMPTY, getPrefix() + "§cEs existieren noch keine Teams.§r");

        messages.put(Message.TEAM_JOIN_NOT_FOUND, getPrefix() + "§cEs existiert kein Team mit dem Namen §e{team}§c.§r");
        messages.put(Message.TEAM_JOIN_SAME_TEAM, getPrefix() + "§cDu befindest dich bereits in diesem Team.§r");
        messages.put(Message.TEAM_JOIN_FULL, getPrefix() + "§cDieses Team ist bereits voll.§r");
        messages.put(Message.TEAM_JOIN_SELF, getPrefix() + "§aDu bist dem Team §e{team} §abeigetreten.§r");
        messages.put(Message.TEAM_JOIN_OTHERS, getPrefix() + "§e{player} §aist deinem Team beigetreten.§r");

        messages.put(Message.TEAM_LEAVE_SELF, getPrefix() + "§cDu hast das Team §e{team} §cverlassen.§r");
        messages.put(Message.TEAM_LEAVE_OTHERS, getPrefix() + "§e{player} §chat dein Team verlassen.§r");

        messages.put(Message.TEAM_RENAME_INVALID, getPrefix() + "§cEin Team-Name darf nur aus Buchstaben und Zahlen bestehen und zwischen 4 und 16 Zeichen lang sein.§r");
        messages.put(Message.TEAM_RENAME_SAME_NAME, getPrefix() + "§cDein Team heißt bereits §e{team}§c.§r");
        messages.put(Message.TEAM_RENAME_TAKEN, getPrefix() + "§cEs existiert bereits ein Team mit diesem Namen.§r");
        messages.put(Message.TEAM_RENAME_SUCCESS, getPrefix() + "§aDein Team wurde in §e{team} §aumbenannt.§r");

        messages.put(Message.TEAM_ASSIGNED, getPrefix() + "§aDu wurdest dem Team §e{team} §azugewiesen.§r");
        messages.put(Message.TEAM_NOT_ASSIGNED, getPrefix() + "§cDu konntest keinem Team zugewiesen werden (keine freien Teams verfügbar).§r");
        messages.put(Message.TEAM_TELEPORT, getPrefix() + "§aDu wurdest teleportiert.§r");
        messages.put(Message.TEAM_ELIMINATED, "§cDas Team §e{team} §cwurde ausgelöscht.§r");
        messages.put(Message.TEAM_WIN, getPrefix() + "§aDas Team §e{team} §ahat §ePugna §agewonnen.§r");

        messages.put(Message.PLAYER_JOIN, "§f» §e{player} §7hat den Server betreten.§r");
        messages.put(Message.PLAYER_QUIT, "§f» §e{player} §7hat den Server verlassen.§r");
        messages.put(Message.PLAYER_DEATH_SELF, "§cDu bist gestorben.§r");
        messages.put(Message.PLAYER_DEATH_OTHERS, "§e{player} §7ist gestorben.§r");
        messages.put(Message.PLAYER_KILL_SELF, "§cDu wurdest von §e{killer} §7({health}§7) §cgetötet.§r");
        messages.put(Message.PLAYER_KILL_OTHERS, "§e{player} §7wurde von §e{killer} §7getötet.§r");
        messages.put(Message.PLAYER_WIN, getPrefix() + "§e{player} §ahat §ePugna §agewonnen.§r");

        /* === Phases === */
        messages.put(Message.TELEPORT_COUNTDOWN, getPrefix() + "§3Die Spieler werden in §e{time} §3{unit} teleportiert.§r");
        messages.put(Message.TELEPORT_COUNTDOWN_ABORTED, getPrefix() + "§cDer Countdown wurde abgebrochen - warte auf weitere Spieler.§r");

        messages.put(Message.GAME_START_COUNTDOWN, getPrefix() + "§3Das Spiel beginnt in §e{time} §3{unit}.§r");
        messages.put(Message.GAME_START, getPrefix() + "§aMögen die Spiele beginnen!§r");
        messages.put(Message.GAME_PAUSED, getPrefix() + "§cDas Spiel wurde pausiert. Bitte warte, bis mehr als die Hälfte der lebenden Spieler online ist.§r");

        messages.put(Message.NETHER_TELEPORT_NOT_ALLOWED, getPrefix() + "§cDer Nether kann derzeit nicht betreten werden.§r");
        messages.put(Message.NETHER_PORTAL_LAVA_PLACE, getPrefix() + "§cDu darfst in der Nähe des Nether-Portals keine Lava platzieren.§r");
        messages.put(Message.NETHER_START, getPrefix() + "§aDer Nether kann nun betreten werden.§r");

        messages.put(Message.BORDER_SHRINK_COUNTDOWN, getPrefix() + "§cDie Border beginnt in §e{time} §c{unit} zu schrumpfen.§r");
        messages.put(Message.BORDER_SHRINK_START, getPrefix() + "§cDie Border verkleinert sich nun.§r");
        messages.put(Message.BORDER_SHRINK_END, getPrefix() + "§aDie Border hat ihre finale Größe erreicht.§r");

        messages.put(Message.NETHER_END_COUNTDOWN, getPrefix() + "§cDer Nether wird in §e{time} §c{unit} geschlossen.§r");
        messages.put(Message.NETHER_END, getPrefix() + "§cDer Nether kann nun nicht mehr betreten werden.§r");

        messages.put(Message.ENEMY_REVEAL_COUNTDOWN, getPrefix() + "§cIn §e{time} §c{unit} werden die Koordinaten des nächsten feindlichen Spielers veröffentlicht.§r");
        messages.put(Message.ENEMY_REVEAL, getPrefix() + "§cDer Spieler §e{player} §cist §e{meters} §cBlöcke entfernt: §7(x: {x}, y: {y}, z: {z})§r");
        messages.put(Message.ENEMY_REVEAL_NO_ENEMY, getPrefix() + "§cEs konnte kein feindlicher Spieler gefunden werden.§r");

        messages.put(Message.GAME_END_COUNTDOWN, getPrefix() + "§cDas Spiel endet in §e{time} §c{unit}.§r");
        messages.put(Message.GAME_END_EXPLANATION, "§7Das Team, das sich am nächsten beim Portal befindet, gewinnt das Spiel.§r");
        messages.put(Message.GAME_END_NO_WINNER, getPrefix() + "§cDie Zeit ist abgelaufen. Das Spiel endet unentschieden.§r");

        messages.put(Message.SERVER_RESTART_COUNTDOWN, getPrefix() + "§cDer Server wird in §e{time} §c{unit} neu gestartet.§r");

        /* === Server === */
        messages.put(Message.KICK_SERVER_FULL, getPrefix() + "§cDer Server ist voll - bitte warte.§r");
        messages.put(Message.KICK_SERVER_RESTARTING, getPrefix() + "§cDer Server wird neu gestartet.§r");
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
