package com.stumblePillars.game;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.style.StyleType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class VoteMode extends RandomMode {
    private final StumblePillars plugin;
    private final VoteSession<StyleType> ballot = new VoteSession<>();
    private Game game;
    private static final int[] SLOTS = {11, 13, 15};

    public VoteMode(StumblePillars plugin) { super(plugin); this.plugin = plugin; }

    public static final class Menu implements InventoryHolder {
        public final VoteMode mode;
        public final UUID viewer;
        private final Inventory inventory;
        Menu(VoteMode mode, UUID viewer) {
            this.mode = mode;
            this.viewer = viewer;
            inventory = Bukkit.createInventory(this, 27, Component.text("Vote no estilo da partida"));
        }
        @Override public Inventory getInventory() { return inventory; }
    }

    @Override public void onCountdown(Game game) {
        this.game = game;
        cancelVoting();
        if (isChosenByStaff()) return;
        ballot.start(List.of(StyleType.values()), new Random());
        game.broadcastPlayers(MiniMessage.miniMessage().deserialize("<gold>Vote no estilo! <yellow>/sp vote</yellow> reabre o menu.</gold>"));
        for (UUID id : game.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) open(player);
        }
    }

    public boolean open(Player player) {
        if (!ballot.active() || game == null || game.getGameState() != GameState.WAITING
                || !game.getPlayers().contains(player.getUniqueId())) return false;
        Menu menu = new Menu(this, player.getUniqueId());
        render(menu);
        player.openInventory(menu.getInventory());
        return true;
    }

    private void render(Menu menu) {
        for (int i = 0; i < ballot.options().size(); i++) {
            StyleType option = ballot.options().get(i);
            ItemStack item = new ItemStack(option.icon);
            var meta = item.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<gold><name></gold>", Placeholder.unparsed("name", option.label)));
            long count = ballot.count(option);
            meta.lore(List.of(MiniMessage.miniMessage().deserialize(option.description), MiniMessage.miniMessage().deserialize("<gray>Votos: " + count + "</gray>"),
                    MiniMessage.miniMessage().deserialize(option == ballot.selected(menu.viewer)
                            ? "<green>✔ Seu voto atual</green>" : "<yellow>Clique para votar</yellow>")));
            item.setItemMeta(meta);
            menu.getInventory().setItem(SLOTS[i], item);
        }
    }

    public void click(Player player, Menu menu, int slot) {
        if (menu.mode != this || !menu.viewer.equals(player.getUniqueId()) || !ballot.active()
                || game.getGameState() != GameState.WAITING || !game.getPlayers().contains(player.getUniqueId())) return;
        for (int i = 0; i < SLOTS.length; i++) {
            if (slot == SLOTS[i]) {
                ballot.cast(player.getUniqueId(), ballot.options().get(i), game.getPlayers());
                refresh();
                return;
            }
        }
    }

    private void refresh() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu && menu.mode == this) render(menu);
        }
    }

    private void closeMenus() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu && menu.mode == this) player.closeInventory();
        }
    }

    public void cancelVoting() {
        ballot.clear();
        closeMenus();
    }

    @Override public void onCountdownFinished(Game game) {
        if (!ballot.active()) return;
        StyleType winner = ballot.finish(game.getPlayers(), new Random());
        setGameStyle(winner.create(plugin, game));
        setChosenByStaff(true);
        cancelVoting();
        game.broadcastPlayers(MiniMessage.miniMessage().deserialize("<gold>Estilo escolhido: <yellow><style></yellow></gold>",
                Placeholder.unparsed("style", winner.label)));
    }

    @Override public void onCountdownCancelled(Game game) { cancelVoting(); }
    @Override public void onJoin(Game game, Player player) { open(player); }
    @Override public void onLeave(Game game, Player player) {
        ballot.remove(player.getUniqueId());
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu menu && menu.mode == this) player.closeInventory();
        refresh();
    }
    @Override public void onStop(Game game) { cancelVoting(); super.onStop(game); }
}

