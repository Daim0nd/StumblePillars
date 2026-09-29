package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.GameState;
import com.stumblePillars.game.style.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Arrays;
import java.util.List;

public class ChooseStyleCommand extends CommonCommand{
    public ChooseStyleCommand(StumblePillars pl) {
        super("chooseStyle", "pillars.choose_style", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        List<Suggestion> tabComplete = Arrays.asList("Wormhole ","RussianRoulette","Potion","GraplinHook","Meteors").stream().map(s -> Suggestion.suggestion(s)).toList();
        manager.command(builder.required("style", StringParser.stringParser(), SuggestionProvider.suggesting(tabComplete)).handler(commandContext -> {
            Player player = (Player) commandContext.sender();
            if (getPlugin().getGameManager().getGame(player).isEmpty()) return;
            Game game = getPlugin().getGameManager().getGame(player).get();
            if (!game.getGameState().equals(GameState.WAITING)) return;
            String style = commandContext.get("style");
            switch (style){
                case "Wormhole": game.setGameStyle(new WormholeStyle(getPlugin(),game),true); break;
                case "RussianRoulette": game.setGameStyle(new RussianRouletteStyle(getPlugin(),game),true); break;
                case "Potion": game.setGameStyle(new RandomPotionStyle(getPlugin(),game),true); break;
                case "GraplinHook": game.setGameStyle(new GraplinHookStyle(getPlugin(),game),true); break;
                case "Meteors": game.setGameStyle(new MeteorStyle(getPlugin(),game),true);  break;
            }
        }));
    }
}
