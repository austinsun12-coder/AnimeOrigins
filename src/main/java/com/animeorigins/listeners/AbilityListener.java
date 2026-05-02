package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import com.animeorigins.managers.CooldownManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AbilityListener implements Listener {

    private final AnimeOriginsPlugin plugin;
    // Track double-jump for Wukong
    private final java.util.Set<java.util.UUID> inAir = new java.util.HashSet<>();

    public AbilityListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    // ── RIGHT-CLICK = PRIMARY ability ─────────────────────────────────────
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return; // must sneak + right-click for primary

        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin == null) return;

        CooldownManager cm = plugin.getCooldownManager();

        switch (origin) {
            case LUFFY -> triggerGearSecond(player, cm);
            case GOKU -> {
                if (player.getInventory().getItemInOffHand().getType() == Material.AIR &&
                    player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    triggerKamehameha(player, cm);
                }
            }
            case NARUTO -> triggerRasengan(player, cm);
            case WUKONG -> trigger72Transformations(player, cm);
            case GOJO -> triggerLimitless(player, cm);
        }
    }

    // ── SECONDARY ability: SHIFT + LEFT-CLICK ────────────────────────────
    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin == null) return;

        CooldownManager cm = plugin.getCooldownManager();

        switch (origin) {
            case GOKU -> triggerInstantTransmission(player, cm);
            case GOJO -> triggerHollowPurple(player, cm);
        }
    }

    // ── DOUBLE JUMP = Wukong Cloud Surfer ─────────────────────────────────
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin != Origin.WUKONG) return;

        CooldownManager cm = plugin.getCooldownManager();
        if (cm.isOnCooldown(player.getUniqueId(), "cloud_surfer")) {
            player.sendActionBar(Component.text("§cCloud Surfer: §e" + cm.getRemainingSeconds(player.getUniqueId(), "cloud_surfer") + "s remaining"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(true);
        player.setFlying(true);
        cm.setCooldown(player.getUniqueId(), "cloud_surfer", 70);
        player.sendActionBar(Component.text("§b☁ Flying Nimbus! §75s flight!"));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.05);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.setFlying(false);
                player.setAllowFlight(false);
                player.sendActionBar(Component.text("§7Flying Nimbus fades... gliding down."));
                // give slow fall
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 80, 0, false, true));
            }
        }, 100L); // 5 seconds
    }

    // ── ABILITY IMPLEMENTATIONS ───────────────────────────────────────────

    private void triggerGearSecond(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "gear_second")) {
            p.sendActionBar(Component.text("§cGear Second: §e" + cm.getRemainingSeconds(p.getUniqueId(), "gear_second") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "gear_second", 85);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 1, false, true));
        p.sendActionBar(Component.text("§c⚙ GEAR SECOND! §eSpeed III + Haste II for 10s!"));
        p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 40, 0.5, 1, 0.5, 0.1);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.8f);
    }

    private void triggerKamehameha(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "kamehameha")) {
            p.sendActionBar(Component.text("§cKamehameha: §e" + cm.getRemainingSeconds(p.getUniqueId(), "kamehameha") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "kamehameha", 100);
        p.sendActionBar(Component.text("§b⚡ KA-ME-HA-ME-HA!"));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.5f);

        // Fire a beam of damage along player's direction
        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();
        for (int i = 0; i < 30; i++) {
            loc = loc.add(dir);
            final Location checkLoc = loc.clone();
            p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, checkLoc, 5, 0.2, 0.2, 0.2, 0);
            for (Entity entity : p.getWorld().getNearbyEntities(checkLoc, 1.5, 1.5, 1.5)) {
                if (entity != p && entity instanceof LivingEntity le) {
                    le.damage(16.0, p);
                    le.setVelocity(dir.clone().multiply(3));
                }
            }
            if (loc.getBlock().getType().isSolid()) {
                p.getWorld().createExplosion(loc, 3.0f, false, false);
                break;
            }
        }
    }

    private void triggerInstantTransmission(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "instant_transmission")) {
            p.sendActionBar(Component.text("§cInstant Transmission: §e" + cm.getRemainingSeconds(p.getUniqueId(), "instant_transmission") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "instant_transmission", 70);
        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location target = p.getLocation().add(dir.multiply(15));
        // find safe spot
        target.setY(target.getY() + 1);
        p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30, 0.5, 1, 0.5, 0.1);
        p.teleport(target);
        p.getWorld().spawnParticle(Particle.PORTAL, target, 30, 0.5, 1, 0.5, 0.1);
        p.getWorld().playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.5f);
        p.sendActionBar(Component.text("§b✦ Instant Transmission!"));
    }

    private void triggerRasengan(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "rasengan")) {
            p.sendActionBar(Component.text("§cRasengan: §e" + cm.getRemainingSeconds(p.getUniqueId(), "rasengan") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "rasengan", 80);

        // Drain hunger
        p.setFoodLevel(Math.max(0, p.getFoodLevel() - 12));

        // Dash forward
        Vector dir = p.getEyeLocation().getDirection().normalize();
        p.setVelocity(dir.multiply(1.5));
        p.getWorld().spawnParticle(Particle.CRIT, p.getLocation(), 20, 0.3, 0.3, 0.3, 0.2);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.8f);
        p.sendActionBar(Component.text("§b🌀 RASENGAN!"));

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            for (Entity e : p.getNearbyEntities(2.5, 2.5, 2.5)) {
                if (e instanceof LivingEntity le && e != p) {
                    le.damage(12.0, p);
                    le.setVelocity(new Vector(dir.getX(), 2.5, dir.getZ()));
                }
            }
        }, 5L);
    }

    private void trigger72Transformations(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "transform")) {
            p.sendActionBar(Component.text("§c72 Transformations: §e" + cm.getRemainingSeconds(p.getUniqueId(), "transform") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "transform", 130);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2, false, true));
        p.sendActionBar(Component.text("§7✦ 72 Transformations activated!"));
        p.getWorld().spawnParticle(Particle.SMOKE, p.getLocation(), 30, 0.5, 1, 0.5, 0.05);
    }

    private void triggerLimitless(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "limitless")) {
            p.sendActionBar(Component.text("§cLimitless: §e" + cm.getRemainingSeconds(p.getUniqueId(), "limitless") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "limitless", 160);
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4, false, true)); // full immunity approx
        p.sendActionBar(Component.text("§b∞ INFINITY ACTIVE! §75s immunity!"));
        p.getWorld().spawnParticle(Particle.WITCH, p.getLocation(), 60, 0.8, 1.5, 0.8, 0.05);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.7f, 1.5f);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline()) {
                p.removePotionEffect(PotionEffectType.RESISTANCE);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 160, 1, false, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 0, false, true));
                p.sendActionBar(Component.text("§c∞ Infinity collapsed. Brain resetting..."));
            }
        }, 100L); // 5 seconds
    }

    private void triggerHollowPurple(Player p, CooldownManager cm) {
        if (cm.isOnCooldown(p.getUniqueId(), "hollow_purple")) {
            p.sendActionBar(Component.text("§cHollow Purple: §e" + cm.getRemainingSeconds(p.getUniqueId(), "hollow_purple") + "s"));
            return;
        }
        cm.setCooldown(p.getUniqueId(), "hollow_purple", 340);
        p.sendActionBar(Component.text("§5☄ HOLLOW PURPLE!"));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.3f);

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        // Break 3x3 tunnel and damage entities
        for (int i = 0; i < 25; i++) {
            loc = loc.add(dir);
            final Location checkLoc = loc.clone();
            // Destroy 3x3 cross section
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Location blLoc = checkLoc.clone().add(
                        dir.getZ() * dx, dy, -dir.getX() * dx);
                    if (!blLoc.getBlock().getType().isAir() &&
                        blLoc.getBlock().getType() != Material.BEDROCK) {
                        blLoc.getBlock().breakNaturally();
                    }
                }
            }
            p.getWorld().spawnParticle(Particle.WITCH, checkLoc, 8, 0.5, 0.5, 0.5, 0);
            for (Entity e : p.getWorld().getNearbyEntities(checkLoc, 2, 2, 2)) {
                if (e instanceof LivingEntity le && e != p) {
                    le.damage(20.0, p);
                }
            }
            if (loc.getBlock().getType() == Material.BEDROCK) break;
        }

        // Brain Fried debuff
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 160, 1, false, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 0, false, true));
            }
        }, 1L);
    }
}
