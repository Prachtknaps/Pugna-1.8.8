package at.minecraft.pugna.game.events;

public interface GameEvent {
    String getEventName();
    int getEventSeconds();
    boolean isExpired(int seconds);
    void handle(int seconds);
}
