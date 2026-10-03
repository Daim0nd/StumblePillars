package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.Material;

public enum StyleType {
    METEORS("Meteors", "Meteoros", Material.FIRE_CHARGE, "<gold>Desvie dos meteoros que caem na arena.</gold>"),
    WORMHOLE("Wormhole", "Buraco de minhoca", Material.ENDER_PEARL, "<gray>Encare os buracos de minhoca da arena.</gray>"),
    HOOK("GraplinHook", "Gancho", Material.FISHING_ROD, "<yellow>Use um gancho para se movimentar.</yellow>"),
    POTION("Potion", "Poções aleatórias", Material.POTION, "<dark_purple>Receba efeitos de poções imprevisíveis.</dark_purple>"),
    ROULETTE("RussianRoulette", "Roleta russa", Material.SKELETON_SKULL, "<white>Escolha um esqueleto e teste sua sorte.</white>");

    public final String id;
    public final String label;
    public final Material icon;
    public final String description;

    StyleType(String id, String label, Material icon, String description) {
        this.id = id;
        this.label = label;
        this.icon = icon;
        this.description = description;
    }

    public GameStyle create(StumblePillars plugin, Game game) {
        return switch (this) {
            case METEORS -> new MeteorStyle(plugin, game);
            case WORMHOLE -> new WormholeStyle(plugin, game);
            case HOOK -> new GraplinHookStyle(plugin, game);
            case POTION -> new RandomPotionStyle(plugin, game);
            case ROULETTE -> new RussianRouletteStyle(plugin, game);
        };
    }
}
