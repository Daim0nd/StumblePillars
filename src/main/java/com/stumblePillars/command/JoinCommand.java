package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;

public class JoinCommand extends CommonCommand{

    public JoinCommand(StumblePillars pl) {
        super("join","pillars.game.join",false,pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(
                builder.handler(commandContext -> {
                    commandContext.sender().getSender().sendMessage("Teste");
                })
        );
    }
}
