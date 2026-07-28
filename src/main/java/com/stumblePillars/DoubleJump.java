package com.stumblePillars;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJump implements Listener {

    @EventHandler
    public void onJump(PlayerJumpEvent event){
        Player player =event.getPlayer();
        Location loc = player.getLocation();
        player.sendMessage("Aura");
        if (loc.clone().subtract(0,1,0).getBlock().getType().equals(Material.AIR)){
            player.setVelocity(player.getEyeLocation().getDirection().multiply(1.2));
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getAction().isLeftClick()){
            player.setVelocity(player.getEyeLocation().getDirection().multiply(1.2));
            player.sendMessage("aaaa");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Só permite iniciar o double jump quando estiver no chão
        if (player.isOnGround()) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Se estiver em Creative ou Spectator, deixa voar normalmente
        if ((player.getGameMode() == GameMode.CREATIVE) ||
                (player.getGameMode() == GameMode.SPECTATOR)) {
            return;
        }

        event.setCancelled(true);

        player.setAllowFlight(false);
        player.setFlying(false);

        Vector velocity = player.getLocation().getDirection()
                .multiply(1.2);

        player.setVelocity(velocity);

        player.getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_BREEZE_JUMP,
                1f,
                1f
        );
    }
}

