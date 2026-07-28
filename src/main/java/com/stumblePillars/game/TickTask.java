package com.stumblePillars.game;

public class TickTask {

    private int countdown;
    private Runnable runnable;
    private int count;


    public TickTask(int countdown, Runnable runnable){
        this.countdown = countdown;
        this.runnable = runnable;
    }

    public void tick(){
        count++;

        if (count >= countdown){
            count = 0;
            runnable.run();
        }

    }

}
