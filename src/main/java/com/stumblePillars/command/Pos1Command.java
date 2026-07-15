package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.GameFolder;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;

public class Pos1Command extends CommonCommand{

    public Pos1Command(StumblePillars pl) {
        super("arena", "pillars.arena.pos1", true, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.literal("pos1").handler(commandContext -> {
            if (!(commandContext.sender().getSender() instanceof Player player)) return;
            StumblePillars pl = getPlugin();
            if (!pl.getGameManager().getGameFocusMap().containsKey(player.getUniqueId())){
                player.sendMessage("Você não está editando nenhuma arena!");
                return;
            }
            Game game = pl.getGameManager().getGameFocusMap().get(player.getUniqueId());
            Location location = player.getLocation();
            game.setPos1(location);
            player.sendMessage(Color.BLACK + "Pos1 setada com sucesso!");

        }));

    }
}
