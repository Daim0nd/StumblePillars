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

    public static String SET_LOBBY = "Você setou o lobby com sucesso!";
    public static String GAME_JOIN = "Você entrou no jogo!";
    public static String GAME_NOT_EXISTS = "Esse jogo não existe!";
    public static String INCOMPLETE_GAME = "Você não pode entrar nesse jogo!";
    public static String GAME_FULL = "§cO jogo está cheio!";
    public static String GAME_ALREADY_STARTED = "§cO jogo já começou ou está reconstruindo!";
    public static String PLAYER_JOINED = "§a{player} entrou no jogo! (§b{current}§a/§b{max}§a)";
    public static String PLAYER_LEFT = "§c{player} saiu do jogo! (§b{current}§c/§b{max}§c)";
    public static String GAME_WILL_START = "§eO jogo começará em {seconds} segundos!";
    public static String GAME_COUNTDOWN = "§6⏱ Jogo começando em {seconds}...";
    public static String GAME_COUNTDOWN_CANCELLED = "§cJogadores insuficientes. Countdown cancelado!";
    public static String GAME_STARTED = "§a✓ Jogo iniciado!";
    public static String GAME_NOT_ENOUGH_PLAYERS = "§cNão há jogadores suficientes!";
    public static String GAME_START_TEXT = "§a§lGAME START!";

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

        GAME_SCOREBOARD_TITLE = addDefault("game_scoreboard_title",GAME_SCOREBOARD_TITLE);
        GAME_SCOREBOARD = addDefault("game_scoreboard",GAME_SCOREBOARD);

        save();


    }

    private String addDefault(String path,String message){
        fileConfiguration.addDefault(path,message);
        return fileConfiguration.getString(path);
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
