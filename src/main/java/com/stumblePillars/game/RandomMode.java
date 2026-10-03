package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.style.GameStyle;
import com.stumblePillars.game.style.RandomStyleProvider;

public class RandomMode implements GameMode {

    private final StumblePillars pl;
    private GameStyle gameStyle;
    private TickTask tickTask;
    private boolean chosenByStaff;

    public RandomMode(StumblePillars pl) {
        this.pl = pl;
    }

    public GameStyle getCurrentStyle() {
        return gameStyle;
    }

    public void setGameStyle(GameStyle gameStyle){
        this.gameStyle = gameStyle;
    }

    @Override
    public void onStart(Game game) {
        if (!isChosenByStaff()){
            RandomStyleProvider randomStyleProvider = new RandomStyleProvider(pl, game);
            gameStyle = randomStyleProvider.tryYourLuck();
            if (gameStyle == null) return;
        }

        tickTask = new TickTask(gameStyle.getTickCooldown(), gameStyle::tick);
        gameStyle.onStart();

        if (tickTask.getCountdown() != 0) {
            pl.getTaskManager().register(tickTask);
        }
    }

    @Override
    public void onStop(Game game) {
        if (gameStyle != null) {
            gameStyle.onEnd();
        }
        gameStyle = null;
        chosenByStaff = false;
        if (tickTask != null) {
            pl.getTaskManager().remove(tickTask);
            tickTask = null;
        }
    }

    public boolean isChosenByStaff() {
        return chosenByStaff;
    }

    public void setChosenByStaff(boolean chosenByStaff) {
        this.chosenByStaff = chosenByStaff;
    }
}
