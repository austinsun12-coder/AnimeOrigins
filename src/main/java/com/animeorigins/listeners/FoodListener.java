package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import com.animeorigins.PassiveTicker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    // Wukong: allowed foods
    private static final java.util.Set<Material> WUKONG_ALLOWED = java.util.Set.of(
        Material.APPLE, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
        Material.CARROT, Material.GOLDEN_CARROT, Material.POTATO, Material.BAKED_POTATO,
        Material.MELON_SLICE, Material.SWEET_BERRIES, Material.GLOW_BERRIES,
        Material.CHORUS_FRUIT
    );

    // Wukong: meat/forbidden foods
    private static final java.util.Set<Material> WUKONG_FORBIDDEN = java.util.Set.of(
        Material.BEEF, Material.COOKED_BEEF, Material.PORKCHOP, Material.COOKED_PORKCHOP,
        Material.CHICKEN, Material.COOKED_CHICKEN, Material.MUTTON, Material.COOKED_MUTTON,
        Material.RABBIT, Material.COOKED_RABBIT, Material.COD, Material.COOKED_COD,
        Material.SALMON, Material.COOKED_SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH,
        Material.ROTTEN_FLESH, Material.SPIDER_EYE
    );

    // Itadori cannot use bows/crossbows (handled by blocking ranged items)
    private static final java.util.Set<Material> ITADORI_BANNED_ITEMS = java.util.Set.of(
        Material.BOW, Material.CROSSBOW, Material.SHIELD
    );

    public FoodListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin == null) return;

        Material food = event.getItem().getType();

        if (origin == Origin.WUKONG) {
            if (WUKONG_FORBIDDEN.contains(food)) {
                // Let them eat but poison them
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, false, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0, false, true));
                    player.sendMessage("§c🐒 Ugh! Meat makes you sick!");
                }, 1L);
            }
        }

        if (origin == Origin.TOJI) {
            // Cannot use potions (brewed ones)
            if (food == Material.POTION || food == Material.SPLASH_POTION || food == Material.LINGERING_POTION) {
                event.setCancelled(true);
                player.sendMessage("§cZero Cursed Energy: You cannot use potions!");
            }
        }
    }
}
