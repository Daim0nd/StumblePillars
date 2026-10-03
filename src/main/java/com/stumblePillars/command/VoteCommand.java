package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.VoteMode;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class VoteCommand extends CommonCommand {
    public VoteCommand(StumblePillars plugin) { super("vote", "pillars.game.join", false, plugin); }
    @Override public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.handler(context -> {
            Player player = (Player) context.sender();
            var game = getPlugin().getGameManager().getGame(player);
            if (game.isEmpty() || !(game.get().getMode() instanceof VoteMode vote) || !vote.open(player))
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Não há votação aberta para você.</red>"));
        }));
    }
}
