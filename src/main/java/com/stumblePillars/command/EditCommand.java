package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

public class EditCommand extends CommonCommand {
    public EditCommand(StumblePillars pl) {
        super("arena", "", false, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.literal("edit").required("name", StringParser.stringParser(), SuggestionProvider.suggesting(Suggestion.suggestion("A"))).handler(commandContext -> {
            if (!(commandContext.sender().getSender() instanceof Player player)) return;
            String gameName = commandContext.get("name");
            Game game = getPlugin().getGameManager().getGame(gameName).get();
            getPlugin().getGameManager().focus(player,game);
            player.sendMessage("Você está editando o jogo: " + gameName);
        }));


    }
}