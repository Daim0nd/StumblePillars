package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.GameConfig;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private StumblePillars pl;
    private List<GameConfig> gameConfigs = new ArrayList<>();
    private List<Game> games = new ArrayList<>();
    private HashMap<UUID, Game> gameFocusMap = new HashMap<>();

    public GameManager(StumblePillars pl) {
        this.pl = pl;
    }

    public void registerGames(){
        Arrays.stream(pl.getGamesFolder().getFile().listFiles()).forEach(file -> {
            if (file.getAbsolutePath().endsWith(".yml")){
                games.add(new Game("",pl));
            }
        });
    }

    public Game createGame(String name){
        Game game = new Game(name,pl);
        games.add(game);
        return game;
    }

    public Optional<Game> getGame(String name){
        for (Game game : games){
            if (game.getName().equals(name)) return Optional.of(game);
        }
        return Optional.empty();
    }

    public void focus(Player player, Game game){
        gameFocusMap.put(player.getUniqueId(),game);
    }

    public HashMap<UUID, Game> getGameFocusMap() {
        return gameFocusMap;
    }

    public List<GameConfig> getGameConfigs() {
        return gameConfigs;
    }

    public List<Game> getGames() {
        return games;
    }

    public StumblePillars getPl() {
        return pl;
    }
}
