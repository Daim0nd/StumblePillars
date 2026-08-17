package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EditCommand extends CommonCommand {
    public EditCommand(StumblePillars pl) {
        super("arena", "pillars.game.edit", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        List<Suggestion> s = getPlugin().getGameManager().getGames().stream().map(c -> Suggestion.suggestion(c.getName())).toList();
        manager.command(builder.literal("edit").required("name", StringParser.stringParser(
        ),SuggestionProvider.suggesting(s)).handler(commandContext -> {
            Player player = (Player) commandContext.sender();
            String gameName = commandContext.get("name");
            Optional<Game> opGame = getPlugin().getGameManager().getGame(gameName);

            if (opGame.isEmpty()){
                player.sendMessage("Esse jogo não existe!");
                return;
            }

            Game game = opGame.get();
            getPlugin().getGameManager().focus(player, game);
            player.sendMessage("Você está editando o jogo: " + gameName);
        }));


    }
}