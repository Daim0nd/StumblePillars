package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import java.util.List;
import java.util.Locale;

public class ArenaModeCommand extends CommonCommand {
    public ArenaModeCommand(StumblePillars plugin) { super("arena", "pillars.game.edit", false, plugin); }
    @Override public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.literal("mode").required("mode", StringParser.stringParser(), SuggestionProvider.suggestingStrings(List.of("NORMAL", "RANDOM", "VOTE"))).handler(context -> {
            Player player = (Player) context.sender();
            String value = ((String) context.get("mode")).toUpperCase(Locale.ROOT);
            if (!List.of("NORMAL", "RANDOM", "VOTE").contains(value)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Use NORMAL, RANDOM ou VOTE.</red>"));
                return;
            }
            var game = getPlugin().getGameManager().getGameFocusMap().get(player.getUniqueId());
            if (game == null || !game.setMode(value)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Selecione uma arena vazia e aguardando jogadores com /sp arena edit.</red>"));
                return;
            }
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Modo salvo!</green>"));
            ArenaSettingsPanel.showFocused(getPlugin(), player);
        }));
    }
}
