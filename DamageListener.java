package com.animeorigins.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // key: uuid + ":" + abilityName  value: System.currentTimeMillis() when used
    private final Map<String, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID uuid, String ability, int seconds) {
        cooldowns.put(uuid + ":" + ability, System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean isOnCooldown(UUID uuid, String ability) {
        Long expiry = cooldowns.get(uuid + ":" + ability);
        if (expiry == null) return false;
        return System.currentTimeMillis() < expiry;
    }

    public long getRemainingSeconds(UUID uuid, String ability) {
        Long expiry = cooldowns.get(uuid + ":" + ability);
        if (expiry == null) return 0;
        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? (remaining / 1000) + 1 : 0;
    }

    public void clearCooldown(UUID uuid, String ability) {
        cooldowns.remove(uuid + ":" + ability);
    }
}
