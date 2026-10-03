package com.stumblePillars.listener;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.MessagesConfig;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.GameState;
import com.stumblePillars.game.ItemFactory;
import com.stumblePillars.game.VoteMode;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import com.stumblePillars.game.style.RussianRouletteStyle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PlayerListener implements Listener {

    private StumblePillars pl;

    public PlayerListener(StumblePillars pl) {
        this.pl = pl;
    }

    @EventHandler public void onVoteClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof VoteMode.Menu menu)) return;
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) menu.mode.click(player, menu, event.getRawSlot());
    }

    @EventHandler public void onVoteDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof VoteMode.Menu) event.setCancelled(true);
    }

    @EventHandler public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player) || !event.isCancellable()) return;
        pl.getGameManager().getGame(player).ifPresent(game -> {
            if (game.getCurrentGameStyle() instanceof RussianRouletteStyle roulette
                    && roulette.blocksDismount(player, event.getDismounted())) event.setCancelled(true);
        });
    }

    @EventHandler public void onRouletteSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        pl.getGameManager().getGame(player).ifPresent(game -> {
            if (player.getVehicle() != null && game.getCurrentGameStyle() instanceof RussianRouletteStyle roulette
                    && roulette.blocksDismount(player, player.getVehicle())) event.setCancelled(true);
        });
    }

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if (player.isOp()) return;
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if (game.getPlayers().contains(player.getUniqueId())){
            for (String command : pl.getCommands()){
                if ("/".concat(command).equals(event.getMessage())){
                    if (pl.isBlacklist()){
                        event.setCancelled(true);
                        player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfig.BLOCKED_COMMAND));
                    }
                    else event.setCancelled(false);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if(game.getGameState().equals(GameState.WAITING)){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if(game.getGameState().equals(GameState.WAITING)){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerDamage(@NotNull EntityDamageEvent e) {
        for (Game arena : pl.getGameManager().getGames()) {
            if (arena.getCurrentGameStyle() instanceof RussianRouletteStyle roulette && roulette.isProtectedVehicle(e.getEntity())) {
                e.setCancelled(true);
                return;
            }
        }
        if (!(e.getEntity() instanceof final Player player)) {
            return;
        }

        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if(game.getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
        } else if (game.getGameState().equals(GameState.RUNNING)) {
            if (game.getSpectators().contains(player.getUniqueId())) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if (game.getSpectators().contains(player.getUniqueId())) event.setCancelled(true);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if (game.getSpectators().contains(player.getUniqueId())) event.setCancelled(true);

    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if (game.getSpectators().contains(player.getUniqueId()) && game.getGameState().equals(GameState.RUNNING)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if(opGame.isEmpty()) return;

        Game game = opGame.get();
        if (game.getSpectators().contains(player.getUniqueId())
        && player.getLocation().y() <= -100) player.teleport(game.getSpectatorSpawn());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pl.getGameManager().getGameFocusMap().remove(player.getUniqueId());

        for (Game game : pl.getGameManager().getGames()) {
            UUID uuid = player.getUniqueId();
            if (game.getPlayers().contains(uuid)) {
                game.leave(player);
                break;
            }else if (game.getSpectators().contains(uuid)){
                game.removeSpectator(player,false);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if (pl.isLobbyEnable() && pl.isToLobbyOnJoin()){
            event.getPlayer().teleport(pl.getLobby());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if (opGame.isEmpty()) return;
        Game game = opGame.get();

        if (game.getGameState().equals(GameState.RUNNING)){
            if (game.getCurrentGameStyle() instanceof RussianRouletteStyle roulette) roulette.releasePlayer(player);
            event.deathMessage(null);
            Bukkit.getScheduler().runTask(pl, () -> {
                player.spigot().respawn();
                game.addSpectator(player);
            });
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
                    player.setVelocity(difference.normalize().multiply(2.5));
                    player.setCooldown(current.getType(), 100);
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Skeleton skeleton)) return;

        Player player = event.getPlayer();
        for (Game game : pl.getGameManager().getGames()) {
            if (!game.getPlayers().contains(player.getUniqueId())) continue;
            if (!(game.getCurrentGameStyle() instanceof RussianRouletteStyle roulette)) return;

            roulette.handleSkeletonClick(player, skeleton);
            return;
        }
    }

}
