package at.minecraft.pugna.chat;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ChatMessage {
    private String message;

    public ChatMessage(String template) {
        this.message = template;
    }

    /* === Queries === */

    public String toString() {
        return message;
    }

    /* === Operations === */

    public ChatMessage prefix(String prefix) {
        message = message.replace("{prefix}", prefix);
        return this;
    }

    public ChatMessage player(String player) {
        message = message.replace("{player}", player);
        return this;
    }

    public ChatMessage killer(String killer) {
        message = message.replace("{killer}", killer);
        return this;
    }

    public ChatMessage time(String time) {
        message = message.replace("{time}", time);
        return this;
    }

    public ChatMessage unit(String unit) {
        message = message.replace("{unit}", unit);
        return this;
    }

    public ChatMessage team(String team) {
        message = message.replace("{team}", team);
        return this;
    }

    public ChatMessage players(List<Player> players) {
        String names = players.stream().map(Player::getName).collect(Collectors.joining(", "));
        if (players.isEmpty()) {
            names = "-";
        }
        message = message.replace("{players}", names);
        return this;
    }

    public ChatMessage health(double health) {
        double hearts = health / 2.0;
        String formatted = String.format("%.1f", hearts);
        String color = (hearts <= 3.0) ? "§4" : (hearts <= 7.0) ? "§6" : "§a";
        message = message.replace("{health}", color + formatted + "§r");
        return this;
    }

    public ChatMessage meters(double meters) {
        String formatted = String.format("%.1f", meters);
        message = message.replace("{meters}", formatted);
        return this;
    }

    public ChatMessage x(int x) {
        message = message.replace("{x}", Integer.toString(x));
        return this;
    }

    public ChatMessage y(int y) {
        message = message.replace("{y}", Integer.toString(y));
        return this;
    }

    public ChatMessage z(int z) {
        message = message.replace("{z}", Integer.toString(z));
        return this;
    }

    public ChatMessage count(int count) {
        message = message.replace("{count}", Integer.toString(count));
        return this;
    }
}
