package com.stumblePillars.arena;

import org.bukkit.World;

public class ArenaInstance {

    private String instanceName;
    private String templateName;
    private World world;

    public ArenaInstance(String instanceName, String templateName, World world) {
        this.instanceName = instanceName;
        this.templateName = templateName;
        this.world = world;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public World getWorld() {
        return world;
    }
}
