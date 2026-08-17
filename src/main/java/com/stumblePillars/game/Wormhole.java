package com.stumblePillars.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class Wormhole {

    private Location entrance1;
    private Location entrance2;
    private HashMap<UUID, Long> cooldownMap = new HashMap<>();
    private SphereEngine sphereEngine = new SphereEngine();
    private final long COOLDOWN = 3_000;
    final double radius = 1;

    public Wormhole(Location entrance1, Location entrance2) {
        this.entrance1 = positionEntrance(entrance1);
        this.entrance2 = positionEntrance(entrance2);
    }

    public void spawn(){

        sphereEngine.drawSphere(entrance1,radius);
        sphereEngine.drawSphere(entrance2,radius);

    }

    public boolean contains(Player player){
        double distance1 = player.getLocation().distanceSquared(entrance1);
        double distance2 = player.getLocation().distanceSquared(entrance2);
        if (distance1 <= 2|| distance2 <= 2) return true;
        return false;
    }

    public void join(Player player){
        UUID uuid = player.getUniqueId();
        if (!cooldownMap.containsKey(uuid) || System.currentTimeMillis() - cooldownMap.get(uuid) >= COOLDOWN) {
            double distance1 = player.getLocation().distanceSquared(entrance1);
            double distance2 = player.getLocation().distanceSquared(entrance2);
            if (distance1 < distance2) player.teleport(entrance2);
            else player.teleport(entrance1);
            cooldownMap.put(uuid, System.currentTimeMillis());
        }
    }
    private Location positionEntrance(Location location){
        Random random = new Random();
        int x = random.nextInt(4);
        int y = random.nextInt(4);
        int z = random.nextInt(4);
        return location.add(x,y,z);
    }

    public Location getEntrance1() {
        return entrance1;
    }

    public Location getEntrance2() {
        return entrance2;
    }

    public HashMap<UUID, Long> getCooldownMap() {
        return cooldownMap;
    }

    public SphereEngine getSphereEngine() {
        return sphereEngine;
    }
}
