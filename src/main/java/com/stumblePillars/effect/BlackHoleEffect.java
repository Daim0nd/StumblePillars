package com.stumblePillars.effect;

import com.stumblePillars.StumblePillars;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/** A temporary, purely visual black hole. All display entities share one anchor. */
public final class BlackHoleEffect implements AutoCloseable {
    private static final int LIFETIME = 400;
    private static final int FRAME_TICKS = 3;
    private static final double CORE_RADIUS = 1.35;
    private static final Color AMBER = Color.fromRGB(255, 161, 47);
    private static final Color GOLD = Color.fromRGB(255, 221, 145);
    private static final Color HOT = Color.fromRGB(255, 248, 225);
    private static final Particle.DustOptions[] DUST = {
            new Particle.DustOptions(Color.fromRGB(255, 99, 22), 0.65f),
            new Particle.DustOptions(AMBER, 0.55f),
            new Particle.DustOptions(HOT, 0.4f)
    };
    private final StumblePillars plugin;
    private final UUID owner;
    private final Location center;
    private final Vector right;
    private final Vector forward;
    private final List<Piece> pieces = new ArrayList<>();
    private final Random random = new Random();
    private final Runnable onClose;
    private BukkitTask task;
    private int age;
    private boolean closed;
    private double lastRenderedScale = -1;

    private record Piece(BlockDisplay entity, Vector offset, Vector3f size,
                         Quaternionf rotation, boolean orbiting) {}

    public BlackHoleEffect(StumblePillars plugin, Player player, Runnable onClose) {
        this.plugin = plugin;
        this.owner = player.getUniqueId();
        this.onClose = onClose;
        double yaw = Math.toRadians(player.getLocation().getYaw());
        this.forward = new Vector(-Math.sin(yaw), 0, Math.cos(yaw));
        this.right = new Vector(Math.cos(yaw), 0, Math.sin(yaw));
        this.center = player.getLocation().add(forward.clone().multiply(9)).add(0, 3.1, 0);
    }

    public void start() {
        try {
            buildCore();
            buildAccretionDisk();
            buildPhotonRing();
            buildLensedLight();
            buildDebris();
            render(0.002);
            sound(Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 0.5f);
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                try { tick(); }
                catch (RuntimeException exception) {
                    close();
                    plugin.getSLF4JLogger().error("Falha durante a animação do buraco negro", exception);
                }
            }, 1L, FRAME_TICKS);
        } catch (RuntimeException exception) {
            close();
            throw exception;
        }
    }

    private void buildCore() {
        // Overlapping tangent patches form an opaque rounded silhouette, without glowing seams.
        int latitudeBands = 8;
        for (int band = 0; band < latitudeBands; band++) {
            double latitude = -Math.PI / 2 + Math.PI * (band + 0.5) / latitudeBands;
            int segments = Math.max(5, (int) Math.round(18 * Math.cos(latitude)));
            double width = 2 * CORE_RADIUS * Math.cos(latitude) * Math.sin(Math.PI / segments) * 1.18;
            double height = 2 * CORE_RADIUS * Math.sin(Math.PI / (2 * latitudeBands)) * 1.18;
            for (int segment = 0; segment < segments; segment++) {
                double angle = Math.PI * 2 * segment / segments;
                Vector normal = new Vector(Math.cos(latitude) * Math.cos(angle), Math.sin(latitude), Math.cos(latitude) * Math.sin(angle));
                Vector tangent = new Vector(-Math.sin(angle), 0, Math.cos(angle));
                Quaternionf rotation = basis(world(tangent), world(normal.clone().crossProduct(tangent)).normalize());
                add(Material.BLACK_CONCRETE, normal.multiply(CORE_RADIUS - 0.07),
                        new Vector3f((float) width, (float) height, 0.24f), rotation, false, null, false);
            }
        }
        add(Material.BLACK_CONCRETE, new Vector(0, 0, 0), new Vector3f(1.55f), new Quaternionf(), false, null, false);
    }

    private void buildAccretionDisk() {
        // Radial layers get cooler towards the rim; overlapping arcs keep the disk continuous.
        double[] radii = {1.78, 2.43, 3.17};
        double[] widths = {0.64, 0.75, 0.78};
        Material[] materials = {Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE, Material.ORANGE_TERRACOTTA};
        for (int ring = 0; ring < radii.length; ring++) {
            int segments = 32;
            for (int segment = 0; segment < segments; segment++) {
                double angle = Math.PI * 2 * segment / segments;
                Vector position = diskPoint(radii[ring], angle);
                Vector tangent = diskPoint(1, angle + Math.PI / 2).normalize();
                Vector normal = world(new Vector(0, Math.cos(0.22), -Math.sin(0.22)));
                Quaternionf rotation = basis(world(tangent), normal);
                float length = (float) (2 * radii[ring] * Math.sin(Math.PI / segments) * 1.15);
                add(materials[ring], position, new Vector3f(length, 0.045f + ring * 0.014f, (float) widths[ring]),
                        rotation, true, ring == 0 ? HOT : ring == 1 ? GOLD : AMBER, ring == 0 && segment % 4 == 0);
            }
        }
    }

    private void buildPhotonRing() {
        for (int segment = 0; segment < 48; segment++) {
            double angle = Math.PI * 2 * segment / 48;
            Vector position = new Vector(1.49 * Math.cos(angle), 1.49 * Math.sin(angle), -0.08);
            Vector tangent = world(new Vector(-Math.sin(angle), Math.cos(angle), 0));
            Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(1, 0, 0), tangent.toVector3f());
            add(Material.YELLOW_CONCRETE, position, new Vector3f(0.23f, 0.035f, 0.035f), rotation, false, GOLD, segment % 3 == 0);
        }
    }

    private void buildLensedLight() {
        // A curved image of the far disk is visible above and below the event horizon.
        for (int segment = 0; segment < 40; segment++) {
            double angle = Math.PI * 2 * segment / 40;
            double x = 2.46 * Math.cos(angle), y = 1.97 * Math.sin(angle);
            Vector tangent = world(new Vector(-2.46 * Math.sin(angle), 1.97 * Math.cos(angle), 0)).normalize();
            Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(1, 0, 0), tangent.toVector3f());
            float thickness = Math.sin(angle) > 0 ? 0.075f : 0.038f;
            add(Material.ORANGE_CONCRETE, new Vector(x, y, 0.48), new Vector3f(0.4f, thickness, 0.10f),
                    rotation, false, AMBER, segment % 5 == 0);
        }
    }

    private void buildDebris() {
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * 2 * i / 10;
            Vector offset = diskPoint(3.9 + random.nextDouble() * 0.4, angle);
            offset.setY(offset.getY() + (random.nextDouble() - 0.5) * 0.4);
            float size = 0.055f + random.nextFloat() * 0.06f;
            add(Material.MAGMA_BLOCK, offset, new Vector3f(size, size, size * 2.8f),
                    new Quaternionf().rotateXYZ(i * 0.4f, i * 0.7f, i * 0.3f), true, AMBER, true);
        }
    }

    private void add(Material material, Vector offset, Vector3f size, Quaternionf rotation,
                     boolean orbiting, Color glow, boolean glowing) {
        BlockDisplay display = center.getWorld().spawn(center, BlockDisplay.class, entity -> {
            entity.setBlock(material.createBlockData());
            entity.setPersistent(false);
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setSilent(true);
            entity.setViewRange(1.25f);
            entity.setDisplayWidth(10);
            entity.setDisplayHeight(10);
            entity.setShadowRadius(0);
            entity.setBrightness(new Display.Brightness(glow == null ? 0 : 15, glow == null ? 0 : 15));
            entity.setGlowing(glowing);
            if (glow != null) entity.setGlowColorOverride(glow);
            entity.setInterpolationDuration(FRAME_TICKS);
            entity.setInterpolationDelay(0);
            entity.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(0.001f), new Quaternionf()));
            entity.addScoreboardTag("stumblepillars_blackhole_test");
        });
        pieces.add(new Piece(display, offset, size, rotation, orbiting));
    }

    private void tick() {
        Player player = plugin.getServer().getPlayer(owner);
        if (closed) return;
        if (age >= LIFETIME || player == null || !player.isOnline() || player.getWorld() != center.getWorld()
                || player.getLocation().distanceSquared(center) > 80 * 80
                || !center.getWorld().isChunkLoaded(center.getBlockX() >> 4, center.getBlockZ() >> 4)) {
            close();
            return;
        }
        age += FRAME_TICKS;
        double growth = smooth(Math.min(1, age / 66.0));
        double collapse = smooth(Math.min(1, (LIFETIME - age) / 30.0));
        double scale = Math.max(0.002, growth * collapse);
        render(scale);
        particles(scale);
        if (age % 120 == 0) sound(Sound.BLOCK_PORTAL_AMBIENT, 0.28f, 0.5f);
        if (age == LIFETIME - 28) sound(Sound.BLOCK_BEACON_DEACTIVATE, 0.4f, 0.55f);
    }

    private void render(double scale) {
        double spin = age * 0.008;
        Quaternionf orbit = new Quaternionf().rotationAxis((float) -spin,
                world(new Vector(0, Math.cos(0.22), -Math.sin(0.22))).toVector3f());
        for (Piece piece : pieces) {
            if (!piece.entity().isValid()) continue;
            if (!piece.orbiting() && scale == lastRenderedScale) continue;
            Vector3f offset = world(piece.offset()).toVector3f();
            Quaternionf rotation = new Quaternionf(piece.rotation());
            if (piece.orbiting()) {
                orbit.transform(offset);
                rotation.premul(orbit);
            }
            Vector3f size = new Vector3f(piece.size()).mul((float) scale);
            Vector3f translation = offset.mul((float) scale);
            // BlockDisplay origins are cube corners. Centre each rotated block on its orbit.
            translation.sub(rotation.transform(new Vector3f(size).mul(0.5f)));
            piece.entity().setInterpolationDelay(0);
            piece.entity().setTransformation(new Transformation(translation, rotation, size, new Quaternionf()));
        }
        lastRenderedScale = scale;
    }

    private void particles(double scale) {
        if (scale < 0.05) return;
        World world = center.getWorld();
        // Three continuous streams wind inward, rather than random particles inside the black core.
        for (int arm = 0; arm < 3; arm++) {
            for (int point = 0; point < 14; point++) {
                double progress = ((point / 14.0 + age * 0.006) % 1);
                double radius = 3.95 - progress * 2.38;
                double angle = arm * Math.PI * 2 / 3 + progress * 5.2 - age * 0.036;
                Location location = at(diskPoint(radius, angle), scale);
                int palette = progress > 0.72 ? 2 : progress > 0.35 ? 1 : 0;
                world.spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, DUST[palette]);
            }
        }
        for (int point = 0; point < 32; point++) {
            double angle = point * Math.PI * 2 / 32 + age * 0.017;
            Location location = at(new Vector(1.52 * Math.cos(angle), 1.52 * Math.sin(angle), -0.14), scale);
            world.spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, DUST[2]);
        }
        for (int point = 0; point < 4; point++) {
            double angle = random.nextDouble() * Math.PI * 2;
            Location location = at(diskPoint(2.0 + random.nextDouble() * 1.5, angle), scale);
            world.spawnParticle(Particle.END_ROD, location, 1, 0, 0, 0, 0);
        }
        if (age % 6 == 0) {
            Location location = at(diskPoint(3.9, age * -0.023), scale);
            world.spawnParticle(Particle.SMOKE, location, 2, 0.06, 0.06, 0.06, 0.005);
        }
    }

    private Vector diskPoint(double radius, double angle) {
        return new Vector(radius * Math.cos(angle), radius * Math.sin(angle) * Math.sin(0.22),
                radius * Math.sin(angle) * Math.cos(0.22));
    }

    private Vector world(Vector local) {
        return right.clone().multiply(local.getX()).add(new Vector(0, local.getY(), 0)).add(forward.clone().multiply(local.getZ()));
    }

    private Location at(Vector offset, double scale) { return center.clone().add(world(offset).multiply(scale)); }

    private static Quaternionf basis(Vector x, Vector y) {
        Vector z = x.clone().crossProduct(y).normalize();
        org.joml.Matrix3f matrix = new org.joml.Matrix3f().setColumn(0, x.toVector3f()).setColumn(1, y.toVector3f()).setColumn(2, z.toVector3f());
        return new Quaternionf().setFromNormalized(matrix);
    }

    private static double smooth(double value) { value = Math.max(0, Math.min(1, value)); return value * value * (3 - 2 * value); }

    private void sound(Sound sound, float volume, float pitch) {
        for (Player viewer : center.getWorld().getNearbyPlayers(center, 40)) viewer.playSound(center, sound, volume, pitch);
    }

    public boolean occupies(Chunk chunk) {
        return chunk.getWorld() == center.getWorld() && chunk.getX() == center.getBlockX() >> 4 && chunk.getZ() == center.getBlockZ() >> 4;
    }

    public boolean belongsTo(World world) { return center.getWorld() == world; }

    @Override public void close() {
        if (closed) return;
        closed = true;
        if (task != null) { task.cancel(); task = null; }
        pieces.forEach(piece -> piece.entity().remove());
        pieces.clear();
        onClose.run();
    }
}
