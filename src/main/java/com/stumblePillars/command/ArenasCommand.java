package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.List;

public class ArenasCommand extends CommonCommand{
    public ArenasCommand(StumblePillars pl) {
        super("arena","pillars.arena.arenas", true, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.handler(commandContext -> {
            List<Game> games = getPlugin().getGameManager().getGames();
            games.forEach(game -> {
                commandContext.sender().sendMessage(game.getName());
            });
        }));

    }
}
