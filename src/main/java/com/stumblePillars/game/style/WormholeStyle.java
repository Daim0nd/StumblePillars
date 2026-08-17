package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.Wormhole;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WormholeStyle extends GameStyle {

    public WormholeStyle(StumblePillars pl, Game game) {
        super(pl, game);
    }

    @Override
    public String getName() {
        return "<gradient:#5700E5:#CA9FD6>ʙᴜʀᴀᴄᴏ </gradient><gradient:#CA9FD6:#5700E5>ᴅᴇ ᴍɪɴʜᴏᴄᴀ</gradient>";
    }

    private Wormhole lastWormhole;
    private int spawnCount;
    private int particleCount;

    @Override
    public void tick() {
        if (getGame().getPlayers().size() < 2) return;

        if (spawnCount >= 20 * 10) {
            Player player1 = getRandomPlayer();
            Player player2 = getRandomPlayer();
            while (player2 != null && player1 != null && player1 == player2) player2 = getRandomPlayer();
            if (player1 == null || player2 == null) {
                spawnCount = 0;
                return;
            }

            if (lastWormhole != null) lastWormhole.getSphereEngine().deleteAll();

            Wormhole wormhole = new Wormhole(player1.getLocation().clone(), player2.getLocation().clone());
            wormhole.spawn();
            lastWormhole = wormhole;
            spawnCount = 0;
        }
        if (particleCount >= 2) {
            if (lastWormhole != null){
                drawRings(lastWormhole.getEntrance1());
                drawRings(lastWormhole.getEntrance2());
                particleCount = 0;
            }
        }
        spawnCount++;
        particleCount++;
        if (lastWormhole == null) return;
        for (UUID uuid : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (lastWormhole.contains(player)) lastWormhole.join(player);
        }
    }

    @Override
    public void onStart() {
        getGame().broadcastPlayers(MiniMessage.miniMessage().deserialize(getName()));
    }

    @Override
    public void onEnd() {
        if (lastWormhole != null) lastWormhole.getSphereEngine().deleteAll();
    }

    @Override
    public int getTickCooldown() {
        return 1;
    }

    private void drawRings(Location location){
        int radius = 3;
        for (double rad = 0; rad < 6.28; rad += 0.08) {
            double x1 = Math.cos(rad) * radius + location.x();
            double y1 = Math.sin(rad) * radius + location.y();
            double z1 = location.z();

            location.getWorld().spawnParticle(Particle.PORTAL,x1,y1,z1,1);
        }
    }

    private Player getRandomPlayer() {
        Random random = new Random();
        int size = getGame().getPlayers().size();
        if (size == 0) return null;
        int luckyNumber = random.nextInt(size);
        return Bukkit.getPlayer(getGame().getPlayers().get(luckyNumber));
    }

}
