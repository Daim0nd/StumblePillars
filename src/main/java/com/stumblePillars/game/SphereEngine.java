package com.stumblePillars.game;

import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SphereEngine {
    private List<ItemDisplay> wormholeDisplay = new ArrayList<>();

    public void drawSphere(Location location, double radius){

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

    public void deleteAll(){
        wormholeDisplay.forEach(itemDisplay -> itemDisplay.remove());
        wormholeDisplay.clear();
    }

    public List<ItemDisplay> getWormholeDisplay() {
        return wormholeDisplay;
    }
}
