package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class InteractListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    public InteractListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    // Toji: Undetectable - Warden won't target Toji unless within 5 blocks
    @EventHandler
    public void onWardenTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Warden warden)) return;
        if (!(event.getTarget() instanceof Player player)) return;

        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.TOJI) return;

        double dist = warden.getLocation().distance(player.getLocation());
        if (dist > 5) {
            event.setCancelled(true);
        }
    }

    // Toji: Hostile mobs only notice within 5 blocks
    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;

        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.TOJI) return;

        if (event.getEntity() instanceof org.bukkit.entity.Monster) {
            double dist = event.getEntity().getLocation().distance(player.getLocation());
            if (dist > 5) {
                event.setCancelled(true);
            }
        }
    }

    // Naruto: Orange Jumpsuit - mobs spot you 2x farther
    // This is handled by NOT cancelling EntityTarget for Naruto (they always see further)
    @EventHandler
    public void onNarutoTarget(EntityTargetLivingEntityEvent event) {
        // No special logic needed - vanilla detection is default
        // Naruto's glowing effect (applied in PassiveTicker) ensures he's visible through walls
    }
}
