package com.stumblePillars.fastboard;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.TickTask;
import com.stumblePillars.util.PlaceholderUtil;
import fr.mrmicky.fastboard.adventure.FastBoard;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class GameBoard extends FastBoard {

    private String scoreboardTitle;
    private List<String> scoreboard;
    private StumblePillars pl;
    private TickTask tickTask;

    public GameBoard(Player player, StumblePillars pl) {
        super(player);
        this.pl = pl;
        scoreboard = MessagesConfig.GAME_SCOREBOARD;
        scoreboardTitle = MessagesConfig.GAME_SCOREBOARD_TITLE;
        tickTask = new TickTask(5,this::update);

        List<Component> components = scoreboard.stream().map(s -> MiniMessage.miniMessage().deserialize(PlaceholderUtil.apply(s,getPlayer(),pl))).toList();
        updateLines(components);
        updateTitle(MiniMessage.miniMessage().deserialize(scoreboardTitle));
        initUpdates();
    }

    private void update() {
        List<Component> components = scoreboard.stream()
                .map(s -> MiniMessage.miniMessage()
                        .deserialize(PlaceholderUtil.apply(s, getPlayer(), pl)))
                .toList();

        updateLines(components);
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
