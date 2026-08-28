package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class SetSpectatorSpawnCommand extends CommonCommand{
    public SetSpectatorSpawnCommand(StumblePillars pl) {
        super("arena", "pillars.arena.spectator",false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("spectatorSpawn").handler(commandContext -> {
            Player player = (Player) commandContext.sender();
            if (!getPlugin().getGameManager().isFocusing(player)) {
                player.sendMessage("Você não está focando em nenhum jogo!");
                return;
            }
            Game game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
            game.setSpectatorSpawn(player.getLocation());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Localização de spawn de espectadores setada com sucesso!</green>"));
        }));
    }
}
