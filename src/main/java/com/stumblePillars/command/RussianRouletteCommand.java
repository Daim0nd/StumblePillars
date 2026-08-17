package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class RussianRouletteCommand extends CommonCommand{
    public RussianRouletteCommand(StumblePillars pl) {
        super("arena", "pillars.arena.russian", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("setRussianRouletteLoc").handler(commandContext -> {
            Player player = (Player) commandContext.sender();


            if (!getPlugin().getGameManager().isFocusing(player)) {
                player.sendMessage("Você não está focando em nenhum jogo!");
                return;
            }
            Game game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
            game.setRussianRouletteLoc(player.getLocation());
            player.sendMessage("Localização da roleta russa definida com sucesso!");
        }));
    }
}
