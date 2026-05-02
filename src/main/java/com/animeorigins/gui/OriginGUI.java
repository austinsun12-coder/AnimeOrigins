package com.animeorigins.gui;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OriginGUI implements InventoryHolder {

    // Slot layout for 8 characters in a 54-slot chest (6 rows)
    // Row 1: border  Row 2-4: characters (4 per row, centred)  Row 5: border  Row 6: info
    private static final int[] CHARACTER_SLOTS = {10, 12, 14, 16, 28, 30, 32, 34};

    private final Inventory inventory;
    private final AnimeOriginsPlugin plugin;

    public OriginGUI(AnimeOriginsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54,
            Component.text("✦ Choose Your Origin ✦").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        populate(player);
    }

    private void populate(Player player) {
        Origin currentOrigin = plugin.getOriginManager().getOrigin(player);

        // Fill borders with grey glass
        ItemStack border = makeBorderPane();
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, border);
        }

        // Place each character
        Origin[] origins = Origin.values();
        for (int i = 0; i < origins.length && i < CHARACTER_SLOTS.length; i++) {
            Origin origin = origins[i];
            boolean isSelected = origin == currentOrigin;
            inventory.setItem(CHARACTER_SLOTS[i], makeCharacterItem(origin, isSelected));
        }

        // Bottom row info items
        inventory.setItem(45, makeInfoItem(Material.BOOK,
            "§e§lHow to use",
            List.of("§7Click a character to", "§7select your origin.", "", "§eSneak + Right-click §7= Primary",
                    "§eSneak + Left-click §7= Secondary", "§eDouble Jump §7= Wukong flight")));

        if (currentOrigin != null) {
            inventory.setItem(49, makeInfoItem(Material.NETHER_STAR,
                "§a§lCurrent: " + currentOrigin.getDisplayName(),
                List.of("§7You are currently playing as", "§e" + currentOrigin.getDisplayName(),
                        "§8" + currentOrigin.getTitle(), "",
                        plugin.getConfig().getBoolean("settings.allow-reselect", true)
                            ? "§7Click another character to reselect." : "§cOrigin is locked.")));
        } else {
            inventory.setItem(49, makeInfoItem(Material.COMPASS,
                "§c§lNo Origin Selected",
                List.of("§7You haven't chosen an origin.", "§7Click a character below to begin!")));
        }

        inventory.setItem(53, makeInfoItem(Material.BARRIER,
            "§c§lClose",
            List.of("§7Click to close this menu.")));
    }

    private ItemStack makeCharacterItem(Origin origin, boolean selected) {
        ItemStack item = new ItemStack(origin.getGuiItem());
        ItemMeta meta = item.getItemMeta();

        // Name
        meta.displayName(Component.text(origin.getEmoji() + " " + origin.getDisplayName())
            .color(selected ? NamedTextColor.GREEN : NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));

        // Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(origin.getTitle())
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());

        for (String line : origin.getDescription()) {
            lore.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());
        if (selected) {
            lore.add(Component.text("✔ Currently Selected")
                .color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Click to choose this origin!")
                .color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS,
                          ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        // Glow effect if selected
        if (selected) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack makeBorderPane() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(" ").decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack makeInfoItem(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public static int[] getCharacterSlots() {
        return CHARACTER_SLOTS;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
