package com.stumblePillars.fastboard;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.TickTask;
import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.entity.Player;


public class GameBoard extends FastBoard {

    private StumblePillars pl;
    private TickTask tickTask;

    public GameBoard(Player player, StumblePillars pl) {
        super(player);
        this.pl = pl;
        tickTask = new TickTask(5,this::update);

        update();
        updateTitle(pl.getMessagesConfig().text("game_scoreboard_title"));
        initUpdates();
    }

    private void update() {
        updateLines(pl.getMessagesConfig().textList("game_scoreboard", getPlayer()));
    }

    public void initUpdates(){
        pl.getTaskManager().register(tickTask);
    }

    @Override
    public synchronized void delete() {
        super.delete();
        pl.getTaskManager().remove(tickTask);
    }
}
