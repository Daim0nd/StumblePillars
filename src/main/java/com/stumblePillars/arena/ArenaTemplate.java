package com.stumblePillars.arena;

import java.io.File;

public class ArenaTemplate {

    private File worldDirectory;
    private String templateName;

    public ArenaTemplate(File worldDirectory, String templateName) {
        this.worldDirectory = worldDirectory;
        this.templateName = templateName;
    }

}
