package com.stumblePillars.command;


import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Arrays;
import java.util.List;

public class ArenaCreateCommand extends CommonCommand {

    public ArenaCreateCommand(StumblePillars pl) {
        super("arena", "pillars.arena.create", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        final List<String> MODES = Arrays.asList("NORMAL","RANDOM");
        manager.command(builder.literal("create").required("mode",StringParser.stringParser(), SuggestionProvider.suggestingStrings(MODES)).handler(
                commandContext -> {
                    Player player = (Player) commandContext.sender();
                    String arenaName = player.getWorld().getName();
                    String gameMode = commandContext.get("mode");
                    Game game = getPlugin().getGameManager().createGame(arenaName, player.getWorld(),gameMode);
                    getPlugin().getGameManager().focus(player,getPlugin().getGameManager().getGame(arenaName).get());
                    player.sendMessage("Arena " + arenaName + " criada com sucesso!");
                }
        ));

    }
}
