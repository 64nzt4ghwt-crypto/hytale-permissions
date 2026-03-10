package com.howlstudio.permissions;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Rank {
    public enum Permission {
        // General
        FLY, GOD_MODE, VANISH,
        // Teleport
        TP_ANY, SET_HOME, WARP_ANY,
        // Economy
        GIVE_MONEY, RESET_ECONOMY,
        // Chat
        MUTE_PLAYERS, COLOR_CHAT, BYPASS_COOLDOWN,
        // Building / World
        BUILD_ANYWHERE, BYPASS_LAND_CLAIM,
        // Admin
        KICK_PLAYERS, BAN_PLAYERS, OP_COMMANDS, MANAGE_RANKS
    }

    private final String id;
    private final String displayName;
    private final String prefix;        // e.g. "§6[VIP]§f"
    private final int weight;           // higher = more powerful; used for promotion checks
    private final Set<Permission> permissions;

    public Rank(String id, String displayName, String prefix, int weight, Permission... perms) {
        this.id = id;
        this.displayName = displayName;
        this.prefix = prefix;
        this.weight = weight;
        this.permissions = perms.length > 0
            ? Collections.unmodifiableSet(EnumSet.of(perms[0], perms))
            : Collections.emptySet();
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getPrefix() { return prefix; }
    public int getWeight() { return weight; }

    public boolean hasPermission(Permission p) { return permissions.contains(p); }
    public Set<Permission> getPermissions() { return permissions; }
}
