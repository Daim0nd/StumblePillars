package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.Game;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.Optional;
import java.util.UUID;

public class LeaveCommand extends CommonCommand{
    public LeaveCommand(StumblePillars pl) {
        super("leave", "sp.pillars.leave", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.handler(commandContext -> {
            Player player = (Player) commandContext.sender();

            Optional<Game> opGame = getPlugin().getGameManager().getGame(player);

            if (opGame.isEmpty()) {
                return;
            }

            Game game = opGame.get();
            UUID uuid = player.getUniqueId();
            if (game.getPlayers().contains(uuid)) game.leave(player);
            else game.removeSpectator(player,true);
        }));
    }
}
