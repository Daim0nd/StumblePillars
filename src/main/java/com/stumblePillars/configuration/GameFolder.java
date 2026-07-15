package com.stumblePillars.configuration;

import com.stumblePillars.StumblePillars;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class GameFolder {

    private File file;
    private StumblePillars pl;

    public static String PREFIX = "games.";

    public GameFolder(StumblePillars pl) {
        this.pl = pl;
    }

    public void load(){
        file = new File(pl.getDataFolder(),"Games");
        file.mkdir();
        try{
            if (!file.exists()){
                file.createNewFile();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public File getFile() {
        return file;
    }
}
