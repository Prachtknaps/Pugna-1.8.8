package at.minecraft.pugna.game;

import at.minecraft.pugna.world.WorldManager;

public class GameManager {
    // TODO: Implement class
    private final WorldManager worldManager;

    private GameState state;

    public GameManager(WorldManager worldManager) {
        this.worldManager = worldManager;

        this.state = GameState.LOBBY_WAITING;
    }

    /* === Getters === */

    public GameState getState() {
        return state;
    }

    /* === Operations === */

    public void setState(GameState state) {
        this.state = state;

        // TODO: Implement method
    }
}
