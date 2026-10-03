package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.Game;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class SetBorderLocCommand extends CommonCommand{
    public SetBorderLocCommand(StumblePillars pl) {
        super("arena", "pillars.arena.border", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("borderLoc").handler(commandContext -> {
            Player player = (Player) commandContext.sender();
            if (!getPlugin().getGameManager().isFocusing(player)) {
                player.sendMessage("Você não está focando em nenhum jogo!");
                return;
            }
            Game game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
            game.setBorderLocation(player.getLocation());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Borda setada com sucesso!</green>"));
            ArenaSettingsPanel.showFocused(getPlugin(), player);
        }));
    }
}

