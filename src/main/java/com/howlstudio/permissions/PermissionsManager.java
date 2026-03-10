package com.howlstudio.permissions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionsManager {
    // UUID → Rank ID
    private final Map<UUID, String> playerRanks = new ConcurrentHashMap<>();
    private final Map<UUID, String> names = new ConcurrentHashMap<>();

    public void trackName(UUID uuid, String name) { names.put(uuid, name); }
    public String getName(UUID uuid) { return names.getOrDefault(uuid, uuid.toString().substring(0, 8)); }

    public UUID findByName(String name) {
        for (Map.Entry<UUID, String> e : names.entrySet()) {
            if (e.getValue().equalsIgnoreCase(name)) return e.getKey();
        }
        return null;
    }

    public Rank getRank(UUID uuid) {
        String id = playerRanks.getOrDefault(uuid, "default");
        Rank r = RankRegistry.getById(id);
        return r != null ? r : RankRegistry.getDefault();
    }

    public void setRank(UUID uuid, Rank rank) {
        playerRanks.put(uuid, rank.getId());
    }

    public boolean hasPermission(UUID uuid, Rank.Permission permission) {
        return getRank(uuid).hasPermission(permission);
    }

    public void onLeave(UUID uuid) {
        // Keep rank data; in production would persist to disk here
    }
}
