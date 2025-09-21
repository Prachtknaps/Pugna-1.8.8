package at.minecraft.pugna.game.events;

public interface GameEvent {
    boolean isExpired(int seconds);
    void handle(int seconds);
}
