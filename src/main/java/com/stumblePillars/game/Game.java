package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.GameConfig;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {

    private StumblePillars pl;
    private GameLoader gameLoader;
    private GameState gameState = GameState.WAITING;
    private List<UUID> players = new ArrayList<>();
    private GameConfig gameConfig;
    private FileConfiguration config;

    private String name;
    private String displaName;
    private int maxPlayers;
    private int minPlayers;

    public Game(String name,StumblePillars pl) {
        this.name = name;
        this.gameConfig = new GameConfig(name, pl);
        this.config = gameConfig.getConfig();
    }

    public void setPos1(Location location){
        config.set("pos1",location);
        gameConfig.save();
    }


    public void join(Player player){
        UUID uuid = player.getUniqueId();
        players.add(uuid);
    }


    public String getName() {
        return name;
    }

    public String getDisplaName() {
        return displaName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

}
