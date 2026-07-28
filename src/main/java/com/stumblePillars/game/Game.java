package com.stumblePillars.game;

import com.stumblePillars.LocationUtil;
import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.GameConfig;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.fastboard.GameBoard;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

public class Game {

    private StumblePillars pl;
    private GameLoader gameLoader;
    private GameState gameState = GameState.WAITING;
    private List<UUID> players = new ArrayList<>();
    private GameConfig gameConfig;
    private FileConfiguration config;
    private TickTask waitTimerTask;
    private TickTask countdownTimerTask;
    private RandomItemService randomService;

    private String name;
    private String displayName;
    private int maxPlayers;
    private int minPlayers;
    private Location waitLobby;
    private int countGameStart = 0;
    private final int COUNTDOWN_DURATION = 30;
    private boolean isCountingDown = false;
    private List<Location> spawns;
    private HashMap<Player, Location> spawnLocationMap = new HashMap<>();

    public Game(String name,StumblePillars pl) {
        this.name = name;
        this.gameConfig = new GameConfig(name, pl);
        this.config = gameConfig.getConfig();
        this.pl = pl;
        this.load();
    }

    private void load(){
        this.waitLobby = config.getLocation("waitLobby");
        this.minPlayers = config.getInt("minPlayers");
        this.maxPlayers = config.getInt("maxPlayers");
        this.displayName = config.getString("displayName");
        this.spawns = config.getStringList("spawns") == null ? new ArrayList<>() : convertSpawns(config.getStringList("spawns"));
    }

    public void setPos1(Location location){
        config.set("pos1",location);
        gameConfig.save();
    }

    public void setPos2(Location location){
        config.set("pos2",location);
        gameConfig.save();
    }

    public void setWord(World world){
        config.set("world",world.getName());
        gameConfig.save();
    }

    public void addSpawn(Location location){
        List<String> spawnsStringList = config.getStringList("spawns");
        if (spawnsStringList == null) spawnsStringList = new ArrayList<>();
        spawnsStringList.add(LocationUtil.locationToString(location));

        config.set("spawns",spawnsStringList);
        gameConfig.save();
    }

    private List<Location> convertSpawns(List<String> strings){
        return strings.stream().map(LocationUtil::stringToLocation).toList();
    }

    public void join(Player player){
        if (!isConfigured()){
            player.sendMessage(Component.text(MessagesConfig.INCOMPLETE_GAME));
            return;
        }

        if (players.size() >= maxPlayers){
            player.sendMessage(Component.text(MessagesConfig.GAME_FULL));
            return;
        }

        if (gameState != GameState.WAITING){
            player.sendMessage(Component.text(MessagesConfig.GAME_ALREADY_STARTED));
           return;
        }

        UUID uuid = player.getUniqueId();
        players.add(uuid);
        toWaitLobby(player);
        String joined = MessagesConfig.PLAYER_JOINED
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers));
        broadcastPlayers(Component.text(joined));
        GameBoard gameBoard = new GameBoard(player);

        checkAndStartCountdown();
    }

    private void mapSpawns(){
        Iterator<Location> spawnsIte = spawns.iterator();
        for (UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            spawnLocationMap.put(player,spawnsIte.next());
        }
    }

    private void checkAndStartCountdown(){
        if (players.size() >= minPlayers && !isCountingDown){
            isCountingDown = true;
            countGameStart = COUNTDOWN_DURATION;
            broadcastPlayers(Component.text(MessagesConfig.GAME_WILL_START.replace("{seconds}", String.valueOf(COUNTDOWN_DURATION))));

            countdownTimerTask = new TickTask(20, this::decrementCountdown);
            pl.getTaskManager().register(countdownTimerTask);
        }
        else if (players.size() < minPlayers && isCountingDown){
            stopCountdown();
            broadcastPlayers(Component.text(MessagesConfig.GAME_COUNTDOWN_CANCELLED));
        }
    }

    private void decrementCountdown(){
        countGameStart--;

        if (countGameStart > 0){
            showCountdownTitle();

            if (countGameStart >= 1){
                broadcastPlayers(Component.text(MessagesConfig.GAME_COUNTDOWN.replace("{seconds}", String.valueOf(countGameStart))));
            }
        }
        else {
            stopCountdown();
            start();
        }
    }

    private void showCountdownTitle(){
        String text = "§e" + countGameStart;
        players.forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null){
                Title title = Title.title(
                    Component.text(text),
                    Component.text("§7Preparando-se..."),
                    Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(0))
                );
                p.showTitle(title);
            }
        });
    }

    private void stopCountdown(){
        isCountingDown = false;
        if (countdownTimerTask != null){
            pl.getTaskManager().remove(countdownTimerTask);
            countdownTimerTask = null;
        }
    }

    public void start(){
        if (gameState != GameState.WAITING){
            return;
        }

        if (players.size() < minPlayers){
            broadcastPlayers(Component.text(MessagesConfig.GAME_NOT_ENOUGH_PLAYERS));
            isCountingDown = false;
            countGameStart = 0;
            return;
        }

        gameState = GameState.RUNNING;
        broadcastPlayers(Component.text(MessagesConfig.GAME_STARTED));
        mapSpawns();

        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null){
                player.sendMessage(Component.text(MessagesConfig.GAME_START_TEXT));
                player.teleport(spawnLocationMap.get(player));
            }

        });
        randomService = new RandomItemService(players);
        pl.getTaskManager().register(new TickTask(100,randomService::tickDelivery));
    }

    public void leave(Player player){
        UUID uuid = player.getUniqueId();
        if (!players.contains(uuid)){
            return;
        }

        players.remove(uuid);
        String left = MessagesConfig.PLAYER_LEFT
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers));
        broadcastPlayers(Component.text(left));

        // Se estava em countdown e agora não temos mínimo, parar
        if (gameState == GameState.WAITING){
            checkAndStartCountdown();
        }
    }

    public void remove(Player player){
        UUID uuid = player.getUniqueId();
        if (!players.contains(uuid)){
            return;
        }

        player.getInventory().clear();
        player.teleport(pl.getLobby());
        players.remove(uuid);
        checkLastPlayer();
        String left = MessagesConfig.PLAYER_LEFT
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers));
        broadcastPlayers(Component.text(left));

        //Teleportar para o lobby
        player.teleport(pl.getLobby());
    }

    public void stop(){
        gameState = GameState.WAITING;

        spawnLocationMap.clear();
        pl.getTaskManager().remove(randomService.getGiveItemTick());

        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.teleport(pl.getLobby());

        });
    }

    public void checkLastPlayer(){
        if (players.size() == 1){
            Player winner = Bukkit.getPlayer(players.get(0));
            win(winner);
        }
    }

    public void win(Player player){
        player.getInventory().clear();
        player.sendMessage("Você ganhou!");
    }

    private void toWaitLobby(Player player){
        player.teleport(waitLobby);
    }

    private void broadcastPlayers(Component message){
        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null){
                player.sendMessage(message);
            }
        });
    }

    public boolean isConfigured(){
        if(waitLobby == null){
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public Location getWaitLobby() {
        return waitLobby;
    }

    public GameState getGameState(){
        return gameState;
    }

    public List<UUID> getPlayers(){
        return players;
    }

    public int getPlayerCount(){
        return players.size();
    }

    public boolean isCountingDown(){
        return isCountingDown;
    }

    public int getCountdownTime(){
        return countGameStart;
    }
}
