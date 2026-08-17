package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class SetGameLobbyCommand extends CommonCommand {

    public SetGameLobbyCommand(StumblePillars pl) {
        super("arena", "pillars.arena.set_game_lobby", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("setLobby").handler(commandContext -> {
            Player player = (Player) commandContext.sender();

            if (!getPlugin().getGameManager().isFocusing(player)) {
                player.sendMessage("Você não está focando em nenhum jogo!");
                return;
            }
            Game game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
            game.setWaitLobby(player.getLocation());
            player.sendMessage("Lobby do jogo definido com sucesso!");
        }));
    }
}
