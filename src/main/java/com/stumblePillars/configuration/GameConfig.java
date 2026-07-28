package com.stumblePillars.configuration;

import com.stumblePillars.StumblePillars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GameConfig {

    private String name;
    private StumblePillars pl;
    private File file;
    private FileConfiguration fileConfiguration;

    public GameConfig(String name, StumblePillars pl){
        this.name = name;
        this.pl = pl;
        load();
    }

    private void load(){
        file = new File(pl.getGamesFolder().getFile(),name + ".yml");

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        createSection("displayName");
        createSection("world");
        createSection("maxPlayers");
        createSection("minPlayers");
        createSection("waitLobby");
        createSection("gameStartCountdown");
        createSection("pos1");
        createSection("pos2");
        createSection("spawns");

        save();
    }

    private void createSection(String path){
        if (!fileConfiguration.isSet(path)) {
            fileConfiguration.createSection(path);
        }
    }

    public void save(){
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getConfig() {
        return fileConfiguration;
    }

    public String getName() {
        return name;
    }
}
