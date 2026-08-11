package com.stumblePillars.game.style;


import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.ItemFactory;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GraplinHookStyle extends GameStyle {
    public GraplinHookStyle(StumblePillars pl, Game game) {
        super(pl, game);
    }

    @Override
    public void tick() {

    }

    @Override
    public void onStart() {
        for(UUID uuid: getGame().getPlayers()){
            Bukkit.getPlayer(uuid).getInventory().addItem(ItemFactory.getGraplinHook());
        }

    }

    @Override
    public void onEnd() {

    }

    @Override
    public int getTickCooldown() {
        return 0;
    }
}
