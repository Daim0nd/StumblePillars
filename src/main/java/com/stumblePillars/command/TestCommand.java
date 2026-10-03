package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.effect.BlackHoleEffect;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestCommand extends CommonCommand implements Listener {
    private final Map<UUID, BlackHoleEffect> effects = new HashMap<>();

    public TestCommand(StumblePillars plugin) {
        super("test", "", false, plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.handler(context -> {
            Player player = (Player) context.sender();
            stop(player.getUniqueId());
            if (effects.size() >= 2) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Aguarde um dos buracos negros terminar antes de criar outro.</yellow>"));
                return;
            }
            UUID owner = player.getUniqueId();
            BlackHoleEffect effect = new BlackHoleEffect(getPlugin(), player, () -> effects.remove(owner));
            effects.put(owner, effect);
            try {
                effect.start();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#FFAD48:#FFF0BF>✦ Um buraco negro está se formando...</gradient> <gray>Duração: 20 segundos. /sp test stop para remover.</gray>"));
            } catch (RuntimeException exception) {
                effect.close();
                getPlugin().getSLF4JLogger().error("Falha ao criar o efeito de buraco negro", exception);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Não foi possível criar o efeito.</red>"));
            }
        }));
        manager.command(builder.literal("stop").handler(context -> {
            stop(((Player) context.sender()).getUniqueId());
            context.sender().sendMessage(MiniMessage.miniMessage().deserialize("<gray>Efeito removido.</gray>"));
        }));
    }

    private void stop(UUID player) {
        BlackHoleEffect effect = effects.remove(player);
        if (effect != null) effect.close();
    }

    @EventHandler public void onQuit(PlayerQuitEvent event) { stop(event.getPlayer().getUniqueId()); }
    @EventHandler public void onWorldChange(PlayerChangedWorldEvent event) { stop(event.getPlayer().getUniqueId()); }
    @EventHandler(ignoreCancelled = true) public void onChunkUnload(ChunkUnloadEvent event) {
        for (BlackHoleEffect effect : java.util.List.copyOf(effects.values())) if (effect.occupies(event.getChunk())) effect.close();
    }
    @EventHandler(ignoreCancelled = true) public void onWorldUnload(WorldUnloadEvent event) {
        for (BlackHoleEffect effect : java.util.List.copyOf(effects.values())) if (effect.belongsTo(event.getWorld())) effect.close();
    }

    public void closeAll() { for (BlackHoleEffect effect : java.util.List.copyOf(effects.values())) effect.close(); }
}
