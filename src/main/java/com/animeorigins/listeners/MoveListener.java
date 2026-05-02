package com.animeorigins.listeners;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private final AnimeOriginsPlugin plugin;

    public MoveListener(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Origin origin = plugin.getOriginManager().getOrigin(player);
        if (origin == null) return;

        if (origin == Origin.NARUTO) {
            // Wall climbing: if touching a wall and pressing against it, negate gravity
            if (isTouchingWall(player) && player.isSprinting()) {
                player.setVelocity(player.getVelocity().setY(0.3));
            }
        }
    }

    private boolean isTouchingWall(Player player) {
        var loc = player.getLocation();
        var block = loc.getBlock();
        return block.getRelative(1, 0, 0).getType().isSolid() ||
               block.getRelative(-1, 0, 0).getType().isSolid() ||
               block.getRelative(0, 0, 1).getType().isSolid() ||
               block.getRelative(0, 0, -1).getType().isSolid();
    }
}
