package com.animeorigins;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.List;

public enum Origin {

    LUFFY("Monkey D. Luffy", "The Rubber Brawler", "🏴‍☠️", Material.SLIME_BALL,
        List.of(
            "§a✦ Gum-Gum Physiology: Immune to fall damage, 50% less knockback.",
            "§a✦ Stretchy Reach: +2 block attack/interact reach.",
            "§a✦ Gear Second [PRIMARY]: Speed III + Haste II for 10s. (85s CD)",
            "§c✦ Devil Fruit User: Cannot swim. Deep water = instant death.",
            "§c✦ Vulnerable to Slashing: Double damage from swords, axes, tridents."
        )),

    ITADORI("Yuji Itadori", "The Vessel", "👊", Material.IRON_CHESTPLATE,
        List.of(
            "§a✦ Superhuman Athletics: Permanent Speed I, Jump Boost II, armor toughness.",
            "§a✦ Black Flash: Bare fists deal 6 dmg. Crits deal 3x damage.",
            "§c✦ Sukuna's Host: Below 3 hearts → Blindness + Nausea until healed.",
            "§c✦ Cursed Tools Only: Cannot use bows, crossbows, or shields."
        )),

    GOKU("Son Goku", "The Martial Artist", "🐉", Material.DRAGON_EGG,
        List.of(
            "§a✦ Kamehameha [PRIMARY]: Explosive energy blast. (100s CD)",
            "§a✦ Zenkai Boost: Below 4 hearts → Strength II + Resistance I.",
            "§a✦ Instant Transmission [SECONDARY]: Teleport 15 blocks forward. (70s CD)",
            "§c✦ Saiyan Metabolism: Hunger depletes 3x faster.",
            "§c✦ Pure Heart: 50% less damage to passive/neutral mobs."
        )),

    TOJI("Toji Fushiguro", "The Sorcerer Killer", "⚔️", Material.NETHERITE_SWORD,
        List.of(
            "§a✦ Heavenly Restriction: Permanent Strength I, Speed II, +10 max hearts.",
            "§a✦ Undetectable: Invisible to Sculk Sensors/Wardens. Mobs notice at 5 blocks.",
            "§c✦ Zero Cursed Energy: Cannot use Enchanting Tables, brew potions, or Ender Pearls.",
            "§c✦ Heavy Footfalls: Sinks faster in water, uses air twice as quickly."
        )),

    ZORO("Roronoa Zoro", "The Master Swordsman", "🗡️", Material.DIAMOND_SWORD,
        List.of(
            "§a✦ Three-Sword Style: Holding a sword → Haste II + Strength I.",
            "§a✦ Iron Will: Survive lethal damage on ½ heart + Resistance III for 5s. (20min 40s CD)",
            "§c✦ Directionally Challenged: Compass spins wildly. Maps are blank. No F3 coords.",
            "§c✦ Clumsy Hands: Mine 50% slower unless using sword or axe."
        )),

    NARUTO("Naruto Uzumaki", "The Jinchuriki", "🌀", Material.ORANGE_DYE,
        List.of(
            "§a✦ Rasengan [PRIMARY]: Dash + strike, launches enemy 10 blocks up. (80s CD)",
            "§a✦ Kurama's Healing: Permanent Regeneration I.",
            "§a✦ Ninja Agility: Wall climbing. No fall damage.",
            "§c✦ Orange Jumpsuit: Mobs spot you 2x farther. Permanent Glowing.",
            "§c✦ Chakra Exhaustion: Rasengan drains massive hunger."
        )),

    WUKONG("Sun Wukong", "The Monkey King", "🐒", Material.STICK,
        List.of(
            "§a✦ Cloud Surfer [JUMP x2]: 5s creative flight, then glide. (70s CD)",
            "§a✦ Ruyi Jingu Bang: Stick = diamond sword damage + 3 reach.",
            "§a✦ 72 Transformations [PRIMARY]: Invisible + Speed III for 10s. (130s CD)",
            "§c✦ The Golden Headband: Cannot wear helmets (continuous damage).",
            "§c✦ Simian Diet: Can only eat fruits/veggies. Meat = Poison."
        )),

    GOJO("Satoru Gojo", "The Honored One", "🔵", Material.END_CRYSTAL,
        List.of(
            "§a✦ Limitless/Infinity [PRIMARY]: 5s complete immunity. (160s CD)",
            "§a✦ Six Eyes: Permanent Night Vision. Entities within 30 blocks glow.",
            "§a✦ Hollow Purple [SECONDARY]: Devastating projectile, breaks 3x3 tunnel. (340s CD)",
            "§c✦ Sensory Overload: Only leather cap allowed. Other helmets = Blindness.",
            "§c✦ Brain Fried: After Limitless/Hollow Purple → Slowness II + Weakness I for 8s."
        ));

    private final String displayName;
    private final String title;
    private final String emoji;
    private final Material guiItem;
    private final List<String> description;

    Origin(String displayName, String title, String emoji, Material guiItem, List<String> description) {
        this.displayName = displayName;
        this.title = title;
        this.emoji = emoji;
        this.guiItem = guiItem;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getTitle() { return title; }
    public String getEmoji() { return emoji; }
    public Material getGuiItem() { return guiItem; }
    public List<String> getDescription() { return description; }

    public Component getFormattedName() {
        return Component.text(emoji + " " + displayName)
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD);
    }

    public static Origin fromString(String name) {
        for (Origin o : values()) {
            if (o.name().equalsIgnoreCase(name) ||
                o.displayName.equalsIgnoreCase(name) ||
                o.name().equalsIgnoreCase(name.replace(" ", "_"))) {
                return o;
            }
        }
        return null;
    }
}
