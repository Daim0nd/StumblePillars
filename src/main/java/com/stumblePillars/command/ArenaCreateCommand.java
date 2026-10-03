package com.stumblePillars.command;


import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Arrays;
import java.util.List;

public class ArenaCreateCommand extends CommonCommand {

    public ArenaCreateCommand(StumblePillars pl) {
        super("arena", "pillars.arena.create", false, pl);
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        final List<String> MODES = Arrays.asList("NORMAL","RANDOM","VOTE");
        manager.command(builder.literal("create").required("mode",StringParser.stringParser(), SuggestionProvider.suggestingStrings(MODES)).handler(
                commandContext -> {
                    Player player = (Player) commandContext.sender();
                    String arenaName = player.getWorld().getName();
                    String gameMode = ((String) commandContext.get("mode")).toUpperCase(java.util.Locale.ROOT);
                    if (!MODES.contains(gameMode)) {
                        player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>Use NORMAL, RANDOM ou VOTE.</red>"));
                        return;
                    }
                    if (getPlugin().getGameManager().getGame(arenaName).isPresent()) {
                        player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>Já existe uma arena neste mundo. Use /sp arena edit.</red>"));
                        return;
                    }
                    Game game = getPlugin().getGameManager().createGame(arenaName, player.getWorld(),gameMode);
                    game.setMode(gameMode);
                    getPlugin().getGameManager().focus(player,game);
                    player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<green>Arena criada com sucesso!</green>"));
                    ArenaSettingsPanel.showFocused(getPlugin(), player);
                }
        ));

    }
}

