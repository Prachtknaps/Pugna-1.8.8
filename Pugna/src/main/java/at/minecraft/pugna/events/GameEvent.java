package at.minecraft.pugna.events;

public abstract class GameEvent {
    public abstract boolean isExpired(int seconds);
    public abstract void handle(int seconds);
}
