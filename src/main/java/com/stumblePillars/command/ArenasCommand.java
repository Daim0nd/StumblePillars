package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.List;

public class ArenasCommand extends CommonCommand{
    public ArenasCommand(StumblePillars pl) {
        super("arena","", true, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.handler(commandContext -> {
            List<Game> games = getPlugin().getGameManager().getGames();
            games.forEach(game -> {
                commandContext.sender().getSender().sendMessage(game.getName());
            });
        }));

    }
}
