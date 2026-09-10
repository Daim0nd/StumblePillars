package com.stumblePillars.configuration;

import com.stumblePillars.StumblePillars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MessagesConfig {

    private File file;
    private FileConfiguration fileConfiguration;
    private StumblePillars pl;

    public static String SET_LOBBY = "<green>Você setou o lobby com sucesso!</green>";
    public static String GAME_JOIN = "<green>Você entrou no jogo!</green>";
    public static String GAME_NOT_EXISTS = "<red>Esse jogo não existe!</red>";
    public static String INCOMPLETE_GAME = "<red>Você não pode entrar nesse jogo!</red>";
    public static String GAME_FULL = "<red>O jogo está cheio!</red>";
    public static String GAME_ALREADY_STARTED = "<red>O jogo já começou ou está reconstruindo!</red>";
    public static String PLAYER_JOINED = "<green>{player} entrou no jogo! (<aqua>{current}</aqua>/<aqua>{max}</aqua>)</green>";
    public static String PLAYER_LEFT = "<red>{player} saiu do jogo! (<aqua>{current}</aqua>/<aqua>{max}</aqua>)</red>";
    public static String GAME_WILL_START = "<yellow>O jogo começará em {seconds} segundos!</yellow>";
    public static String GAME_COUNTDOWN = "<gold>⏱ Jogo começando em {seconds}...</gold>";
    public static String GAME_COUNTDOWN_CANCELLED = "<red>Jogadores insuficientes. Countdown cancelado!</red>";
    public static String GAME_STARTED = "<green>✓ Jogo iniciado!</green>";
    public static String GAME_NOT_ENOUGH_PLAYERS = "<red>Não há jogadores suficientes!</red>";
    public static String GAME_START_TEXT = "<green><bold>GAME START!</bold></green>";
    public static String SET_BORDER_SIZE = "<green>Tamanho da borda setado para: {size}</green>";
    public static String WIN_MESSAGE = "<green>Parabéns {player}!</green>";
    public static String SEARCHING_GAME = "<gray> Procurando jogo disponível!</gray>";
    public static String BLOCKED_COMMAND = "<gray> Você não pode executar esse comando! </gray>";
    public static String PLAYER_DIED = "<gray> O player <red>{player}</red> morreu! </gray>";
    public static String ALREADY_PLAYING = "<red> Você já está jogando!</red>";

    public static String GAME_SCOREBOARD_TITLE = "Pillars";
    public static List<String> GAME_SCOREBOARD = Arrays.asList("  Pillars  ","","  Cringe  ","");

    public MessagesConfig(StumblePillars pl) {
        this.pl = pl;
    }

    public void load(){
        file = new File(pl.getDataFolder(),"messages.yml");
        try{
            if (!file.exists()){
                file.createNewFile();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfiguration.options().copyDefaults(true);

        loadMessages();

    }

    private void loadMessages(){
        SET_LOBBY = addDefault("set_lobby",SET_LOBBY);
        GAME_JOIN = addDefault("game_join",GAME_JOIN);
        GAME_NOT_EXISTS = addDefault("game_not_exists",GAME_NOT_EXISTS);
        INCOMPLETE_GAME = addDefault("incomplete_game",INCOMPLETE_GAME);
        GAME_FULL = addDefault("game_full",GAME_FULL);
        GAME_ALREADY_STARTED = addDefault("game_already_started",GAME_ALREADY_STARTED);
        PLAYER_JOINED = addDefault("player_joined",PLAYER_JOINED);
        PLAYER_LEFT = addDefault("player_left",PLAYER_LEFT);
        GAME_WILL_START = addDefault("game_will_start",GAME_WILL_START);
        GAME_COUNTDOWN = addDefault("game_countdown",GAME_COUNTDOWN);
        GAME_COUNTDOWN_CANCELLED = addDefault("game_countdown_cancelled",GAME_COUNTDOWN_CANCELLED);
        GAME_STARTED = addDefault("game_started",GAME_STARTED);
        GAME_NOT_ENOUGH_PLAYERS = addDefault("game_not_enough_players",GAME_NOT_ENOUGH_PLAYERS);
        GAME_START_TEXT = addDefault("game_start_text",GAME_START_TEXT);
        SET_BORDER_SIZE = addDefault("set_border_size",SET_BORDER_SIZE);
        WIN_MESSAGE = addDefault("win_message",WIN_MESSAGE);
        SEARCHING_GAME = addDefault("searching_game",SEARCHING_GAME);
        BLOCKED_COMMAND = addDefault("blocked_command",BLOCKED_COMMAND);
        PLAYER_DIED = addDefault("playerd_died",PLAYER_DIED);
        ALREADY_PLAYING = addDefault("already_playing",ALREADY_PLAYING);

        GAME_SCOREBOARD_TITLE = addDefault("game_scoreboard_title",GAME_SCOREBOARD_TITLE);
        GAME_SCOREBOARD = addDefault("game_scoreboard",GAME_SCOREBOARD);
        save();
    }

    private String addDefault(String path,String message){
        fileConfiguration.addDefault(path,message);
        return fileConfiguration.getString(path);
    }

    public void reloadMessages(){
        loadMessages();
    }

    private List<String> addDefault(String path,List<String> list){
        fileConfiguration.addDefault(path,list);
        return fileConfiguration.getStringList(path);
    }

    public void save(){
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
