package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import com.animeorigins.PassiveTicker;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamageListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    public DamageListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin == null) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        switch (origin) {
            case LUFFY -> {
                // Immune to fall damage
                if (cause == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
            }
            case NARUTO -> {
                // Ninja Agility: no fall damage
                if (cause == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // ── ATTACKING PLAYER deals damage to someone ──────────────────────
        if (event.getDamager() instanceof Player attacker) {
            Origin attackerOrigin = plugin.getOriginManager().getOrigin(attacker);

            if (attackerOrigin == Origin.ITADORI) {
                ItemStack hand = attacker.getInventory().getItemInMainHand();
                // Black Flash: bare fists deal 6 damage
                if (hand.getType() == Material.AIR) {
                    boolean isCrit = attacker.getFallDistance() > 0;
                    double baseDamage = 6.0;
                    event.setDamage(isCrit ? baseDamage * 3.0 : baseDamage);
                }
            }

            if (attackerOrigin == Origin.GOKU) {
                // Pure Heart: 50% less damage to passive/neutral mobs
                if (event.getEntity() instanceof Animals || event.getEntity() instanceof WaterMob ||
                    isNeutralMob(event.getEntity())) {
                    event.setDamage(event.getDamage() * 0.5);
                }
            }

            if (attackerOrigin == Origin.WUKONG) {
                // Ruyi Jingu Bang: stick = diamond sword damage + 3 reach (handled via event)
                ItemStack hand = attacker.getInventory().getItemInMainHand();
                if (hand.getType() == Material.STICK || hand.getType() == Material.BLAZE_ROD) {
                    event.setDamage(7.0); // diamond sword = 7
                }
            }
        }

        // ── PLAYER RECEIVING damage ────────────────────────────────────────
        if (event.getEntity() instanceof Player victim) {
            Origin victimOrigin = plugin.getOriginManager().getOrigin(victim);
            if (victimOrigin == null) return;

            switch (victimOrigin) {
                case LUFFY -> {
                    // Double damage from swords, axes, tridents
                    if (event.getDamager() instanceof Player || event.getDamager() instanceof Monster) {
                        ItemStack weapon = getWeapon(event.getDamager());
                        if (weapon != null) {
                            Material type = weapon.getType();
                            if (PassiveTicker.isSword(type) || PassiveTicker.isAxe(type) || PassiveTicker.isTrident(type)) {
                                event.setDamage(event.getDamage() * 2.0);
                            }
                        }
                    }
                }
                case ZORO -> {
                    // Iron Will: survive lethal damage
                    if (event.getFinalDamage() >= victim.getHealth()) {
                        var cm = plugin.getCooldownManager();
                        if (!cm.isOnCooldown(victim.getUniqueId(), "iron_will")) {
                            event.setCancelled(true);
                            victim.setHealth(1.0);
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 2, false, true));
                            cm.setCooldown(victim.getUniqueId(), "iron_will", 1240);
                            victim.sendMessage("§6⚔ §eIron Will activates! You survive on half a heart!");
                        }
                    }
                }
                case ITADORI -> {
                    // Sukuna trigger check (handled in PassiveTicker)
                }
            }
        }
    }

    private ItemStack getWeapon(Entity entity) {
        if (entity instanceof Player p) return p.getInventory().getItemInMainHand();
        if (entity instanceof Mob mob) return mob.getEquipment() != null ? mob.getEquipment().getItemInMainHand() : null;
        return null;
    }

    private boolean isNeutralMob(Entity e) {
        return e instanceof Bee || e instanceof Wolf || e instanceof PolarBear ||
               e instanceof Llama || e instanceof Dolphin || e instanceof Spider;
    }
}
