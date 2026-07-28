package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

public class SetSpawnsCommand extends CommonCommand{

    public SetSpawnsCommand(StumblePillars pl) {
        super("arena", "", false, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.literal("setSpawn").required("",IntegerParser.integerParser()).handler(commandContext -> {
            if (!(commandContext.sender().getSender() instanceof Player player)) return;

            Location currentLocation = player.getLocation();

        }));
    }
}
