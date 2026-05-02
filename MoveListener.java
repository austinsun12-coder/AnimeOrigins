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

    private static final Attribute ATTR_ENTITY_REACH;
    private static final Attribute ATTR_BLOCK_REACH;

    static {
        Attribute er = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("player.entity_interaction_range"));
        if (er == null) er = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.entity_interaction_range"));
        ATTR_ENTITY_REACH = er;

        Attribute br = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("player.block_interaction_range"));
        if (br == null) br = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.block_interaction_range"));
        ATTR_BLOCK_REACH = br;
    }

    public static void clearAll(Player player) {
        removeModifier(player, ATTR_ENTITY_REACH,            KEY_REACH_ENTITY);
        removeModifier(player, ATTR_BLOCK_REACH,             KEY_REACH_BLOCK);
        removeModifier(player, Attribute.GENERIC_MAX_HEALTH, KEY_HEALTH);
    }

    public static void apply(Player player, Origin origin) {
        clearAll(player);
        switch (origin) {
            case LUFFY, WUKONG -> {
                addModifier(player, ATTR_ENTITY_REACH, KEY_REACH_ENTITY, 1.0,
                        AttributeModifier.Operation.ADD_NUMBER);
                addModifier(player, ATTR_BLOCK_REACH, KEY_REACH_BLOCK, 1.0,
                        AttributeModifier.Operation.ADD_NUMBER);
            }
            case TOJI -> {
                addModifier(player, Attribute.GENERIC_MAX_HEALTH, KEY_HEALTH, 20.0,
                        AttributeModifier.Operation.ADD_NUMBER);
                var inst = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (inst != null) player.setHealth(inst.getValue());
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
