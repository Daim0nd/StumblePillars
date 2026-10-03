package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public final class ArenaSettingsPanel {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private ArenaSettingsPanel() {}
    public static void showFocused(StumblePillars plugin, Player player) {
        Game game = plugin.getGameManager().getGameFocusMap().get(player.getUniqueId());
        if (game != null) show(plugin, player, game);
    }
    public static void show(StumblePillars plugin, Player player, Game game) {
        var config = game.getArenaConfig();
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>\n<gold><bold>Configuração: <arena></bold></gold>", Placeholder.unparsed("arena", game.getName())));
        row(player, "Modo", game.getModeName(), true, "/sp arena mode ", true, "pillars.game.edit", "Escolha NORMAL, RANDOM ou VOTE. A arena deve estar vazia.");
        row(player, "Mínimo de jogadores", String.valueOf(game.getMinPlayers()), game.getMinPlayers() > 0 && game.getMinPlayers() < game.getMaxPlayers(), "/sp arena setMinPlayers ", true, "pillars.arena.min_players", "Digite o mínimo de jogadores.");
        row(player, "Máximo de jogadores", String.valueOf(game.getMaxPlayers()), game.getMaxPlayers() > game.getMinPlayers(), "/sp arena setMaxPlayers ", true, "pillars.arena.max_players", "Digite o máximo de jogadores.");
        row(player, "Spawns", String.valueOf(game.getSpawnCount()), game.getSpawnCount() >= game.getMinPlayers() && game.getSpawnCount() > 0, "/sp arena addSpawn", false, "pillars.arena.add_spawn", "Adiciona sua posição atual como spawn.");
        location(player, "Lobby da arena", config.getString("waitLobby"), "/sp arena setLobby", "pillars.arena.set_game_lobby");
        row(player, "Lobby principal", plugin.isLobbyEnable() ? describe(plugin.getLobby()) : "Pendente", plugin.isLobbyEnable(), "/sp setLobby", false, "pillars.lobby", "Define o lobby principal na sua posição atual.");
        location(player, "Centro da borda", config.getString("worldBorder.borderLoc"), "/sp arena borderLoc", "pillars.arena.border");
        row(player, "Tamanho da borda", String.valueOf(config.getDouble("worldBorder.size")), config.getDouble("worldBorder.size") > 0, "/sp arena borderSize ", true, "pillars.arena.border", "Digite o tamanho da borda em blocos.");
        location(player, "Roleta russa", config.getString("russianRouletteLoc"), "/sp arena setRussianRouletteLoc", "pillars.arena.russian");
        location(player, "Espectadores", config.getString("spectatorSpawn"), "/sp arena spectatorSpawn", "pillars.arena.spectator");
        player.sendMessage(MM.deserialize(game.isConfigured() ? "<green>✔ Arena pronta para jogar.</green>" : "<yellow>⚠ Complete as configurações pendentes.</yellow>"));
        player.sendMessage(MM.deserialize("<gray>Os botões alteram a arena em edição. Posicione-se antes de definir um local.</gray>"));
    }
    private static String describe(org.bukkit.Location location) {
        return location.getWorld().getName() + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }
    private static void location(Player player, String label, String value, String command, String permission) {
        boolean valid = com.stumblePillars.util.LocationUtil.isLocation(value);
        String display = "Pendente";
        if (valid) {
            String[] parts = value.split(";");
            display = parts[0] + " (" + parts[1] + ", " + parts[2] + ", " + parts[3] + ")";
        }
        row(player, label, display, valid, command, false, permission, "Define este local na sua posição atual.");
    }
    private static void row(Player player, String label, String value, boolean valid, String command, boolean suggest, String permission, String help) {
        Component line = MM.deserialize("<gray><label>: </gray>" + (valid ? "<green>" : "<red>") + "<value></" + (valid ? "green" : "red") + "> ", Placeholder.unparsed("label", label), Placeholder.unparsed("value", value));
        Component button = MM.deserialize(player.hasPermission(permission) ? "<yellow>[Configurar]</yellow>" : "<dark_gray>[Sem permissão]</dark_gray>")
                .hoverEvent(HoverEvent.showText(Component.text(help)));
        if (player.hasPermission(permission)) button = button.clickEvent(suggest ? ClickEvent.suggestCommand(command) : ClickEvent.runCommand(command));
        player.sendMessage(line.append(button));
    }
}
