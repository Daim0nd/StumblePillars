package com.stumblePillars;

import com.stumblePillars.arena.ArenaManager;
import com.stumblePillars.command.CommandService;
import com.stumblePillars.configuration.GameFolder;
import com.stumblePillars.game.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;


public final class StumblePillars extends JavaPlugin {

    private CommandService service;
    private @NonNull PaperCommandManager<CommandSourceStack> commandManager;
    private GameFolder gamesFolder;
    private ArenaManager arenaManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        this.commandManager = PaperCommandManager.builder().executionCoordinator(ExecutionCoordinator.simpleCoordinator()).buildOnEnable(this);
        this.service = new CommandService(this);
        this.service.init();

        this.arenaManager = new ArenaManager();
        this.gamesFolder = new GameFolder(this);
        this.gamesFolder.load();

        this.gameManager = new GameManager(this);
    }

    @Override
    public void onDisable() {
        
    }

    public @NonNull PaperCommandManager<CommandSourceStack> getCommandManager() {
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
}
