package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.TickTask;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class JoinCommand extends CommonCommand {

    public JoinCommand(StumblePillars pl) {
        super("join", "pillars.game.join", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(
                builder.optional("name", StringParser.stringParser()).handler(commandContext -> {
                    Player player = (Player) commandContext.sender();

                    String arenaName = commandContext.getOrDefault("name","");

                    if (arenaName.equals("")){
                        getPlugin().getGameManager().getPlayersWaiting().add(player.getUniqueId());
                        player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfig.SEARCHING_GAME));
                        return;
                    }

                    Optional<Game> opGame = getPlugin().getGameManager().getGame(arenaName);

                    if (opGame.isEmpty()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfig.GAME_NOT_EXISTS));
                        return;
                    }

                    Game game = opGame.get();
                    game.join(player);
                })
        );
    }
}
