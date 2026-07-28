package com.stumblePillars.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.sun.jdi.connect.Connector;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class ArenaCreateCommand extends CommonCommand {

    public ArenaCreateCommand(StumblePillars pl) {
        super("arena", "pillars.arena", false, pl);
    }

    @Override
    public void construct(PaperCommandManager<CommandSourceStack> manager, Command.Builder<CommandSourceStack> builder) {
        manager.command(builder.literal("create").required("name", StringParser.stringParser()).handler(
                commandContext -> {
                    if (!(commandContext.sender().getSender() instanceof Player player)) return;
                    String arenaName = commandContext.get("name");
                    player.sendMessage(arenaName);
                    Game game = getPlugin().getGameManager().createGame(arenaName);
                    game.setWord(player.getWorld());
                    getPlugin().getGameManager().focus(player,getPlugin().getGameManager().getGame(arenaName).get());
                }
        ));

    }
}
