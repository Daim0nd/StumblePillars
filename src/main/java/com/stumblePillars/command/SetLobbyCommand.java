package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

public class SetLobbyCommand extends CommonCommand{

    public SetLobbyCommand(StumblePillars pl) {
        super("setLobby", "pillars.lobby", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.handler(commandContext -> {
            Player player = (Player) commandContext.sender();

            Location currentLocation = player.getLocation();
            getPlugin().setLobby(currentLocation);

            player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(MessagesConfig.SET_LOBBY));

            ArenaSettingsPanel.showFocused(getPlugin(), player);
        }));

    }
}

