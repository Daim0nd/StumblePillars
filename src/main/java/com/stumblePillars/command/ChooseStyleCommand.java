package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.GameState;
import com.stumblePillars.game.RandomMode;
import com.stumblePillars.game.style.StyleType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import java.util.Arrays;

public class ChooseStyleCommand extends CommonCommand {
    public ChooseStyleCommand(StumblePillars plugin) { super("chooseStyle", "pillars.choose_style", false, plugin); }
    @Override public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.required("style", StringParser.stringParser(), SuggestionProvider.suggestingStrings(Arrays.stream(StyleType.values()).map(style -> style.id).toList())).handler(context -> {
            Player player = (Player) context.sender();
            var selected = getPlugin().getGameManager().getGame(player);
            if (selected.isEmpty() || !selected.get().isWaitingForPlayers() || !(selected.get().getMode() instanceof RandomMode)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Entre em uma arena RANDOM ou VOTE aguardando jogadores.</red>"));
                return;
            }
            String value = context.get("style");
            var style = Arrays.stream(StyleType.values()).filter(type -> type.id.equalsIgnoreCase(value)).findFirst();
            if (style.isEmpty()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Estilo inválido.</red>"));
                return;
            }
            var game = selected.get();
            game.setGameStyle(style.get().create(getPlugin(), game), true);
            game.broadcastPlayers(MiniMessage.miniMessage().deserialize("<gold>A administração escolheu o estilo: <style>.</gold>", Placeholder.unparsed("style", style.get().label)));
        }));
    }
}
