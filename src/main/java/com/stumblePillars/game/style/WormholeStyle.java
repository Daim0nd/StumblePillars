package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.Wormhole;
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

    private Wormhole lastWormhole;
    private int spawnCount;
    private int particleCount;

    @Override
    public void tick() {
        if (spawnCount >= 20 * 10) {
            Player player1 = getRandomPlayer();
            Player player2 = getRandomPlayer();
            while (player1 == player2) player2 = getRandomPlayer();

            if (lastWormhole != null) lastWormhole.delete();

            Wormhole wormhole = new Wormhole(player1.getLocation().clone(), player2.getLocation().clone());
            wormhole.spawn();
            lastWormhole = wormhole;
            spawnCount = 0;
        }
        if (particleCount >= 2) {
            if (lastWormhole != null){
                int radius = 3;
                for (double rad = 0; rad < 6.28; rad += 0.08) {
                    double x1 = Math.cos(rad) * radius + lastWormhole.getEntrance1().x();
                    double y1 = Math.sin(rad) * radius + lastWormhole.getEntrance1().y();
                    double z1 = lastWormhole.getEntrance1().z();

                    lastWormhole.getEntrance1().getWorld().spawnParticle(Particle.PORTAL,x1,y1,z1,1);
                }
                for (double rad = 0; rad < 6.28; rad += 0.08) {
                    double x2 = Math.cos(rad) * radius + lastWormhole.getEntrance2().x();
                    double y2 = lastWormhole.getEntrance2().y();
                    double z2 = Math.sin(rad) * radius + lastWormhole.getEntrance2().z();

                    lastWormhole.getEntrance1().getWorld().spawnParticle(Particle.PORTAL,x2,y2,z2,1);
                }
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

    }

    @Override
    public void onEnd() {
        if (lastWormhole != null) lastWormhole.delete();
    }

    @Override
    public int getTickCooldown() {
        return 1;
    }

    private Player getRandomPlayer() {
        Random random = new Random();
        int size = getGame().getPlayers().size();
        int luckyNumber = random.nextInt(size);
        return Bukkit.getPlayer(getGame().getPlayers().get(luckyNumber));
    }

}
