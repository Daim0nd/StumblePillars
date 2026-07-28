package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;

public class SetLobbyCommand extends CommonCommand{

    public SetLobbyCommand(StumblePillars pl) {
        super("setLobby", "", false, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.handler(commandContext -> {
            if (!(commandContext.sender().getSender() instanceof Player player)) return;

            Location currentLocation = player.getLocation();
            getPlugin().setLobby(currentLocation);

            player.sendMessage(MessagesConfig.SET_LOBBY);

        }));

    }
}
