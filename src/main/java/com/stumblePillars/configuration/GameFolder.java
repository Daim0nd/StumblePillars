package com.stumblePillars.configuration;

import com.stumblePillars.StumblePillars;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class GameFolder {

    private File file;
    private StumblePillars pl;

    public GameFolder(StumblePillars pl) {
        this.pl = pl;
    }

    public void load(){
        file = new File(pl.getDataFolder(),"games");
        file.mkdir();
    }

    public File getFile() {
        return file;
    }
}
