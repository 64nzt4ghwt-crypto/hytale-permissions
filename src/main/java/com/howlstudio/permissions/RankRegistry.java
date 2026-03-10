package com.howlstudio.permissions;

import java.util.*;

public class RankRegistry {
    private static final Map<String, Rank> RANKS = new LinkedHashMap<>();

    static {
        register(new Rank("default", "Player", "§7[Player]§f", 0));
        register(new Rank("vip", "VIP", "§a[VIP]§f", 10,
            Rank.Permission.FLY, Rank.Permission.SET_HOME,
            Rank.Permission.COLOR_CHAT, Rank.Permission.BYPASS_COOLDOWN));
        register(new Rank("vip_plus", "VIP+", "§b[VIP+]§f", 20,
            Rank.Permission.FLY, Rank.Permission.GOD_MODE, Rank.Permission.VANISH,
            Rank.Permission.SET_HOME, Rank.Permission.WARP_ANY,
            Rank.Permission.COLOR_CHAT, Rank.Permission.BYPASS_COOLDOWN,
            Rank.Permission.BUILD_ANYWHERE));
        register(new Rank("helper", "Helper", "§e[Helper]§f", 50,
            Rank.Permission.FLY, Rank.Permission.MUTE_PLAYERS,
            Rank.Permission.COLOR_CHAT, Rank.Permission.BYPASS_COOLDOWN,
            Rank.Permission.TP_ANY, Rank.Permission.WARP_ANY));
        register(new Rank("mod", "Moderator", "§6[Mod]§f", 75,
            Rank.Permission.FLY, Rank.Permission.GOD_MODE, Rank.Permission.VANISH,
            Rank.Permission.MUTE_PLAYERS, Rank.Permission.KICK_PLAYERS,
            Rank.Permission.COLOR_CHAT, Rank.Permission.BYPASS_COOLDOWN,
            Rank.Permission.TP_ANY, Rank.Permission.WARP_ANY,
            Rank.Permission.BUILD_ANYWHERE, Rank.Permission.BYPASS_LAND_CLAIM));
        register(new Rank("admin", "Admin", "§c[Admin]§f", 100,
            Rank.Permission.values()));
    }

    private static void register(Rank r) { RANKS.put(r.getId(), r); }

    public static Rank getById(String id) { return RANKS.get(id); }
    public static Rank getDefault() { return RANKS.get("default"); }
    public static Collection<Rank> getAll() { return RANKS.values(); }

    public static List<Rank> getSortedByWeight() {
        List<Rank> list = new ArrayList<>(RANKS.values());
        list.sort(Comparator.comparingInt(Rank::getWeight));
        return list;
    }
}
