package com.animeorigins.commands;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import com.animeorigins.gui.OriginGUI;
import com.animeorigins.listeners.PlayerJoinListener;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OriginCommand implements CommandExecutor {

    private final AnimeOriginsPlugin plugin;

    public OriginCommand(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command is for players only.");
            return true;
        }

        // /origin with no args → open GUI
        if (args.length == 0) {
            new OriginGUI(plugin, player).open(player);
            return true;
        }

        // /origin info → show current origin info in chat
        if (args[0].equalsIgnoreCase("info")) {
            Origin current = plugin.getOriginManager().getOrigin(player);
            if (current == null) {
                player.sendMessage("§cYou haven't chosen an origin yet. Type §e/origin §cto open the selection menu.");
            } else {
                PlayerJoinListener.sendOriginInfo(player, current);
            }
            return true;
        }

        // /origin <name> → select by name (text fallback)
        String originArg = String.join(" ", args);
        Origin chosen = Origin.fromString(originArg);

        if (chosen == null) {
            player.sendMessage("§cUnknown origin: §e" + originArg);
            player.sendMessage("§7Available: §eluffy, itadori, goku, toji, zoro, naruto, wukong, gojo");
            player.sendMessage("§7Or just type §e/origin §7to open the selection GUI.");
            return true;
        }

        Origin current = plugin.getOriginManager().getOrigin(player);
        if (current != null && !plugin.getConfig().getBoolean("settings.allow-reselect", true)) {
            player.sendMessage("§cYou have already chosen an origin and cannot change it!");
            return true;
        }

        plugin.getOriginManager().setOrigin(player, chosen);
        applyOriginSetup(player, chosen);

        if (plugin.getConfig().getBoolean("settings.broadcast-selection", true)) {
            plugin.getServer().broadcast(Component.text(
                "§6" + player.getName() + " §7has chosen the origin of §e" + chosen.getEmoji() + " " + chosen.getDisplayName() + "§7!"));
        }

        PlayerJoinListener.sendOriginInfo(player, chosen);
        return true;
    }

    public static void applyOriginSetup(Player player, Origin origin) {
        // Apply attribute modifiers (reach, health etc.)
        com.animeorigins.managers.AttributeManager.apply(player, origin);

        // Flight flag for Wukong double-jump
        if (origin == Origin.WUKONG) {
            if (player.getGameMode() != GameMode.CREATIVE) player.setAllowFlight(true);
        } else {
            if (player.getGameMode() != GameMode.CREATIVE) player.setAllowFlight(false);
        }
    }
}
