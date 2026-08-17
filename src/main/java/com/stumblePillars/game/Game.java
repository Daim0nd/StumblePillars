package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.arena.ArenaInstance;
import com.stumblePillars.configuration.GameConfig;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.fastboard.GameBoard;
import com.stumblePillars.game.style.GameStyle;
import com.stumblePillars.util.LocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Game {

    private StumblePillars pl;
    private GameState gameState = GameState.WAITING;
    private List<UUID> players = new ArrayList<>();
    private GameConfig gameConfig;
    private FileConfiguration config;
    private RandomItemService randomService;
    private World world;
    private ArenaInstance arenaInstance;
    private Timer gameStartTimer;
    private Timer gameFinishTimer;

    private GameMode mode;
    private String name;
    private int maxPlayers;
    private int minPlayers;
    private Location waitLobby;
    private int WAIT_COUNTDOWN_DURATION = 30;
    private int GAME_COUNTDOWN_DURATION = 5 * 60;
    private boolean isCountingDown = false;
    private List<Location> spawns;
    private HashMap<Player, Location> spawnLocationMap = new HashMap<>();
    private Location russianRouletteLocation;
    private final Map<UUID, GameBoard> gameBoards = new HashMap<>();

    public Game(String name, StumblePillars pl, String mode) {
        this.name = name;
        this.gameConfig = new GameConfig(name, pl);
        this.config = gameConfig.getConfig();
        this.pl = pl;
        this.mode = getGameMode(mode);
        this.load();
    }

    private void load() {
        this.world = LocationUtil.loadAndGetWorld(name);
        this.waitLobby = LocationUtil.stringToLocation(config.getString("waitLobby"));
        this.minPlayers = config.getInt("minPlayers");
        this.maxPlayers = config.getInt("maxPlayers");
        this.WAIT_COUNTDOWN_DURATION = config.getInt("gameStartCountdown");
        List<String> spawnList = config.getStringList("spawns");
        this.spawns = spawnList == null || spawnList.isEmpty() ? new ArrayList<>() : convertSpawns(spawnList);
        this.russianRouletteLocation = LocationUtil.stringToLocation(config.getString("russianRouletteLoc"));
        this.mode = getGameMode(config.getString("gameMode"));
    }

    public void setWaitLobby(Location location) {
        config.set("waitLobby", LocationUtil.locationToString(location));
        gameConfig.save();
        this.waitLobby = location;
    }

   public void setMinPlayers(int amount){
        config.set("minPlayers",amount);
        gameConfig.save();
        this.minPlayers = amount;
   }

   public void setMaxPlayers(int amount){
       config.set("maxPlayers",amount);
       gameConfig.save();
       this.maxPlayers = amount;
   }

   public void setRussianRouletteLoc(Location loc){
        config.set("russianRouletteLoc",LocationUtil.locationToString(loc));
        gameConfig.save();;
        this.russianRouletteLocation = loc;
   }

    private GameMode getGameMode(String name){
        switch (name){
            case "NORMAL": return new NoOpMode();
            case "RANDOM": return new RandomMode(pl);
            default: return new NoOpMode();
        }
    }

    public void addSpawn(Location location) {
        List<String> spawnsStringList = config.getStringList("spawns");
        if (spawnsStringList == null) spawnsStringList = new ArrayList<>();
        spawnsStringList.add(LocationUtil.locationToString(location));

        config.set("spawns", spawnsStringList);
        gameConfig.save();
    }

    private List<Location> convertSpawns(List<String> strings) {
        return strings.stream().map(LocationUtil::stringToLocation).toList();
    }

    public void join(Player player) {
        if (!isConfigured()) {
            if (player.isOp()){
                missingSettings().forEach(context -> player.sendMessage(context));
                return;
            }
            player.sendMessage(Component.text(MessagesConfig.INCOMPLETE_GAME));
            return;
        }

        if (players.size() >= maxPlayers) {
            player.sendMessage(Component.text(MessagesConfig.GAME_FULL));
            return;
        }

        if (gameState != GameState.WAITING) {
            player.sendMessage(Component.text(MessagesConfig.GAME_ALREADY_STARTED));
            return;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfig.GAME_JOIN));
        UUID uuid = player.getUniqueId();
        players.add(uuid);
        toWaitLobby(player);
        String joined = MessagesConfig.PLAYER_JOINED
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers));
        broadcastPlayers(Component.text(joined));
        GameBoard gameBoard = new GameBoard(player,pl);
        gameBoards.put(uuid, gameBoard);

        checkAndStartCountdown();
    }

    private void mapSpawns() {
        Iterator<Location> spawnsIterator = spawns.iterator();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            Location spawn;
            if (spawnsIterator.hasNext()) {
                spawn = spawnsIterator.next();
            } else if (!spawns.isEmpty()) {
                spawn = spawns.get(0);
            } else {
                spawn = waitLobby;
            }

            if (spawn == null) {
                continue;
            }

            spawn = spawn.clone();
            spawn.setWorld(world);

            spawnLocationMap.put(player, spawn);
        }
    }

    private void checkAndStartCountdown() {
        if (players.size() >= minPlayers && !isCountingDown) {
            isCountingDown = true;
            broadcastPlayers(Component.text(MessagesConfig.GAME_WILL_START.replace("{seconds}", String.valueOf(WAIT_COUNTDOWN_DURATION))));

            this.gameStartTimer = new Timer(pl,WAIT_COUNTDOWN_DURATION);
            gameStartTimer.start(this::gameStartCountdown);
        } else if (players.size() < minPlayers && isCountingDown) {
            gameStartTimer.stop();
            broadcastPlayers(Component.text(MessagesConfig.GAME_COUNTDOWN_CANCELLED));
        }
    }

    private void gameStartCountdown() {
        int countdown = gameStartTimer.getCountdown();
        if (countdown > 0) {
            showCountdownTitle(countdown);

            if (countdown >= 1) {
                broadcastPlayers(Component.text(MessagesConfig.GAME_COUNTDOWN.replace("{seconds}", String.valueOf(countdown))));
            }
        } else {
            gameStartTimer.stop();
            start();
        }
    }

    private void showCountdownTitle(int countdown) {
        String text = "§e" + countdown;
        players.forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                Title title = Title.title(
                        Component.text(text),
                        Component.text("§7Preparando-se..."),
                        Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(0))
                );
                p.showTitle(title);
            }
        });
    }

    public void start() {
        if (gameState != GameState.WAITING) {
            return;
        }

        if (players.size() < minPlayers) {
            broadcastPlayers(Component.text(MessagesConfig.GAME_NOT_ENOUGH_PLAYERS));
            isCountingDown = false;
            return;
        }

        CompletableFuture<String> arenaCreate = pl.getArenaManager().createInstance(name);

            arenaCreate.thenAccept((arenaName) -> {

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        world = LocationUtil.loadAndGetWorld(arenaName);
                        arenaInstance = new ArenaInstance(arenaName,name,world);

                        gameState = GameState.RUNNING;
                        gameFinishTimer = new Timer(pl,60*5);
                        gameFinishTimer.start(Game.this::gameFinishCountdown);
                        broadcastPlayers(Component.text(MessagesConfig.GAME_STARTED));
                        mapSpawns();
                        players.forEach(uuid -> {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null) {
                                player.sendMessage(Component.text(MessagesConfig.GAME_START_TEXT));
                                Location spawn = spawnLocationMap.get(player);
                                if (spawn != null) {
                                    player.teleport(spawn);
                                }
                            }
                        });
                        randomService = new RandomItemService(players);
                        pl.getTaskManager().register(randomService.getGiveItemTick());

                        mode.onStart(Game.this);

                    }
                }.runTask(pl);

            }).exceptionally(ex -> {
                pl.getSLF4JLogger().error("Falha ao criar arena para o jogo " + name, ex);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        gameState = GameState.WAITING;
                        isCountingDown = false;
                        broadcastPlayers(Component.text("§cFalha ao iniciar o jogo!"));
                    }
                }.runTask(pl);
                return null;
            });
    }

    private void gameFinishCountdown(){
        int countdown = gameFinishTimer.getCountdown();
        if (countdown <= 0){
            stop();
        }
    }

    public void leave(Player player) {
        UUID uuid = player.getUniqueId();
        if (!players.contains(uuid)) {
            return;
        }

        players.remove(uuid);
        removeBoard(uuid);
        String left = MessagesConfig.PLAYER_LEFT
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers));
        broadcastPlayers(Component.text(left));

        if (gameState == GameState.WAITING) {
            checkAndStartCountdown();
        }
    }

    public void remove(Player player) {
        UUID uuid = player.getUniqueId();
        if (!players.contains(uuid)) {
            return;
        }

        player.getInventory().clear();
        players.remove(uuid);
        removeBoard(uuid);
        checkLastPlayer();
        String left = MessagesConfig.PLAYER_LEFT
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers));
        broadcastPlayers(Component.text(left));

            Bukkit.getScheduler().runTaskLater(pl, () -> {
                player.spigot().respawn();
                if (pl.getLobby() != null) {
                    player.teleport(pl.getLobby());
                }
            }, 2L);
    }

    public void stop() {
        gameState = GameState.WAITING;

        gameFinishTimer.stop();
        spawnLocationMap.clear();
        if (randomService != null) {
            pl.getTaskManager().remove(randomService.getGiveItemTick());
            randomService = null;
        }

        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            if (pl.getLobby() != null) {
                player.teleport(pl.getLobby());
            }
        });

        isCountingDown = false;
        players.clear();
        clearBoards();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arenaInstance != null) {
                    pl.getArenaManager().deleteInstance(arenaInstance.getInstanceName());
                    arenaInstance = null;
                }
            }
        }.runTaskLater(pl,20*10);


        mode.onStop(this);
    }

    public void checkLastPlayer() {
        if (players.size() == 1) {
            Player winner = Bukkit.getPlayer(players.get(0));
            win(winner);
            stop();
        }
    }

    public void win(Player player) {
        player.getInventory().clear();
        player.sendMessage(Component.text("Você ganhou!"));
    }

    private void removeBoard(UUID uuid) {
        GameBoard board = gameBoards.remove(uuid);
        if (board != null) {
            board.delete();
        }
    }

    private void clearBoards() {
        gameBoards.values().forEach(GameBoard::delete);
        gameBoards.clear();
    }

    private void toWaitLobby(Player player) {
        player.teleport(waitLobby);
    }

    public void broadcastPlayers(Component message) {
        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        });
    }

    public boolean isConfigured() {
        if (waitLobby == null || russianRouletteLocation == null
        || maxPlayers <= 0 || minPlayers <= 0 || minPlayers >= maxPlayers
        || spawns.isEmpty() || spawns.size() < minPlayers) {
            return false;
        }
        return true;
    }

    public List<Component> missingSettings(){
        if (isConfigured()) return List.of();

        String PREFIX = "<color:#777777>Ξ</color>";

        List<Component> contexts = new ArrayList<>();
        contexts.add(mm("<b><color:#B5B5B5>CONFIGURAÇÕES FALTANTES</color></b>"));
        contexts.add(mm(""));

        if (waitLobby == null) contexts.add(mm(PREFIX+" Lobby de espera não configurado!"));
        if (russianRouletteLocation == null) contexts.add(mm(PREFIX+" Localização da roleta russa não configurado!"));
        if (maxPlayers <= 0) contexts.add(mm(PREFIX+" Jogadores máximos menor ou igual a zero"));
        if (minPlayers <= 0) contexts.add(mm(PREFIX+" Jogadores mínimos menores ou iguais a zero"));
        if (minPlayers >= maxPlayers) contexts.add(mm(PREFIX+" Jogadores mínimos maior que jogadores máximos"));
        if (spawns.isEmpty() || spawns.size() <= minPlayers) contexts.add(mm(PREFIX+" Localização dos spawns inexistentes ou quantidade de jogadores mínimos é maior que a quantidade de spawns"));

        contexts.add(mm(""));

        return contexts;
    }

    private Component mm(String s){
        return MiniMessage.miniMessage().deserialize(s);
    }

    public String getName() {
        return name;
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

    public GameState getGameState() {
        return gameState;
    }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public World getWorld() {
        return world;
    }

    public Location getRussianRouletteLocation() {
        if (russianRouletteLocation == null) return null;
        russianRouletteLocation.setWorld(world);
        return russianRouletteLocation;
    }

    public Timer getGameFinishTimer() {
        return gameFinishTimer;
    }

    public GameStyle getCurrentGameStyle() {
        if (mode instanceof RandomMode) {
            return ((RandomMode) mode).getCurrentStyle();
        }
        return null;
    }
}
