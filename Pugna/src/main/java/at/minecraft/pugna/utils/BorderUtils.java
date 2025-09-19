package at.minecraft.pugna.utils;

import at.minecraft.pugna.Pugna;
import at.minecraft.pugna.config.GameConfig;

public final class BorderUtils {
    private BorderUtils() {}

    /* === Queries === */

    public static double calculateBorderSize() {
        if (GameConfig.getSeconds() > GameConfig.getBorderShrinkEndSeconds()) {
            return GameConfig.getBorderEndSize();
        }

        double baseSize = GameConfig.getBorderBaseSize();
        double sizePerPlayer = GameConfig.getBorderSizePerPlayer();

        return Math.min(GameConfig.getMaxBorderSize(), baseSize + (sizePerPlayer * PlayerUtils.getAlivePlayersCount()));
    }
}
