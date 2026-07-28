package com.stumblePillars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

public class LocationUtil {

    public static boolean isLocation(String string){
        if (string == null) return false;

        String[] s = string.split(";");

        if (s.length != 6) return false;

        try {
            Double.parseDouble(s[1]);
            Double.parseDouble(s[2]);
            Double.parseDouble(s[3]);
            Float.parseFloat(s[4]);
            Float.parseFloat(s[5]);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static String locationToString(Location location){
        String world = location.getWorld().getName();
        double x = location.x();
        double y = location.y();
        double z = location.z();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return world + ";"+x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

    public static Location stringToLocation(String string){
        if (string == null) {
            throw new IllegalArgumentException("String can't be null");
        }

        String[] s = string.split(";");

        if (s.length != 6){
            throw new IllegalArgumentException("Invalid format");
        }

        String world = s[0];
        double x = Double.valueOf(s[1]);
        double y = Double.valueOf(s[2]);
        double z = Double.valueOf(s[3]);
        float yaw = Float.valueOf(s[4]);
        float pitch = Float.valueOf(s[5]);

        World worldObj = Bukkit.getWorld(world);
        if(worldObj == null){
            worldObj = Bukkit.createWorld(WorldCreator.name(world));
            System.out.println(worldObj.getName());
        }

        return new Location(worldObj,x,y,z,yaw,pitch);
    }

}
