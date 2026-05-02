package com.animeorigins.managers;

import com.animeorigins.AnimeOriginsPlugin;
import com.animeorigins.Origin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OriginManager {

    private final AnimeOriginsPlugin plugin;
    private final Map<UUID, Origin> playerOrigins = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public OriginManager(AnimeOriginsPlugin plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    private void loadAll() {
        if (dataConfig.contains("players")) {
            for (String key : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String originName = dataConfig.getString("players." + key);
                    Origin origin = Origin.valueOf(originName);
                    playerOrigins.put(uuid, origin);
                } catch (Exception ignored) {}
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Origin> entry : playerOrigins.entrySet()) {
            dataConfig.set("players." + entry.getKey().toString(), entry.getValue().name());
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void setOrigin(Player player, Origin origin) {
        playerOrigins.put(player.getUniqueId(), origin);
        dataConfig.set("players." + player.getUniqueId(), origin.name());
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public Origin getOrigin(Player player) {
        return playerOrigins.get(player.getUniqueId());
    }

    public boolean hasOrigin(Player player) {
        return playerOrigins.containsKey(player.getUniqueId());
    }

    public void resetOrigin(Player player) {
        playerOrigins.remove(player.getUniqueId());
        dataConfig.set("players." + player.getUniqueId(), null);
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public int getCharacterCount() {
        return Origin.values().length;
    }
}
