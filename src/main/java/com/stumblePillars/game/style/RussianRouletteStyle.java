package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import com.stumblePillars.game.TickTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;





import org.bukkit.entity.ArmorStand;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.*;

public class RussianRouletteStyle extends GameStyle {

    public enum SkeletonType {
        SAFE, LEGENDARY, DEATH
    }

    private int spawnCount;
    private boolean isRouletteActive;
    private Player currentVictim;
    private Location initialPlayerLoc;
    private final List<Skeleton> currentSkeletons = new ArrayList<>();
    private final Map<Integer, SkeletonType> skeletonTypeMap = new HashMap<>();
    private TickTask glowTask;
    private TickTask timeoutTask;
    private TickTask moveTask;
    private final Random random = new Random();
    private boolean[] isInLocation = {false};
    private ArmorStand vehicle;
    private boolean allowDismount;
    private boolean choiceMade;
    private boolean savedGravity;
    private final List<org.bukkit.scheduler.BukkitTask> delayedTasks = new ArrayList<>();

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
        savedGravity = player.hasGravity();
        choiceMade = false;
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
        initialPlayerLoc = player.getLocation();
        movePlayer(player, rouletteLoc,true,false);
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
            skeleton.lookAt(currentVictim);

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
        if (!isRouletteActive || currentVictim == null) return;
        if (!currentVictim.isOnline() || currentVictim.isDead() || !getGame().getPlayers().contains(currentVictim.getUniqueId())) {
            cleanupRoulette();
            return;
        }

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
        if (!isRouletteActive || currentVictim == null || choiceMade) return;
        choiceMade = true;
        cleanupSkeletons();
        Player victim = currentVictim;
        Location loc = victim.getLocation();
        switch (type) {
            case SAFE -> {
                loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f);
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 50, 2, 2, 2);
                victim.sendMessage(MiniMessage.miniMessage().deserialize("<green>✔ Este esqueleto é inofensivo! Você escapou!</green>"));
                movePlayer(victim, initialPlayerLoc, false, true);
            }
            case LEGENDARY -> {
                loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                loc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 80, 2, 2, 2, 0.5);
                giveLegendaryItem();
                victim.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#FFD700:#FFA500>✦ Este esqueleto lhe presenteou com um item lendário!</gradient>"));
                movePlayer(victim, initialPlayerLoc, false, true);
            }
            case DEATH -> {
                loc.getWorld().strikeLightningEffect(loc);
                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.5f, 0.5f);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
                loc.getWorld().spawnParticle(Particle.SOUL, loc, 100, 2, 2, 2, 0.1);
                victim.sendMessage(MiniMessage.miniMessage().deserialize("<red><bold>☠ VOCÊ MORREU!</bold> <gray>Este esqueleto era a morte certa...</gray>"));
                delayedTasks.add(Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    if (!isRouletteActive || currentVictim != victim) return;
                    cleanupRoulette();
                    if (victim.isOnline() && getGame().getPlayers().contains(victim.getUniqueId())) victim.setHealth(0);
                }, 40L));
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
        delayedTasks.forEach(org.bukkit.scheduler.BukkitTask::cancel);
        delayedTasks.clear();
        allowDismount = true;
        if (vehicle != null) {
            vehicle.eject();
            vehicle.remove();
            vehicle = null;
        }
        if (currentVictim != null) {
            currentVictim.setGravity(savedGravity);
            currentVictim.setFallDistance(0);
        }
        allowDismount = false;
        isRouletteActive = false;
        currentVictim = null;
        choiceMade = false;
        isInLocation[0] = false;
    }

    public boolean blocksDismount(Player player, org.bukkit.entity.Entity mount) {
        return isRouletteActive && !allowDismount && player.equals(currentVictim) && mount.equals(vehicle);
    }

    public boolean isProtectedVehicle(org.bukkit.entity.Entity entity) { return entity.equals(vehicle); }

    public void releasePlayer(Player player) {
        if (player.equals(currentVictim)) cleanupRoulette();
    }
    private Player getRandomPlayer() {
        int size = getGame().getPlayers().size();
        if (size == 0) return null;
        int luckNumber = random.nextInt(size);
        return Bukkit.getPlayer(getGame().getPlayers().get(luckNumber));
    }

    private void removeMoveTask() {
        if (moveTask != null) {
            getPlugin().getTaskManager().remove(moveTask);
            moveTask = null;
        }
    }

    private void movePlayer(Player player, Location location, boolean startRoulette, boolean unlockPlayer) {
        removeMoveTask();
        if (location == null || location.getWorld() == null) { cleanupRoulette(); return; }
        Location destination = location.clone();
        if (vehicle == null || !vehicle.isValid()) {
            vehicle = player.getWorld().spawn(player.getLocation(), ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setMarker(true);
                stand.setGravity(false);
                stand.setInvulnerable(true);
                stand.setCollidable(false);
                stand.setPersistent(false);
                stand.setSilent(true);
                stand.setBasePlate(false);
            });
        }
        player.setSneaking(false);
        player.setGravity(false);
        if (!vehicle.getPassengers().contains(player) && !vehicle.addPassenger(player)) {
            cleanupRoulette();
            return;
        }
        moveTask = new TickTask(1, () -> {
            if (!isRouletteActive || currentVictim != player || !player.isOnline() || player.isDead()
                    || !getGame().getPlayers().contains(player.getUniqueId()) || vehicle == null || !vehicle.isValid()) {
                cleanupRoulette();
                return;
            }
            Location current = vehicle.getLocation();
            Vector difference = destination.toVector().subtract(current.toVector());
            double distance = difference.length();
            Location next = distance <= 1 ? destination.clone() : current.clone().add(difference.normalize());
            // Paper 1.21.10 retains passengers when teleporting the vehicle.
            if (!vehicle.teleport(next)) { cleanupRoulette(); return; }
            player.setFallDistance(0);
            if (distance <= 1) {
                removeMoveTask();
                isInLocation[0] = true;
                if (startRoulette) spawnSkeletons(destination);
                if (unlockPlayer) {
                    allowDismount = true;
                    vehicle.removePassenger(player);
                    player.teleport(destination);
                    cleanupRoulette();
                }
            }
        });
        getPlugin().getTaskManager().register(moveTask);
    }
    public Player getCurrentVictim() {
        return currentVictim;
    }

    public boolean[] getIsInLocation() {
        return isInLocation;
    }
}
