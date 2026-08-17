package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;

public abstract class GameStyle {

    private StumblePillars pl;
    private Game game;

    public GameStyle(StumblePillars pl, Game game){
        this.pl = pl;
        this.game = game;
    }

    public abstract String getName();

    public abstract void tick();

    public abstract void onStart();

    public abstract void onEnd();

    public abstract int getTickCooldown();

    public StumblePillars getPlugin() {
        return pl;
    }

    public Game getGame(){
        return game;
    }
}
