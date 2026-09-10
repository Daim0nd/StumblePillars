package com.stumblePillars.command;

import com.stumblePillars.ReloadHandler;
import com.stumblePillars.StumblePillars;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class ReloadCommand extends CommonCommand{
    public ReloadCommand(StumblePillars pl) {
        super("reload", "pillars.reload", true, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.optional("force", StringParser.stringParser()).handler(commandContext -> {
            String arg = commandContext.getOrDefault("force","");
            if (arg.equals("--force") || commandContext.sender() instanceof ConsoleCommandSender){
                getPlugin().getGameManager().reloadGames();
                getPlugin().getMessagesConfig().reloadMessages();
                commandContext.sender().sendMessage(MiniMessage.miniMessage().deserialize("<green> Plugin recarregado com sucesso!</green>"));

            }else{
                new ReloadHandler().handle(commandContext.sender());
            }
        }));
    }
}
