package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

public class SetMinPlayersCommand extends CommonCommand{
    public SetMinPlayersCommand(StumblePillars pl) {
        super("arena","pillars.arena.min_players", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("setMinPlayers").required("amount", IntegerParser.integerParser()).handler(commandContext -> {
            Player player = (Player) commandContext.sender();

            StumblePillars pl = getPlugin();
            if (!pl.getGameManager().getGameFocusMap().containsKey(player.getUniqueId())){
                player.sendMessage("Você não está editando nenhuma arena!");
                return;
            }
            int amount = commandContext.get("amount");
            Game game = pl.getGameManager().getGameFocusMap().get(player.getUniqueId());
            game.setMinPlayers(amount);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green> Quantidade de jogadores mínimos setada com sucesso! </green>"));
        }));
    }
}
