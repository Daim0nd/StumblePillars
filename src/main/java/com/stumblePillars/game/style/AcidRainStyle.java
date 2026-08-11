package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AcidRainStyle extends GameStyle{
    public AcidRainStyle(StumblePillars pl, Game game) {
        super(pl, game);
    }

    @Override
    public void tick() {
        for (UUID uuid : getGame().getPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if (player.isInRain()) {
                if (player.getHealth() != 1) {
                    player.damage(1);
                }
            }
        }
    }

    @Override
    public void onStart() {
        getGame().getWorld().setStorm(true);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public int getTickCooldown() {
        return 20*2;
    }
}
