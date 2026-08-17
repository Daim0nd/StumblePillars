package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;

public class Timer {

    private StumblePillars pl;
    private TickTask timerTask;
    private int COUNTDOWN_DURATION;
    private int countdown;

    public Timer(StumblePillars pl,int COUNTDOWN_DURATION) {
        this.pl = pl;
        this.COUNTDOWN_DURATION = COUNTDOWN_DURATION;
        this.countdown = COUNTDOWN_DURATION;
    }

    public void start(Runnable runnable){
        this.timerTask = new TickTask(20,() -> {
            decrement();
            runnable.run();
        });
        pl.getTaskManager().register(timerTask);
    }

    public void stop(){
        if (timerTask == null) return;
        countdown = COUNTDOWN_DURATION;
        pl.getTaskManager().remove(timerTask);
    }

    private void decrement(){
        if (countdown <= 0) return;
        countdown--;
    }

    public int getCountdown() {
        return countdown;
    }
}
