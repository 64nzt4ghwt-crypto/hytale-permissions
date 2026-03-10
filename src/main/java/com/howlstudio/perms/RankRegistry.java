package com.howlstudio.perms;

import java.util.*;

public class RankRegistry {
    private static final Map<String, Rank> ranks = new LinkedHashMap<>();

    static {
        // Default ranks — servers can modify via /rank commands
        Rank owner = new Rank("owner", "Owner", "§4[Owner] §f", "§c", 100);
        owner.addPermission("*");
        register(owner);

        Rank admin = new Rank("admin", "Admin", "§c[Admin] §f", "§c", 90);
        admin.addPermission("rank.*");
        admin.addPermission("chatmod.*");
        admin.addPermission("spawn.set");
        admin.addPermission("warp.create");
        register(admin);

        Rank mod = new Rank("mod", "Moderator", "§9[Mod] §f", "§9", 70);
        mod.addPermission("chatmod.mute");
        mod.addPermission("chatmod.unmute");
        mod.addPermission("teleport.to");
        register(mod);

        Rank vip = new Rank("vip", "VIP", "§6[VIP] §f", "§e", 30);
        vip.addPermission("home.set.multiple");
        vip.addPermission("warp.use");
        vip.addPermission("chat.color");
        register(vip);

        Rank member = new Rank("member", "Member", "§7[Member] §f", "§f", 10);
        member.addPermission("home.set");
        member.addPermission("home.go");
        member.addPermission("spawn.go");
        member.addPermission("trade.use");
        register(member);

        Rank defaultRank = new Rank("default", "Player", "", "§f", 0);
        defaultRank.addPermission("spawn.go");
        register(defaultRank);
    }

    private static void register(Rank r) { ranks.put(r.getId(), r); }

    public static Rank getById(String id) { return ranks.get(id.toLowerCase()); }
    public static Collection<Rank> getAll() { return ranks.values(); }

    public static void addRank(Rank r) { ranks.put(r.getId(), r); }
    public static boolean removeRank(String id) {
        if (id.equals("default")) return false; // can't remove default
        return ranks.remove(id) != null;
    }

    public static Rank getDefault() { return ranks.getOrDefault("default", ranks.values().iterator().next()); }

    public static List<Rank> getSorted() {
        List<Rank> sorted = new ArrayList<>(ranks.values());
        sorted.sort((a, b) -> b.getPriority() - a.getPriority());
        return sorted;
    }
}
