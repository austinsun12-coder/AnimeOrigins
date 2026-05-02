package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EquipmentListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    public EquipmentListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin == null) return;

        // Check if clicking into armor slot
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) return;
        int slot = event.getSlot();
        if (slot != 39) return; // 39 = helmet slot

        ItemStack item = event.getCursor();
        if (item == null || item.getType() == Material.AIR) return;

        // Wukong: no helmets at all
        if (origin == Origin.WUKONG) {
            event.setCancelled(true);
            player.sendMessage("§c☁ The Golden Headband will not allow you to wear that!");
        }

        // Gojo: only leather cap
        if (origin == Origin.GOJO && item.getType() != Material.LEATHER_HELMET) {
            // Don't cancel — let them equip but PassiveTicker punishes them
            player.sendMessage("§cWarning: Wearing non-leather headgear will blind you! (Sensory Overload)");
        }

        // Itadori: no shields
        if (origin == Origin.ITADORI) {
            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                ItemStack cursor = event.getCurrentItem();
                if (cursor != null && cursor.getType() == Material.SHIELD) {
                    event.setCancelled(true);
                    player.sendMessage("§cCursed Tools Only! You cannot use a shield.");
                }
            }
        }
    }
}
