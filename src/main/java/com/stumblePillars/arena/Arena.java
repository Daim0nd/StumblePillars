package com.stumblePillars.arena;

import javax.xml.stream.Location;
import java.util.List;

public class Arena {

    private Location pos1;
    private Location pos2;
    private List<Location> spawns;

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(List<Location> spawns) {
        this.spawns = spawns;
    }
}
