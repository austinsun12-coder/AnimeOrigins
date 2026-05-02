package com.animeorigins.commands;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OriginAdminCommand implements CommandExecutor {

    private final AnimeOriginsPlugin plugin;

    public OriginAdminCommand(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("animeorigins.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /originadmin <set|reset> <player> [character]");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = plugin.getServer().getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + args[1]);
            return true;
        }

        switch (action) {
            case "reset" -> {
                plugin.getOriginManager().resetOrigin(target);
                sender.sendMessage("§aReset origin for " + target.getName());
                target.sendMessage("§7Your origin has been reset by an admin.");
            }
            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /originadmin set <player> <character>");
                    return true;
                }
                Origin origin = Origin.fromString(args[2]);
                if (origin == null) {
                    sender.sendMessage("§cUnknown origin: " + args[2]);
                    return true;
                }
                plugin.getOriginManager().setOrigin(target, origin);
                sender.sendMessage("§aSet " + target.getName() + "'s origin to " + origin.getDisplayName());
                target.sendMessage("§aYour origin has been set to §e" + origin.getEmoji() + " " + origin.getDisplayName() + " §aby an admin.");
            }
            default -> sender.sendMessage("§cUnknown action. Use: set or reset");
        }

        return true;
    }
}
