package com.stumblePillars.listener;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerListener implements Listener {

    private StumblePillars pl;

    public PlayerListener(StumblePillars pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (Game game : pl.getGameManager().getGames()) {
            if (game.getPlayers().contains(player.getUniqueId())) {
                game.leave(player);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if (pl.isLobbyEnable()) event.getPlayer().teleport(pl.getLobby());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if (opGame.isEmpty()) return;
        Game game = opGame.get();

        if (game.getGameState().equals(GameState.RUNNING)){
            game.remove(player);
        }

    }

}

