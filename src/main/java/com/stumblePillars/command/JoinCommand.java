package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.TickTask;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class JoinCommand extends CommonCommand{

    public JoinCommand(StumblePillars pl) {
        super("join","pillars.game.join",false,pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(
                builder.required("name", StringParser.stringParser()).handler(commandContext -> {
                    Player player = (Player) commandContext.sender();

                    String arenaName = commandContext.get("name");
                    Optional<Game> opGame = getPlugin().getGameManager().getGame(arenaName);

                    if (opGame.isEmpty()){
                        player.sendMessage(Component.text(MessagesConfig.GAME_NOT_EXISTS));
                        return;
                    }

                    Game game = opGame.get();
                    game.join(player);

                })
        );
    }
}
