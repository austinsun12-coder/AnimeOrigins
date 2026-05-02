package com.animeorigins;

import com.animeorigins.commands.OriginAdminCommand;
import com.animeorigins.commands.OriginCommand;
import com.animeorigins.gui.GUIListener;
import com.animeorigins.listeners.*;
import com.animeorigins.managers.CooldownManager;
import com.animeorigins.managers.OriginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimeOriginsPlugin extends JavaPlugin {

    private static AnimeOriginsPlugin instance;
    private OriginManager originManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        originManager = new OriginManager(this);
        cooldownManager = new CooldownManager();

        // Register listeners
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EquipmentListener(this), this);
        getServer().getPluginManager().registerEvents(new FoodListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);

        // Register commands
        getCommand("origin").setExecutor(new OriginCommand(this));
        getCommand("originadmin").setExecutor(new OriginAdminCommand(this));

        // Start passive effect tick
        getServer().getScheduler().runTaskTimer(this, new PassiveTicker(this), 20L, 20L);

        getLogger().info("AnimeOrigins enabled! " + originManager.getCharacterCount() + " characters loaded.");
    }

    @Override
    public void onDisable() {
        originManager.saveAll();
        getLogger().info("AnimeOrigins disabled.");
    }

    public static AnimeOriginsPlugin getInstance() { return instance; }
    public OriginManager getOriginManager() { return originManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
}
