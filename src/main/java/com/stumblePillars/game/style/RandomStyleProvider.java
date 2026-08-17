package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomStyleProvider {

    private final List<GameStyle> gameStyles = new ArrayList<>();
    private final Random random = new Random();

    public RandomStyleProvider(StumblePillars pl, Game game) {
       gameStyles.add(new MeteorStyle(pl, game));
       gameStyles.add(new WormholeStyle(pl,game));
       gameStyles.add(new GraplinHookStyle(pl,game));
       gameStyles.add(new AcidRainStyle(pl,game));
       gameStyles.add(new RandomPotionStyle(pl,game));
       gameStyles.add(new RussianRouletteStyle(pl,game));
    }

    public GameStyle tryYourLuck(){
        int size = gameStyles.size();
        if (size == 0) return null;
        int luckyNumber = random.nextInt(size);
        return gameStyles.get(luckyNumber);
    }

}
