package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;
import java.util.Collection;

public class CommandService {

    private Collection<CommonCommand> commandCollection = new ArrayList<>();
    private StumblePillars pl;
    private PaperCommandManager<CommandSourceStack> commandManager;

    public CommandService(StumblePillars pl) {
        this.pl = pl;
        this.commandManager = pl.getCommandManager();
    }

    public void init(){
        commandCollection.add(new JoinCommand(pl));
        commandCollection.add(new ArenaCreateCommand(pl));
        commandCollection.add(new Pos1Command(pl));
        commandCollection.add(new EditCommand(pl));
        commandCollection.add(new ArenasCommand(pl));
        commandCollection.add(new TestCommand(pl));
        commandCollection.add(new AddSpawnCommand(pl));
        commandCollection.add(new SetLobbyCommand(pl));

        for (CommonCommand command : commandCollection){

            command.construct(commandManager);
            Bukkit.getLogger().info("Comando inicializado com sucesso!");
        }
    }

}
