package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

public abstract class CommonCommand {

    private String commandName;
    private String permission;
    private boolean allowConsole;
    private StumblePillars pl;

    public CommonCommand(String commandName, String permission, boolean allowConsole, StumblePillars pl) {
        this.commandName = commandName;
        this.permission = permission;
        this.allowConsole = allowConsole;
        this.pl = pl;
    }

    public abstract void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder);

    public void construct(PaperCommandManager<CommandSourceStack> manager){
        Command.Builder<CommandSourceStack> builder = manager.commandBuilder("sp","pillars").literal(commandName);

        if (!allowConsole){

        }
        construct(manager,builder);
    }

    public String getCommandName() {
        return commandName;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isAllowConsole() {
        return allowConsole;
    }

    public StumblePillars getPlugin() {
        return pl;
    }
}
