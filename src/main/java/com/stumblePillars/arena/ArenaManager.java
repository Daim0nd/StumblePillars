package com.stumblePillars.arena;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.configuration.TemplatesFolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ArenaManager {

    private StumblePillars pl;
    private TemplatesFolder templatesFolder;
    private List<World> templates = new ArrayList<>();

    public ArenaManager(StumblePillars pl) {
        this.pl = pl;
        this.templatesFolder = pl.getTemplatesFolder();
    }

    public CompletableFuture<String> createInstance(String templateName){
        return CompletableFuture.supplyAsync(() -> {
            String instanceName = generateCustomId(templateName);
            Path source = templatesFolder.getFile().toPath().resolve(templateName);
            Path path = Bukkit.getWorldContainer().toPath().resolve(instanceName);

            try {
                Files.walk(source).forEach(file ->{
                    try {
                        Path target = path.resolve(source.relativize(file));

                        if (Files.isDirectory(file)) {
                            Files.createDirectories(target);
                        } else {
                            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                        }

                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return instanceName;
        });
    }

    public CompletableFuture<Void> createTemplate(String name, World world){
        return CompletableFuture.runAsync(() -> {
            File file = new File(templatesFolder.getFile(),name);
            file.mkdir();

            copyWorldFolder(world.getWorldFolder(),file);
            Bukkit.getLogger().info("Template gerado com sucesso!");
        });
    }

    private void copyWorldFolder(File source, File target){
        if (!source.isDirectory()){
            return;
        }


        if (!target.exists() && !target.mkdirs()) {
            return;
        }

        final File[] files = source.listFiles();
        if (files == null) {
            return;
        }

        for (final File file : files) {
            if (isIgnoredFile(file.getName())) {
                continue;
            }

            final File targetFile = new File(target, file.getName());
            if (file.isDirectory()) {
                copyWorldFolder(file, targetFile);
            } else {
                try {
                    java.nio.file.Files.copy(file.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        }

    }

    public void deleteInstance(String instanceName){
        Path worldsPath = Bukkit.getWorldContainer().toPath();
        File instance = worldsPath.resolve(instanceName).toFile();

        if (instance == null || !instance.isDirectory()) return;

        unloadInstance(instanceName);
        deleteFolder(instance);
    }

    private void unloadInstance(String instanceName){
        World world = Bukkit.getWorld(instanceName);
        if (world != null){
            for (Player player : world.getPlayers()){
                player.kick(MiniMessage.miniMessage().deserialize("A arena foi descarregada!"));
            }
            Bukkit.unloadWorld(world,true);
        }
    }

    private boolean deleteFolder(File path){
        if(!path.exists()) return false;

        File[] files = path.listFiles();

        for (File file : files){
            if (file.isDirectory()) deleteFolder(file);
            else file.delete();
        }
        return path.delete();
    }

    private String generateCustomId(String name){
        return "sp-" + name +"-instance";
    }

    private boolean isIgnoredFile(String name) {
        return name.equals("uid.dat") || name.equals("session.dat") || name.equals("session.lock");
    }

}
