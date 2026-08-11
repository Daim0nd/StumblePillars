package com.stumblePillars.listener;

import com.stumblePillars.SavedBlock;
import com.stumblePillars.StumblePillars;
import com.stumblePillars.arena.Cuboid;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.GameState;
import com.stumblePillars.game.ItemFactory;
import io.papermc.paper.event.entity.FishHookStateChangeEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.util.Vector;

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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if (opGame.isEmpty()) return;
        Game game = opGame.get();

        Cuboid cuboid = game.getArenaCuboid();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if (opGame.isEmpty()) return;
        Game game = opGame.get();

        if (game.getGameState().equals(GameState.RUNNING)){
            Block block = event.getBlock();
            SavedBlock savedBlock = new SavedBlock(block.getLocation().clone(),block.getBlockData().clone());
            if (!game.getPlacedBlocks().contains(savedBlock)) game.getBreakedBlocks().add(savedBlock);
        }

    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();

        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if (opGame.isEmpty()) return;
        Game game = opGame.get();

        if (game.getGameState().equals(GameState.RUNNING)){
            Block block = event.getBlock();
            game.getPlacedBlocks().add(new SavedBlock(block.getLocation().clone(),block.getBlockData().clone()));
        }
    }

    @EventHandler
    public void onHookChangeState(PlayerFishEvent event){
        Player player = event.getPlayer();
        ItemStack current = player.getInventory().getItemInMainHand();
        if (!current.hasItemMeta()) return;
        if (current.getPersistentDataContainer().has(ItemFactory.GRAPLIN_HOOK_NAMESPACE)){
            if (event.getHook().getState().equals(FishHook.HookState.UNHOOKED)) {
                if (event.getState().equals(PlayerFishEvent.State.REEL_IN)) {
                    Vector difference = event.getHook().getLocation().toVector().subtract(player.getLocation().toVector());
                    player.setVelocity(difference.normalize().multiply(2));
                    player.setCooldown(current.getType(), 100);
                }
            }
        }
    }











}

