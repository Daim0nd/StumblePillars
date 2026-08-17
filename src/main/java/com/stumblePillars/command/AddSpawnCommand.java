package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class AddSpawnCommand extends CommonCommand{
    public AddSpawnCommand(StumblePillars pl) {
        super("arena", "pillars.arena.add_spawn", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("addSpawn").handler(commandContext -> {
            Player player = (Player) commandContext.sender();

            if(!getPlugin().getGameManager().isFocusing(player)){
                player.sendMessage("Você não está focando em nenhum jogo!");
                return;
            }
            Game game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
            game.addSpawn(player.getLocation());
            player.sendMessage("Spawn adicionado com sucesso!");
        }));
    }
}
