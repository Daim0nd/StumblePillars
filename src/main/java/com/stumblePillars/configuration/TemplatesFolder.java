package com.stumblePillars.configuration;

import com.stumblePillars.StumblePillars;

import java.io.File;
import java.io.IOException;

public class TemplatesFolder {

    private File file;
    private StumblePillars pl;

    public TemplatesFolder(StumblePillars pl) {
        this.pl = pl;
    }

    public void load(){
        file = new File(pl.getDataFolder(),"templates");
        file.mkdir();

    }

    public File getFile() {
        return file;
    }

}
