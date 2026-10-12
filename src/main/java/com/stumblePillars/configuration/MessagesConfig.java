package com.stumblePillars.configuration;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.util.PlaceholderUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessagesConfig {

    private File file;
    private FileConfiguration fileConfiguration;
    private final StumblePillars pl;
    private TagResolver colors;

    public static String SET_LOBBY = "<succes>Você setou o lobby com sucesso!</succes>";
    public static String GAME_JOIN = "<succes>Você entrou no jogo!</succes>";
    public static String GAME_NOT_EXISTS = "<error>Esse jogo não existe!</error>";
    public static String INCOMPLETE_GAME = "<error>Você não pode entrar nesse jogo!</error>";
    public static String GAME_FULL = "<error>O jogo está cheio!</error>";
    public static String GAME_ALREADY_STARTED = "<error>O jogo já começou ou está reconstruindo!</error>";
    public static String PLAYER_JOINED = "<succes><player> entrou no jogo! (<primary><current></primary>/<primary><max></primary>)</succes>";
    public static String PLAYER_LEFT = "<error><player> saiu do jogo! (<primary><current></primary>/<primary><max></primary>)</error>";
    public static String GAME_WILL_START = "<primary>O jogo começará em <seconds> segundos!</primary>";
    public static String GAME_COUNTDOWN = "<acent>⏱ Jogo começando em <seconds>...</acent>";
    public static String GAME_COUNTDOWN_CANCELLED = "<error>Jogadores insuficientes. Countdown cancelado!</error>";
    public static String GAME_STARTED = "<succes>✓ Jogo iniciado!</succes>";
    public static String GAME_NOT_ENOUGH_PLAYERS = "<error>Não há jogadores suficientes!</error>";
    public static String GAME_START_TEXT = "<succes><bold>GAME START!</bold></succes>";
    public static String SET_BORDER_SIZE = "<succes>Tamanho da borda setado para: <size></succes>";
    public static String WIN_MESSAGE = "<succes>Parabéns <player>!</succes>";
    public static String SEARCHING_GAME = "<primary>Procurando jogo disponível!</primary>";
    public static String BLOCKED_COMMAND = "<primary>Você não pode executar esse comando!</primary>";
    public static String PLAYER_DIED = "<primary>O player <error><player></error> morreu!</primary>";
    public static String ALREADY_PLAYING = "<error>Você já está jogando!</error>";

    public static String GAME_SCOREBOARD_TITLE = "<b><primary>PILLARS</primary></b>";
    public static List<String> GAME_SCOREBOARD = Arrays.asList(
            "",
            "<acent> ┇</acent> <primary>Modo de Jogo</primary>: <gameMode>",
            "<acent> ┇</acent> <primary>Jogadores: </primary><gameSize>\n" +
                    "  / <maxPlayers>",
            "<acent> ┇</acent> <primary>Tempo: </primary><succes><countdown></succes>",
            "",
            "<primary>yourserver.com</primary>"

    );

    public MessagesConfig(StumblePillars pl) {
        this.pl = pl;
    }

    private void loadColors() {
        addDefault("pallete.primary","#16c8f6");
        addDefault("pallete.succes","#c5e3f8");
        addDefault("pallete.error","#f80314");
        addDefault("pallete.acent","#079ef9");
        save();

        TagResolver.Builder builder = TagResolver.builder();
        Map<String, String> colorMap = Map.of("primary","#16c8f6","succes","#c5e3f8","error","#f80314","acent","#079ef9");
        for (var entry : colorMap.entrySet()){
            TextColor textColor = TextColor.fromHexString(fileConfiguration.getString("palette." + entry.getKey(),entry.getValue()));
            if (textColor == null) throw new IllegalArgumentException("O formato de cor hex(" + entry.getValue() + ") não existe!");
            builder.resolver(TagResolver.resolver(entry.getKey(), Tag.styling(textColor)));
        }
        colors = builder.build();
    }

    public Component text(String text, Object... values){
        return render(fileConfiguration.getString(text), values);
    }

    public List<Component> textList(String path, Player player) {
        return fileConfiguration.getStringList(path).stream()
                .map(line -> render(PlaceholderUtil.apply(line, player, pl)))
                .toList();
    }

    private Component render(String message, Object... values) {
        if (values.length % 2 != 0) throw new IllegalArgumentException("Os placeholders devem ser pares de nome e valor");
        TagResolver.Builder builder = TagResolver.builder().resolver(colors);
        for (int i = 0; i < values.length; i += 2) {
            String name = values[i].toString();
            message = message.replace("{" + name + "}", "<" + name + ">");
            builder.resolver(TagResolver.resolver(name, Tag.inserting(Component.text(values[i+1].toString()))));
        }
        return MiniMessage.miniMessage().deserialize(message, builder.build());
    }

    public void load() {
        file = new File(pl.getDataFolder(), "messages.yml");

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfiguration.options().copyDefaults(true);

        loadMessages();
        loadColors();
        save();
    }

    public void reloadMessages() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        loadMessages();
        loadColors();
    }

    private void loadMessages() {
        SET_LOBBY = addDefault("set_lobby", SET_LOBBY);
        GAME_JOIN = addDefault("game_join", GAME_JOIN);
        GAME_NOT_EXISTS = addDefault("game_not_exists", GAME_NOT_EXISTS);
        INCOMPLETE_GAME = addDefault("incomplete_game", INCOMPLETE_GAME);
        GAME_FULL = addDefault("game_full", GAME_FULL);
        GAME_ALREADY_STARTED = addDefault("game_already_started", GAME_ALREADY_STARTED);
        PLAYER_JOINED = addDefault("player_joined", PLAYER_JOINED);
        PLAYER_LEFT = addDefault("player_left", PLAYER_LEFT);
        GAME_WILL_START = addDefault("game_will_start", GAME_WILL_START);
        GAME_COUNTDOWN = addDefault("game_countdown", GAME_COUNTDOWN);
        GAME_COUNTDOWN_CANCELLED = addDefault("game_countdown_cancelled", GAME_COUNTDOWN_CANCELLED);
        GAME_STARTED = addDefault("game_started", GAME_STARTED);
        GAME_NOT_ENOUGH_PLAYERS = addDefault("game_not_enough_players", GAME_NOT_ENOUGH_PLAYERS);
        GAME_START_TEXT = addDefault("game_start_text", GAME_START_TEXT);
        SET_BORDER_SIZE = addDefault("set_border_size", SET_BORDER_SIZE);
        WIN_MESSAGE = addDefault("win_message", WIN_MESSAGE);
        SEARCHING_GAME = addDefault("searching_game", SEARCHING_GAME);
        BLOCKED_COMMAND = addDefault("blocked_command", BLOCKED_COMMAND);
        PLAYER_DIED = addDefault("player_died", PLAYER_DIED);
        ALREADY_PLAYING = addDefault("already_playing", ALREADY_PLAYING);

        GAME_SCOREBOARD_TITLE = addDefault("game_scoreboard_title", GAME_SCOREBOARD_TITLE);
        GAME_SCOREBOARD = addDefault("game_scoreboard", GAME_SCOREBOARD);
    }

    private String addDefault(String path, String text) {
        fileConfiguration.addDefault(path, text);
        return fileConfiguration.getString(path, text);
    }

    private List<String> addDefault(String path, List<String> list) {
        fileConfiguration.addDefault(path, list);
        return fileConfiguration.getStringList(path);
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return file;
    }
}
