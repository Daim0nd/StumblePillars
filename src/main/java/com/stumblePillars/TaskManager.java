package com.stumblePillars;

import com.stumblePillars.game.TickTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TaskManager {

    private StumblePillars pl;
    private ConcurrentLinkedDeque<TickTask> tickTaskList = new ConcurrentLinkedDeque<>();

    public TaskManager(StumblePillars pl) {
        this.pl = pl;
    }

    public void register(TickTask tickTask){
        tickTaskList.add(tickTask);
    }

    public void remove(TickTask tickTask){
        tickTaskList.remove(tickTask);
    }

    public void startMainTask(){
        new BukkitRunnable(){
            @Override
            public void run() {

                tickTaskList.forEach(TickTask::tick);

            }
        }.runTaskTimer(pl,0l,1l);
    }

}
