package com.howlstudio.perms;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Rank {
    private final String id;
    private String displayName;
    private String prefix;        // e.g. "§6[VIP] §f"
    private String color;         // e.g. "§a"
    private int priority;         // higher = more powerful; default 0
    private final Set<String> permissions = new HashSet<>();

    public Rank(String id, String displayName, String prefix, String color, int priority) {
        this.id = id;
        this.displayName = displayName;
        this.prefix = prefix;
        this.color = color;
        this.priority = priority;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getPrefix() { return prefix; }
    public String getColor() { return color; }
    public int getPriority() { return priority; }

    public void setDisplayName(String n) { this.displayName = n; }
    public void setPrefix(String p) { this.prefix = p; }
    public void setColor(String c) { this.color = c; }
    public void setPriority(int p) { this.priority = p; }

    public void addPermission(String perm) { permissions.add(perm.toLowerCase()); }
    public void removePermission(String perm) { permissions.remove(perm.toLowerCase()); }
    public boolean hasPermission(String perm) {
        return permissions.contains("*") || permissions.contains(perm.toLowerCase());
    }
    public Set<String> getPermissions() { return Collections.unmodifiableSet(permissions); }

    public String format(String playerName, String message) {
        return prefix + playerName + "§f: " + message;
    }
}
