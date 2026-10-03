package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.GameConfig;
import com.stumblePillars.game.style.GameStyle;
import com.stumblePillars.game.style.GraplinHookStyle;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

public class GameManager {

    private StumblePillars pl;
    private List<GameConfig> gameConfigs = new ArrayList<>();
    private List<Game> games = new ArrayList<>();
    private HashMap<UUID, Game> gameFocusMap = new HashMap<>();
    private Queue<UUID> playersWaiting = new ArrayDeque<>();
    public static List<Class<? extends GameStyle>> availableGameStyles = List.of(GraplinHookStyle.class);

    public GameManager(StumblePillars pl) {
        this.pl = pl;
        startQueueCheck();
    }

    public void registerGames() {
        File[] files = pl.getGamesFolder().getFile().listFiles();
        if (files == null) return;
        Arrays.stream(files).forEach(file -> {
            if (file.getAbsolutePath().endsWith(".yml")) {
                String mode = YamlConfiguration.loadConfiguration(file).getString("gameMode");
                games.add(new Game(file.getName().replace(".yml", ""), pl, mode));
            }
        });
    }

    public void reloadGames(){
        for (Game game : games){
            game.stop(false);
        }
        games.clear();
        gameFocusMap.clear();
        registerGames();
    }

    public Game createGame(String name, World world, String gameMode){
        Game game = new Game(name, pl,gameMode );
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
            if (game.getPlayers().contains(player.getUniqueId()) || game.getSpectators().contains(player.getUniqueId())) return Optional.of(game);
        }
        return Optional.empty();
    }

    public CompletableFuture<Optional<Game>> getAvailableGame(Player player){
        return CompletableFuture.supplyAsync(() -> {
            return Optional.of(getAvailableGameRecursive(player));
        });
    }

    private Game getAvailableGameRecursive(Player player){
        for (Game game : getGames()){
            if (game.getPlayers().size() < game.getMaxPlayers()){
                return game;
            }
        }
        return getAvailableGameRecursive(player);
    }

    private void startQueueCheck(){
        Bukkit.getScheduler().runTaskTimer(pl,() -> {
            for (Game game : games){
                if (playersWaiting.isEmpty()) return;
                if (!game.getGameState().equals(GameState.WAITING)) continue;
                if (game.getMaxPlayers() > game.getPlayers().size()){
                    Player player = Bukkit.getPlayer(playersWaiting.poll());
                    if (player != null){
                        game.join(player);
                    }
                }
            }
        },0L,10L);
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

    public Queue<UUID> getPlayersWaiting() {
        return playersWaiting;
    }
}
