package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MeteorStyle extends GameStyle{

    private Random random = new Random();

    public MeteorStyle(StumblePillars pl, Game game) {
        super(pl, game);
    }

    @Override
    public void tick() {
        List<UUID> players = getGame().getPlayers();
        int size = players.size();

        int luckyNumber = random.nextInt(size);
        Player player = Bukkit.getPlayer(players.get(luckyNumber));
        Location start = player.getLocation().clone().add(0,50,0);
        Location end = player.getLocation().clone();

        play(start,end);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public int getTickCooldown() {
        return 20 * 5;
    }

    private void play(Location start, Location end) {
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
                    explode(meteor.getLocation().clone());
                    meteor.remove();
                    fragments.forEach(Entity::remove);
                    cancel();
                    return;
                }

                Location loc = start.clone().add(direction.clone().multiply(progress));

                meteor.teleport(loc);
                Transformation tr = meteor.getTransformation();
                if(!meteor.getTransformation().getScale().equals(0,0,0)){
                    tr.getScale().set(meteor.getTransformation().getScale().sub(0.01f,0.01f,0.01f));
                }

                Quaternionf left = new Quaternionf()
                        .rotateXYZ(
                                (float) Math.toRadians(progress * 6),
                                (float) Math.toRadians(progress * 12),
                                (float) Math.toRadians(progress * 4)
                        );

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
                world.createExplosion(loc,4f,true,true);
            }

        }.runTaskTimer(getPlugin(), 0L, 1L);
    }



}
