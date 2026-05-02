package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import com.animeorigins.PassiveTicker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    public BlockListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    // Prevent Toji from using enchanting table
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.TOJI) return;

        InventoryType type = event.getInventory().getType();
        if (type == InventoryType.ENCHANTING || type == InventoryType.BREWING) {
            event.setCancelled(true);
            player.sendMessage("§cZero Cursed Energy: You cannot use enchanting tables or brewing stands!");
        }
    }

    // Prevent Toji from throwing Ender Pearls
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.TOJI) return;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT ||
            event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            player.sendMessage("§cZero Cursed Energy: You cannot use Ender Pearls!");
        }
    }

    // Zoro mines 50% slower unless using sword/axe
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.ZORO) return;

        Material inHand = player.getInventory().getItemInMainHand().getType();
        if (!PassiveTicker.isSword(inHand) && !PassiveTicker.isAxe(inHand)) {
            // Apply mining fatigue briefly to simulate 50% slower
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20, 1, true, false));
        }
    }

    // Itadori cannot use bows/crossbows/shields - block the interact
    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.ITADORI) return;

        Material inHand = player.getInventory().getItemInMainHand().getType();
        if (inHand == Material.BOW || inHand == Material.CROSSBOW ||
            inHand == Material.SHIELD ||
            player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
            event.setCancelled(true);
            player.sendMessage("§cCursed Tools Only! You cannot use bows or shields.");
        }
    }
}
