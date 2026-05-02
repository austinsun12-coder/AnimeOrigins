package com.animeorigins.gui;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import com.animeorigins.listeners.PlayerJoinListener;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Arrays;

public class GUIListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    public GUIListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof OriginGUI)) return;

        // Always cancel clicks inside our GUI to prevent item theft
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        int slot = event.getRawSlot();

        // Close button
        if (slot == 53) {
            player.closeInventory();
            return;
        }

        // Check if the clicked slot is a character slot
        int[] characterSlots = OriginGUI.getCharacterSlots();
        int charIndex = -1;
        for (int i = 0; i < characterSlots.length; i++) {
            if (characterSlots[i] == slot) {
                charIndex = i;
                break;
            }
        }
        if (charIndex == -1) return;

        Origin[] origins = Origin.values();
        if (charIndex >= origins.length) return;

        Origin chosen = origins[charIndex];
        Origin current = plugin.getOriginManager().getOrigin(player);

        // Already selected
        if (chosen == current) {
            player.sendActionBar(Component.text("§eYou already are " + chosen.getDisplayName() + "!"));
            return;
        }

        // Locked check
        if (current != null && !plugin.getConfig().getBoolean("settings.allow-reselect", true)) {
            player.sendActionBar(Component.text("§cYour origin is locked! You cannot change it."));
            return;
        }

        // Apply origin
        plugin.getOriginManager().setOrigin(player, chosen);
        com.animeorigins.commands.OriginCommand.applyOriginSetup(player, chosen);

        player.closeInventory();

        // Broadcast
        if (plugin.getConfig().getBoolean("settings.broadcast-selection", true)) {
            plugin.getServer().broadcast(Component.text(
                "§6" + player.getName() + " §7has chosen the origin of §e" + chosen.getEmoji() + " " + chosen.getDisplayName() + "§7!"));
        }

        // Show info
        PlayerJoinListener.sendOriginInfo(player, chosen);

        // Play a sound
        player.getWorld().playSound(player.getLocation(),
            org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.0f);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof OriginGUI) {
            event.setCancelled(true);
        }
    }
}
