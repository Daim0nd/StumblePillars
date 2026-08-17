package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.TickTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RussianRouletteStyle extends GameStyle {

    public enum SkeletonType {
        SAFE, LEGENDARY, DEATH
    }

    private int spawnCount;
    private boolean isRouletteActive;
    private Player currentVictim;
    private final List<Skeleton> currentSkeletons = new ArrayList<>();
    private final Map<Integer, SkeletonType> skeletonTypeMap = new HashMap<>();
    private TickTask glowTask;
    private TickTask timeoutTask;
    private TickTask moveTask;
    private final Random random = new Random();

    public RussianRouletteStyle(StumblePillars pl, Game game) {
        super(pl, game);
    }

    @Override
    public String getName() {
        return "<gradient:#5A5A5A:#FF7676:#5A5A5A>ʀᴏʟᴇᴛᴀ ʀᴜꜱꜱᴀ</gradient>";
    }

    @Override
    public void tick() {
        if (isRouletteActive) return;

        spawnCount++;
        if (spawnCount >= 20 * 60) {
            startRoulette();
            spawnCount = 0;
        }
    }

    @Override
    public void onStart() {
        getGame().broadcastPlayers(MiniMessage.miniMessage().deserialize(getName()));
    }

    @Override
    public void onEnd() {
        cleanupRoulette();
    }

    @Override
    public int getTickCooldown() {
        return 1;
    }

    private void startRoulette() {
        Player player = getRandomPlayer();
        if (player == null) return;
        currentVictim = player;
        isRouletteActive = true;

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gradient:#FF0000:#FF7676>☠ Você foi o escolhido para a Roleta Russa!</gradient>"
        ));
        getGame().broadcastPlayers(MiniMessage.miniMessage().deserialize(
                "<gray>" + player.getName() + " foi enviado para a Roleta Russa...</gray>"
        ));

        Location rouletteLoc = getGame().getRussianRouletteLocation();
        if (rouletteLoc == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>A localização da roleta russa não foi configurada!</red>"
            ));
            isRouletteActive = false;
            currentVictim = null;
            return;
        }
        movePlayerToLocation(player, rouletteLoc);
    }

    private void spawnSkeletons(Location center) {
        List<SkeletonType> types = new ArrayList<>();
        types.add(SkeletonType.SAFE);
        types.add(SkeletonType.SAFE);
        types.add(SkeletonType.LEGENDARY);
        types.add(SkeletonType.DEATH);
        types.add(SkeletonType.DEATH);
        Collections.shuffle(types, random);

        center.getWorld().playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

        double angleStep = 2 * Math.PI / 5;
        for (int i = 0; i < 5; i++) {
            double angle = angleStep * i;
            double x = center.x() + 3 * Math.cos(angle);
            double z = center.z() + 3 * Math.sin(angle);
            Location skeleLoc = new Location(
                    center.getWorld(), x, center.y(), z,
                    (float) Math.toDegrees(-angle), 0
            );

            Skeleton skeleton = center.getWorld().spawn(skeleLoc, Skeleton.class);
            skeleton.setAI(false);
            skeleton.setCollidable(false);
            skeleton.setInvulnerable(true);
            skeleton.setPersistent(false);
            skeleton.setShouldBurnInDay(false);
            skeleton.setRemoveWhenFarAway(false);
            skeleton.lookAt(center);

            currentSkeletons.add(skeleton);
            skeletonTypeMap.put(skeleton.getEntityId(), types.get(i));
        }

        currentVictim.sendMessage(MiniMessage.miniMessage().deserialize(
                "<yellow>⚔ Escolha um esqueleto clicando com <bold>botão direito</bold>!</yellow>"
        ));

        glowTask = new TickTask(1, this::checkGlow);
        getPlugin().getTaskManager().register(glowTask);

        timeoutTask = new TickTask(20 * 15, () -> {
            if (isRouletteActive && currentVictim != null) {
                currentVictim.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>⏰ Tempo esgotado! Um esqueleto da morte veio até você...</red>"
                ));
                handleChoice(SkeletonType.DEATH);
            }
        });
        getPlugin().getTaskManager().register(timeoutTask);
    }

    private void checkGlow() {
        if (!isRouletteActive || currentVictim == null || !currentVictim.isOnline()) return;

        Location eyeLoc = currentVictim.getEyeLocation();
        Vector direction = eyeLoc.getDirection();

        for (Skeleton skeleton : currentSkeletons) {
            if (skeleton.isDead()) continue;

            Vector toSkele = skeleton.getEyeLocation()
                    .toVector().subtract(eyeLoc.toVector());
            double distance = toSkele.length();

            if (distance > 10) {
                skeleton.setGlowing(false);
                continue;
            }

            toSkele.normalize();
            double dot = direction.dot(toSkele);
            boolean looking = dot > 0.98 && currentVictim.hasLineOfSight(skeleton);
            skeleton.setGlowing(looking);
            if (looking) {
                skeleton.setGlowing(true);
            }
        }
    }

    public void handleSkeletonClick(Player player, Skeleton skeleton) {
        if (!isRouletteActive || !player.equals(currentVictim)) return;
        if (!currentSkeletons.contains(skeleton)) return;

        SkeletonType type = skeletonTypeMap.get(skeleton.getEntityId());
        if (type == null) return;

        handleChoice(type);
    }

    private void handleChoice(SkeletonType type) {
        if (!isRouletteActive || currentVictim == null) return;

        cleanupSkeletons();

        Location loc = currentVictim.getLocation();

        switch (type) {
            case SAFE -> {
                loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f);
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 50, 2, 2, 2);
                currentVictim.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<green>✔ Este esqueleto é inofensivo! Você escapou!</green>"
                ));
                Bukkit.getScheduler().runTaskLater(getPlugin(), this::cleanupRoulette, 30L);
            }
            case LEGENDARY -> {
                loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                loc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 80, 2, 2, 2, 0.5);
                loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 60, 2, 2, 2, 0.3);
                giveLegendaryItem();
                currentVictim.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gradient:#FFD700:#FFA500>✦ Este esqueleto lhe presenteou com um item lendário!</gradient>"
                ));
                Bukkit.getScheduler().runTaskLater(getPlugin(), this::cleanupRoulette, 30L);
            }
            case DEATH -> {
                loc.getWorld().strikeLightning(loc);
                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.5f, 0.5f);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
                loc.getWorld().spawnParticle(Particle.SOUL, loc, 100, 2, 2, 2, 0.1);
                currentVictim.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red><bold>☠ VOCÊ MORREU!</bold> <gray>Este esqueleto era a morte certa...</gray>"
                ));

                Player victim = currentVictim;
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    victim.setHealth(0);
                    cleanupRoulette();
                }, 40L);
            }
        }
    }

    private void giveLegendaryItem() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize(
                "<gradient:#FFD700:#FFA500>✦ Espada Lendária</gradient>"
        ));
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<gray>Um presente dos esqueletos</gray>"));
        meta.lore(lore);
        meta.setUnbreakable(true);
        sword.setItemMeta(meta);
        currentVictim.getInventory().addItem(sword);
    }

    private void cleanupSkeletons() {
        if (glowTask != null) {
            getPlugin().getTaskManager().remove(glowTask);
            glowTask = null;
        }
        if (timeoutTask != null) {
            getPlugin().getTaskManager().remove(timeoutTask);
            timeoutTask = null;
        }
        removeMoveTask();
        for (Skeleton skeleton : currentSkeletons) {
            if (!skeleton.isDead()) {
                skeleton.remove();
            }
        }
        currentSkeletons.clear();
        skeletonTypeMap.clear();
    }

    private void cleanupRoulette() {
        cleanupSkeletons();
        isRouletteActive = false;
        currentVictim = null;
    }

    private Player getRandomPlayer() {
        int size = getGame().getPlayers().size();
        if (size == 0) return null;
        int luckNumber = random.nextInt(size);
        return Bukkit.getPlayer(getGame().getPlayers().get(luckNumber));
    }

    private void movePlayerToLocation(Player player, Location location) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.entity.decoration.ArmorStand armorStand =
                new net.minecraft.world.entity.decoration.ArmorStand(EntityType.ARMOR_STAND, serverPlayer.level());
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setNoGravity(true);

        ClientboundAddEntityPacket packet =
                new ClientboundAddEntityPacket(
                        armorStand.getId(),
                        armorStand.getUUID(),
                        serverPlayer.getX(),
                        serverPlayer.getY(),
                        serverPlayer.getZ(),
                        armorStand.getXRot(),
                        armorStand.getYRot(),
                        armorStand.getType(),
                        0,
                        armorStand.getDeltaMovement(),
                        armorStand.getYHeadRot()
                );
        serverPlayer.connection.send(packet);

        serverPlayer.connection.send(
                new ClientboundSetEntityDataPacket(
                        armorStand.getId(),
                        armorStand.getEntityData().packAll()
                )
        );

        Location destLoc = location;
        Vec3[] currentPos = {serverPlayer.position()};
        Vec3 dest = new Vec3(destLoc.x(), destLoc.y(), destLoc.z());

        serverPlayer.startRiding(armorStand);
        ClientboundSetPassengersPacket setPassengersPacket = new ClientboundSetPassengersPacket(armorStand);
        serverPlayer.connection.send(setPassengersPacket);
        boolean[] isIn = {false};

        moveTask = new TickTask(1, () -> {
            if (!player.isOnline() || isIn[0]) {
                removeMoveTask();
                return;
            }
            Vec3 moveVec = dest.subtract(currentPos[0]).normalize();
            Vec3 difference = dest.subtract(currentPos[0]);
            double distance = difference.length();
            if (distance <= 1) {
                currentPos[0] = dest;
                serverPlayer.stopRiding();
                serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(armorStand.getId()));
                serverPlayer.teleportTo(dest.x, dest.y, dest.z);
                isIn[0] = true;
                removeMoveTask();
                spawnSkeletons(destLoc);
                return;
            }
            currentPos[0] = currentPos[0].add(moveVec);
            ClientboundMoveEntityPacket moveEntityPacket = new ClientboundMoveEntityPacket.Pos(
                    armorStand.getId(),
                    (short) (moveVec.x * 4096),
                    (short) (moveVec.y * 4096),
                    (short) (moveVec.z * 4096),
                    false
            );
            serverPlayer.connection.send(moveEntityPacket);
        });
        getPlugin().getTaskManager().register(moveTask);
    }

    private void removeMoveTask() {
        if (moveTask != null) {
            getPlugin().getTaskManager().remove(moveTask);
            moveTask = null;
        }
    }
}