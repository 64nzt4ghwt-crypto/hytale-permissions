package com.howlstudio.perms;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionsManager {
    // UUID → rank id
    private final Map<UUID, String> playerRanks = new ConcurrentHashMap<>();
    // UUID → display name
    private final Map<UUID, String> names = new ConcurrentHashMap<>();
    // UUID → individual permission overrides (granted explicitly)
    private final Map<UUID, Set<String>> extraPerms = new ConcurrentHashMap<>();

    public void trackName(UUID uuid, String name) { names.put(uuid, name); }
    public String getName(UUID uuid) { return names.getOrDefault(uuid, "?"); }

    public Rank getRank(UUID uuid) {
        String rankId = playerRanks.getOrDefault(uuid, "default");
        Rank rank = RankRegistry.getById(rankId);
        return rank != null ? rank : RankRegistry.getDefault();
    }

    public String getRankId(UUID uuid) {
        return playerRanks.getOrDefault(uuid, "default");
    }

    public void setRank(UUID uuid, String rankId) {
        playerRanks.put(uuid, rankId.toLowerCase());
    }

    public boolean hasPermission(UUID uuid, String permission) {
        // Check individual overrides first
        Set<String> extra = extraPerms.get(uuid);
        if (extra != null && (extra.contains("*") || extra.contains(permission.toLowerCase()))) {
            return true;
        }
        return getRank(uuid).hasPermission(permission);
    }

    public void grantPermission(UUID uuid, String perm) {
        extraPerms.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(perm.toLowerCase());
    }

    public void revokePermission(UUID uuid, String perm) {
        Set<String> extra = extraPerms.get(uuid);
        if (extra != null) extra.remove(perm.toLowerCase());
    }

    public Set<String> getExtraPerms(UUID uuid) {
        return extraPerms.getOrDefault(uuid, Set.of());
    }

    public UUID findByName(String name) {
        for (Map.Entry<UUID, String> e : names.entrySet()) {
            if (e.getValue().equalsIgnoreCase(name)) return e.getKey();
        }
        return null;
    }
}
