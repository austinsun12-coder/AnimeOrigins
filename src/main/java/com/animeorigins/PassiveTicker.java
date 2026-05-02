package com.animeorigins;

import com.animeorigins.managers.OriginManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PassiveTicker implements Runnable {

    private final AnimeOriginsPlugin plugin;
    private int tick = 0;

    public PassiveTicker(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        tick++;
        OriginManager om = plugin.getOriginManager();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!om.hasOrigin(player)) continue;
            Origin origin = om.getOrigin(player);

            switch (origin) {
                case LUFFY -> tickLuffy(player);
                case ITADORI -> tickItadori(player);
                case GOKU -> tickGoku(player);
                case TOJI -> tickToji(player);
                case ZORO -> tickZoro(player);
                case NARUTO -> tickNaruto(player);
                case WUKONG -> tickWukong(player);
                case GOJO -> tickGojo(player);
            }
        }
    }

    // ── LUFFY ──────────────────────────────────────────────────────────────
    private void tickLuffy(Player p) {
        // Devil Fruit: deep water = instant damage
        if (p.isInWater()) {
            var loc = p.getLocation();
            var block = loc.getBlock();
            var above = block.getRelative(0, 1, 0);
            boolean submerged = block.isLiquid() && above.isLiquid();
            if (submerged) {
                p.setRemainingAir(0);
                p.damage(4.0);
                p.sendActionBar(net.kyori.adventure.text.Component.text("§cYou cannot swim! Devil Fruit weakness!"));
            }
        }
    }

    // ── ITADORI ───────────────────────────────────────────────────────────
    private void tickItadori(Player p) {
        // Superhuman Athletics: Speed I, Jump Boost II
        applyEffect(p, PotionEffectType.SPEED, 0);
        applyEffect(p, PotionEffectType.JUMP_BOOST, 1);

        // Sukuna's Host
        if (p.getHealth() < 6.0) {
            applyEffect(p, PotionEffectType.BLINDNESS, 0);
            applyEffect(p, PotionEffectType.NAUSEA, 0);
        }
    }

    // ── GOKU ──────────────────────────────────────────────────────────────
    private void tickGoku(Player p) {
        // Zenkai Boost
        if (p.getHealth() < 8.0) {
            applyEffect(p, PotionEffectType.STRENGTH, 1);
            applyEffect(p, PotionEffectType.RESISTANCE, 0);
        }

        // Saiyan Metabolism: drain food faster (every 2 ticks)
        if (tick % 2 == 0) {
            int food = p.getFoodLevel();
            if (food > 0) {
                p.setFoodLevel(Math.max(0, food - 1));
            } else {
                applyEffect(p, PotionEffectType.WEAKNESS, 1);
                applyEffect(p, PotionEffectType.SLOWNESS, 1);
            }
        }
    }

    // ── TOJI ──────────────────────────────────────────────────────────────
    private void tickToji(Player p) {
        // Heavenly Restriction passives
        applyEffect(p, PotionEffectType.STRENGTH, 0);
        applyEffect(p, PotionEffectType.SPEED, 1);

        // Ensure max health is 40 (20 hearts)
        var maxHealthAttr = p.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr != null && maxHealthAttr.getValue() < 40.0) {
            maxHealthAttr.setBaseValue(40.0);
        }

        // Heavy Footfalls: water sinks faster handled in MoveListener
        if (p.isInWater()) {
            p.setRemainingAir(p.getRemainingAir() - 1); // extra air drain
        }
    }

    // ── ZORO ──────────────────────────────────────────────────────────────
    private void tickZoro(Player p) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (isSword(hand.getType())) {
            applyEffect(p, PotionEffectType.HASTE, 1);
            applyEffect(p, PotionEffectType.STRENGTH, 0);
        }
    }

    // ── NARUTO ────────────────────────────────────────────────────────────
    private void tickNaruto(Player p) {
        // Kurama's Healing
        applyEffect(p, PotionEffectType.REGENERATION, 0);
        // Orange Jumpsuit: permanent Glowing
        applyEffect(p, PotionEffectType.GLOWING, 0);
    }

    // ── WUKONG ────────────────────────────────────────────────────────────
    private void tickWukong(Player p) {
        // Golden Headband: no helmet
        ItemStack helmet = p.getInventory().getHelmet();
        if (helmet != null && helmet.getType() != Material.AIR &&
            helmet.getType() != Material.LEATHER_HELMET) {
            p.damage(2.0);
            p.sendActionBar(net.kyori.adventure.text.Component.text("§cThe Golden Headband rejects that helmet!"));
        }
    }

    // ── GOJO ──────────────────────────────────────────────────────────────
    private void tickGojo(Player p) {
        // Six Eyes: Night Vision always
        applyEffect(p, PotionEffectType.NIGHT_VISION, 0);

        // Sensory Overload: only leather cap
        ItemStack helmet = p.getInventory().getHelmet();
        if (helmet != null && helmet.getType() != Material.AIR &&
            helmet.getType() != Material.LEATHER_HELMET) {
            applyEffect(p, PotionEffectType.BLINDNESS, 0);
            p.sendActionBar(net.kyori.adventure.text.Component.text("§cSensory Overload! Remove your helmet!"));
        }

        // Six Eyes: highlight entities within 30 blocks (applied via Glowing)
        p.getWorld().getEntities().stream()
            .filter(e -> e != p && e.getLocation().distance(p.getLocation()) <= 30)
            .filter(e -> e instanceof org.bukkit.entity.LivingEntity)
            .forEach(e -> {
                org.bukkit.entity.LivingEntity le = (org.bukkit.entity.LivingEntity) e;
                le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, true, false));
            });
    }

    // ── HELPERS ──────────────────────────────────────────────────────────
    private void applyEffect(Player p, PotionEffectType type, int amplifier) {
        p.addPotionEffect(new PotionEffect(type, 60, amplifier, true, false, false));
    }

    public static boolean isSword(Material m) {
        return m == Material.WOODEN_SWORD || m == Material.STONE_SWORD ||
               m == Material.IRON_SWORD || m == Material.GOLDEN_SWORD ||
               m == Material.DIAMOND_SWORD || m == Material.NETHERITE_SWORD;
    }

    public static boolean isAxe(Material m) {
        return m == Material.WOODEN_AXE || m == Material.STONE_AXE ||
               m == Material.IRON_AXE || m == Material.GOLDEN_AXE ||
               m == Material.DIAMOND_AXE || m == Material.NETHERITE_AXE;
    }

    public static boolean isTrident(Material m) {
        return m == Material.TRIDENT;
    }

    public static boolean isFruit(Material m) {
        return m == Material.APPLE || m == Material.GOLDEN_APPLE ||
               m == Material.ENCHANTED_GOLDEN_APPLE || m == Material.CARROT ||
               m == Material.GOLDEN_CARROT || m == Material.POTATO ||
               m == Material.BAKED_POTATO || m == Material.MELON_SLICE ||
               m == Material.SWEET_BERRIES || m == Material.GLOW_BERRIES ||
               m == Material.CHORUS_FRUIT;
    }
}
