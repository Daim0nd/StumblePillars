package com.stumblePillars.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RandomItemService {

    private TickTask giveItemTick;
    private List<Material> materials = Arrays.stream(Material.values()).filter(Material::isItem).toList();
    private List<UUID> players;
    int total = materials.size();

    public RandomItemService(List<UUID> players) {
        this.players = players;
        giveItemTick = new TickTask(100,this::tickDelivery);
    }

    public void tickDelivery(){
        for (UUID uuid: players){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            giveRandomItem(player);
        }
    }

    private void giveRandomItem(Player player){
        Random random = new Random();
        int num = random.nextInt(total);
        player.give(new ItemStack(materials.get(num)));
    }

    public TickTask getGiveItemTick() {
        return giveItemTick;
    }
}
