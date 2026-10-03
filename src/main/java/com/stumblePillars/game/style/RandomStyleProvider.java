package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import java.util.Random;

public class RandomStyleProvider {
    private final StumblePillars plugin;
    private final Game game;
    private final Random random = new Random();
    public RandomStyleProvider(StumblePillars plugin, Game game) { this.plugin = plugin; this.game = game; }
    public GameStyle tryYourLuck() {
        StyleType[] styles = StyleType.values();
        return styles[random.nextInt(styles.length)].create(plugin, game);
    }
}
