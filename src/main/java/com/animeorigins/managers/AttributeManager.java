package com.animeorigins.managers;

import com.animeorigins.Origin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Manages persistent attribute modifiers for each origin.
 * Uses stable UUIDs so modifiers don't stack on re-login.
 */
public class AttributeManager {

    // Stable keys for each modifier so we can remove them cleanly
    private static final NamespacedKey KEY_REACH_ENTITY  = key("reach_entity");
    private static final NamespacedKey KEY_REACH_BLOCK   = key("reach_block");
    private static final NamespacedKey KEY_HEALTH        = key("bonus_health");

    private static NamespacedKey key(String name) {
        return new NamespacedKey("animeorigins", name);
    }

    /** Remove ALL AnimeOrigins modifiers from a player (call before applying new origin). */
    public static void clearAll(Player player) {
        removeModifier(player, Attribute.ENTITY_INTERACTION_RANGE, KEY_REACH_ENTITY);
        removeModifier(player, Attribute.BLOCK_INTERACTION_RANGE,  KEY_REACH_BLOCK);
        removeModifier(player, Attribute.MAX_HEALTH,               KEY_HEALTH);
    }

    /** Apply the correct attributes for the given origin. */
    public static void apply(Player player, Origin origin) {
        clearAll(player);

        switch (origin) {
            case LUFFY, WUKONG -> {
                // +2 block reach for stretchy / staff
                addModifier(player, Attribute.ENTITY_INTERACTION_RANGE, KEY_REACH_ENTITY, 2.0,
                        AttributeModifier.Operation.ADD_NUMBER);
                addModifier(player, Attribute.BLOCK_INTERACTION_RANGE, KEY_REACH_BLOCK, 2.0,
                        AttributeModifier.Operation.ADD_NUMBER);
            }
            case TOJI -> {
                // +10 hearts = +20 max health
                addModifier(player, Attribute.MAX_HEALTH, KEY_HEALTH, 20.0,
                        AttributeModifier.Operation.ADD_NUMBER);
            }
            default -> {} // nothing extra
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static void addModifier(Player player, Attribute attribute,
                                    NamespacedKey key, double amount,
                                    AttributeModifier.Operation op) {
        var inst = player.getAttribute(attribute);
        if (inst == null) return;
        // Remove old copy first to avoid stacking
        inst.getModifiers().stream()
            .filter(m -> m.getKey().equals(key))
            .forEach(inst::removeModifier);
        inst.addModifier(new AttributeModifier(key, amount, op));
    }

    private static void removeModifier(Player player, Attribute attribute, NamespacedKey key) {
        var inst = player.getAttribute(attribute);
        if (inst == null) return;
        inst.getModifiers().stream()
            .filter(m -> m.getKey().equals(key))
            .toList()
            .forEach(inst::removeModifier);
    }
}
