package com.stumblePillars.game;

import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class Wormhole {

    private Location entrance1;
    private Location entrance2;
    private List<ItemDisplay> wormholeDisplay1 = new ArrayList<>();
    private List<ItemDisplay> wormholeDisplay2 = new ArrayList<>();
    private HashMap<UUID, Long> cooldownMap = new HashMap<>();
    private final long COOLDOWN = 3_000;
    final double radius = 1;

    public Wormhole(Location entrance1, Location entrance2) {
        this.entrance1 = positionEntrance(entrance1);
        this.entrance2 = positionEntrance(entrance2);
    }

    public void spawn(){

        drawSphere(entrance1,wormholeDisplay1);
        drawSphere(entrance2,wormholeDisplay2);

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

    public void delete(){
        wormholeDisplay1.forEach(itemDisplay -> itemDisplay.remove());
        wormholeDisplay1.clear();

        wormholeDisplay2.forEach(itemDisplay -> itemDisplay.remove());
        wormholeDisplay2.clear();
    }

    private void drawSphere(Location location, List<ItemDisplay> wormholeDisplay){

        for (int theta = 0; theta <= 180; theta += 20) {
            for (int phi = 0; phi <= 360; phi += 20) {

                double x = (Math.cos(Math.toRadians(phi))  * radius);
                double y = (Math.sin(Math.toRadians(phi)) * radius);
                double z = 0;

                double rx = x * Math.cos(Math.toRadians(theta)) + z * Math.sin(Math.toRadians(theta));
                double rz = -x * Math.sin(Math.toRadians(theta)) + z * Math.cos(Math.toRadians(theta));

                rx += location.x();
                y += location.y();
                rz += location.z();


                ItemDisplay itemDisplay = location.getWorld().spawn(new Location(location.getWorld(), rx, y, rz), ItemDisplay.class);
                Transformation transformation = new Transformation(new Vector3f(),new Quaternionf(), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf());
                itemDisplay.setTransformation(transformation);
                itemDisplay.setItemStack(ItemStack.of(Material.BLACK_CONCRETE));
                itemDisplay.setGlowing(true);
                itemDisplay.setGlowColorOverride(Color.WHITE);
                itemDisplay.lookAt(location, LookAnchor.EYES);
                wormholeDisplay.add(itemDisplay);
            }
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
}
