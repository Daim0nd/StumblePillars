package com.stumblePillars.command;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.TickTask;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.incendo.cloud.Command;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class TestCommand extends CommonCommand {

    private final StumblePillars plugin;

    public TestCommand(StumblePillars pl) {
        super("test", "", false, pl);
        this.plugin = pl;
    }

    @Override
    public void construct(LegacyPaperCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        manager.command(builder.handler(commandContext -> {
            Player player = (Player) commandContext.sender();





        }));
    }

    public void play(Location start, Location end) {
        ItemDisplay meteor = start.getWorld().spawn(start, ItemDisplay.class);

        meteor.setItemStack(new ItemStack(Material.MAGMA_BLOCK));
        meteor.setGlowing(true);
        meteor.setGlowColorOverride(Color.ORANGE);

        Transformation transformation = meteor.getTransformation();
        transformation.getScale().set(2.5f, 2.5f, 2.5f);
        meteor.setTransformation(transformation);

        List<ItemDisplay> fragments = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            ItemDisplay piece = start.getWorld().spawn(start, ItemDisplay.class);
            piece.setItemStack(new ItemStack(Material.COBBLED_DEEPSLATE));
            piece.setGlowing(true);

            Transformation t = piece.getTransformation();
            t.getScale().set(0.35f, 0.35f, 0.35f);
            piece.setTransformation(t);

            fragments.add(piece);
        }

        Vector direction = end.toVector()
                .subtract(start.toVector())
                .normalize();

        double distance = start.distance(end);
        Random random = new Random();

        new BukkitRunnable() {

            double progress = 0;

            @Override
            public void run() {
                progress += 0.8;


                if (progress >= distance) {
                    explode(meteor.getLocation());
                    meteor.remove();
                    fragments.forEach(Entity::remove);
                    cancel();
                    return;
                }

                Location loc = start.clone().add(direction.clone().multiply(progress));

                meteor.teleport(loc);
                Transformation tr = meteor.getTransformation();

                Quaternionf left = new Quaternionf()
                        .rotateXYZ(
                                (float) Math.toRadians(progress * 6),
                                (float) Math.toRadians(progress * 12),
                                (float) Math.toRadians(progress * 4)
                        );
                meteor.getTransformation().getScale().sub(new Vector3f(10f,10f,10f));

                tr.getLeftRotation().set(left);
                meteor.setTransformation(tr);

                World world = loc.getWorld();

                world.spawnParticle(Particle.FLAME, loc, 10, 0.25, 0.25, 0.25, 0.01);
                world.spawnParticle(Particle.LARGE_SMOKE, loc, 6, 0.3, 0.3, 0.3, 0);
                world.spawnParticle(Particle.ASH, loc, 5, 0.5, 0.5, 0.5, 0);

                for (int i = 0; i < fragments.size(); i++) {
                    ItemDisplay piece = fragments.get(i);

                    Vector offset = direction.clone()
                            .multiply(-1.2 - i * 0.15);

                    offset.add(new Vector(
                            (random.nextDouble() - 0.5) * 0.8,
                            (random.nextDouble() - 0.5) * 0.8,
                            (random.nextDouble() - 0.5) * 0.8
                    ));

                    piece.teleport(loc.clone().add(offset));

                    Transformation t = piece.getTransformation();

                    Quaternionf rot = new Quaternionf()
                            .rotateXYZ(
                                    (float) Math.toRadians(progress * 15 + i * 20),
                                    (float) Math.toRadians(progress * 10),
                                    (float) Math.toRadians(progress * 25)
                            );

                    t.getLeftRotation().set(rot);
                    piece.setTransformation(t);
                }
            }

            private void explode(Location loc) {
                World world = loc.getWorld();

                world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
                world.spawnParticle(Particle.FLAME, loc, 150, 2, 2, 2, 0.15);
                world.spawnParticle(Particle.LAVA, loc, 80, 1.5, 1.5, 1.5, 0.05);
                world.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, loc, 80, 2, 2, 2, 0);

                world.playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 4f, 0.7f);
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

}