package com.stumblePillars.fastboard;

import com.stumblePillars.configuration.MessagesConfig;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameBoard extends FastBoard {

    private String scoreboardTitle;
    private List<String> scoreboard;

    public GameBoard(Player player) {
        super(player);
        scoreboard = MessagesConfig.GAME_SCOREBOARD;
        scoreboardTitle = MessagesConfig.GAME_SCOREBOARD_TITLE;

        List<Component> components = scoreboard.stream().map(s -> MiniMessage.miniMessage().deserialize(s)).toList();
        updateLines(components);
        updateTitle(MiniMessage.miniMessage().deserialize(scoreboardTitle));
    }


}
