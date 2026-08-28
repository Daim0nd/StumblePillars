package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.Game;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.DoubleParser;

public class SetBorderSizeCommand extends CommonCommand{
    public SetBorderSizeCommand(StumblePillars pl) {
        super("arena", "pillars.arena.border", true, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("borderSize")
                .required("size", DoubleParser.doubleParser())
                .handler(commandContext -> {
                    Player player = (Player) commandContext.sender();
                    double size = commandContext.get("size");
                    if(size <= 0){
                        player.sendMessage("O tamanho da borda não pode ser menor ou igual a zero!");
                        return;
                    }
                    if(!getPlugin().getGameManager().isFocusing(player)){
                        player.sendMessage("Você não está focando em nenhum jogo!");
                        return;
                    }
                    Game game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
                    game.setBorderSize(size);
                    player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfig.SET_BORDER_SIZE.replace("{size}",String.valueOf(size))));
        }));
    }
}
