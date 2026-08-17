package com.stumblePillars;

import com.stumblePillars.arena.ArenaManager;
import com.stumblePillars.command.CommandService;
import com.stumblePillars.configuration.GameFolder;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.configuration.TemplatesFolder;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.GameManager;
import com.stumblePillars.game.GameState;
import com.stumblePillars.listener.PlayerListener;
import com.stumblePillars.util.LocationUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class StumblePillars extends JavaPlugin {

    private static final Logger log = LoggerFactory.getLogger(StumblePillars.class);
    private CommandService service;
    private @NonNull LegacyPaperCommandManager<CommandSender> commandManager;
    private GameFolder gamesFolder;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private MessagesConfig messagesConfig;
    private TaskManager taskManager;
    private TemplatesFolder templatesFolder;

    private Location lobby;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.templatesFolder = new TemplatesFolder(this);
        this.templatesFolder.load();

        this.arenaManager = new ArenaManager(this);
        this.gamesFolder = new GameFolder(this);
        this.gamesFolder.load();

        this.gameManager = new GameManager(this);
        this.gameManager.registerGames();

        this.commandManager = LegacyPaperCommandManager.createNative(this, ExecutionCoordinator.simpleCoordinator());
        this.service = new CommandService(this);
        this.service.init();

        this.messagesConfig = new MessagesConfig(this);
        this.messagesConfig.load();

        this.taskManager = new TaskManager(this);
        this.taskManager.startMainTask();

        getConfig().options().copyDefaults(true);
        getConfig().addDefault("lobby","NaN");
        saveConfig();

        this.setupLobby();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this),this);
    }

    @Override
    public void onDisable() {
        for (Game game : gameManager.getGames()){
            if (game.getGameState().equals(GameState.RUNNING)) game.stop();
        }
    }

    private void setupLobby(){
        String lobbySignature = getConfig().getString("lobby");
        if (lobbySignature == null || !LocationUtil.isLocation(lobbySignature)){
            this.lobby = null;
            return;
        }
        this.lobby = LocationUtil.stringToLocation(getConfig().getString("lobby"));
    }

    public void setLobby(Location location){
        getConfig().set("lobby",LocationUtil.locationToString(location));
        saveConfig();
    }

    public boolean isLobbyEnable(){
        if (this.lobby == null) return false;
        return true;
    }

    public @NonNull LegacyPaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameFolder getGamesFolder() {
        return gamesFolder;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Location getLobby() {
        return lobby;
    }

    public TemplatesFolder getTemplatesFolder() {
        return templatesFolder;
    }
}
