package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutionException;

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
                games.add(new Game(file.getName().replace(".yml", ""), pl, new RandomMode(pl)));
            }
        });
    }

    public Game createGame(String name, World world){
        Game game = new Game(name, pl, new RandomMode(pl));
        games.add(game);
        try {
            pl.getArenaManager().createTemplate(name,world).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return game;
    }

    public Optional<Game> getGame(String name){
        for (Game game : games){
            if (game.getName().equals(name)) return Optional.of(game);
        }
        return Optional.empty();
    }

    public Optional<Game> getGame(Player player){
        for (Game game : games){
            if (game.getPlayers().contains(player.getUniqueId())) return Optional.of(game);
        }
        return Optional.empty();
    }

    public void focus(Player player, Game game){
        gameFocusMap.put(player.getUniqueId(),game);
    }

    public boolean isFocusing(Player player){
        if (gameFocusMap.containsKey(player.getUniqueId())) return true;
        return false;
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
