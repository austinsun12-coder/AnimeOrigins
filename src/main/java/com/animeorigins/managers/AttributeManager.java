package com.animeorigins.managers;

import com.animeorigins.Origin;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class AttributeManager {

    private static final NamespacedKey KEY_REACH_ENTITY = new NamespacedKey("animeorigins", "reach_entity");
    private static final NamespacedKey KEY_REACH_BLOCK  = new NamespacedKey("animeorigins", "reach_block");
    private static final NamespacedKey KEY_HEALTH       = new NamespacedKey("animeorigins", "bonus_health");

    // Looked up at runtime so we don't crash if a version doesn't have them
    private static final Attribute ATTR_ENTITY_REACH = Registry.ATTRIBUTE.get(
            NamespacedKey.minecraft("player.entity_interaction_range"));
    private static final Attribute ATTR_BLOCK_REACH  = Registry.ATTRIBUTE.get(
            NamespacedKey.minecraft("player.block_interaction_range"));
    private static final Attribute ATTR_MAX_HEALTH   = Registry.ATTRIBUTE.get(
            NamespacedKey.minecraft("generic.max_health"));

    public static void clearAll(Player player) {
        removeModifier(player, ATTR_ENTITY_REACH, KEY_REACH_ENTITY);
        removeModifier(player, ATTR_BLOCK_REACH,  KEY_REACH_BLOCK);
        removeModifier(player, ATTR_MAX_HEALTH,   KEY_HEALTH);
    }

    public static void apply(Player player, Origin origin) {
        clearAll(player);
        switch (origin) {
            case LUFFY, WUKONG -> {
                addModifier(player, ATTR_ENTITY_REACH, KEY_REACH_ENTITY, 2.0,
                        AttributeModifier.Operation.ADD_NUMBER);
                addModifier(player, ATTR_BLOCK_REACH, KEY_REACH_BLOCK, 2.0,
                        AttributeModifier.Operation.ADD_NUMBER);
            }
            case TOJI -> {
                addModifier(player, ATTR_MAX_HEALTH, KEY_HEALTH, 20.0,
                        AttributeModifier.Operation.ADD_NUMBER);
                // Make sure current health reflects new max
                var inst = player.getAttribute(ATTR_MAX_HEALTH);
                if (inst != null) player.setHealth(Math.min(player.getHealth(), inst.getValue()));
            }
            default -> {}
        }
    }

    private static void addModifier(Player player, Attribute attribute,
                                    NamespacedKey key, double amount,
                                    AttributeModifier.Operation op) {
        if (attribute == null) return;
        var inst = player.getAttribute(attribute);
        if (inst == null) return;
        inst.getModifiers().stream()
            .filter(m -> m.getKey().equals(key))
            .forEach(inst::removeModifier);
        inst.addModifier(new AttributeModifier(key, amount, op));
    }

    private static void removeModifier(Player player, Attribute attribute, NamespacedKey key) {
        if (attribute == null) return;
        var inst = player.getAttribute(attribute);
        if (inst == null) return;
        inst.getModifiers().stream()
            .filter(m -> m.getKey().equals(key))
            .toList()
            .forEach(inst::removeModifier);
    }
}

