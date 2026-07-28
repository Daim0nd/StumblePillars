package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;

public class AddSpawnCommand extends CommonCommand{
    public AddSpawnCommand(StumblePillars pl) {
        super("arena", "", false, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.literal("addSpawn").handler(commandContext -> {
            if (!(commandContext.sender().getSender() instanceof Player player)) return;

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
