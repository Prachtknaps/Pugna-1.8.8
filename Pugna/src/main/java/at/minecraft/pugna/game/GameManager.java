package at.minecraft.pugna.game;

import at.minecraft.pugna.world.WorldManager;

public class GameManager {
    // TODO: Implement class
    private final WorldManager worldManager;

    private GameState state;
    private boolean allowNether;

    public GameManager(WorldManager worldManager) {
        this.worldManager = worldManager;

        this.state = GameState.LOBBY_WAITING;
    }

    /* === Getters === */

    public GameState getState() {
        return state;
    }

    public boolean allowNether() {
        return allowNether;
    }

    /* === Operations === */

    public void setState(GameState state) {
        this.state = state;

        // TODO: Implement method
    }

    public void setAllowNether(boolean allowNether) {
        this.allowNether = allowNether;
    }

    public void handleElimination() {
        // TODO: Implement method
    }
}
