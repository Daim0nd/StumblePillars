package com.stumblePillars.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataType;

public class ItemFactory {

    public static NamespacedKey GRAPLIN_HOOK_NAMESPACE = new NamespacedKey("pillars","graplin");

    public static ItemStack getGraplinHook(){
        ItemStack itemStack = ItemStack.of(Material.FISHING_ROD);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<color:#FFF243>Graplin Hook</color>"));
        meta.getPersistentDataContainer().set(GRAPLIN_HOOK_NAMESPACE, PersistentDataType.STRING,"graplin");
        meta.addEnchant(Enchantment.AQUA_AFFINITY,67,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
