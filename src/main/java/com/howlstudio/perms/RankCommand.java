package com.howlstudio.perms;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Arrays;
import java.util.UUID;

public class RankCommand extends AbstractPlayerCommand {
    private final PermissionsManager manager;

    public RankCommand(PermissionsManager manager) {
        super("rank", "Rank management. /rank <set|check|list|perms|create|grant|revoke>");
        this.manager = manager;
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        UUID uuid = playerRef.getUuid();
        if (uuid == null) return;
        manager.trackName(uuid, playerRef.getUsername() != null ? playerRef.getUsername() : "?");

        String input = ctx.getInputString().trim();
        String[] parts = input.split("\\s+");
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        if (args.length == 0) {
            // Show own rank
            showRank(playerRef, uuid);
            return;
        }

        // Most sub-commands require rank.* or admin
        if (!manager.hasPermission(uuid, "rank.*") && !manager.hasPermission(uuid, "*")) {
            // Non-admins can only check their own rank or /rank list
            if (!args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("check")) {
                playerRef.sendMessage(Message.raw("§c[Rank] No permission."));
                return;
            }
        }

        switch (args[0].toLowerCase()) {
            case "set" -> {
                if (args.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /rank set <player> <rank>")); return; }
                doSet(playerRef, args[1], args[2]);
            }
            case "check" -> {
                if (args.length < 2) { showRank(playerRef, uuid); return; }
                doCheck(playerRef, args[1]);
            }
            case "list" -> doList(playerRef);
            case "perms" -> {
                if (args.length < 2) { playerRef.sendMessage(Message.raw("§cUsage: /rank perms <rank_id>")); return; }
                doPerms(playerRef, args[1]);
            }
            case "create" -> {
                if (args.length < 4) { playerRef.sendMessage(Message.raw("§cUsage: /rank create <id> <display> <priority>")); return; }
                doCreate(playerRef, args[1], args[2], parseInt(args[3], 5));
            }
            case "grant" -> {
                if (args.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /rank grant <player> <permission>")); return; }
                doGrant(playerRef, args[1], args[2]);
            }
            case "revoke" -> {
                if (args.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /rank revoke <player> <permission>")); return; }
                doRevoke(playerRef, args[1], args[2]);
            }
            default -> sendHelp(playerRef);
        }
    }

    private void sendHelp(PlayerRef ref) {
        ref.sendMessage(Message.raw("§6[Rank] §eCommands:"));
        ref.sendMessage(Message.raw("§f/rank §7— Show your rank"));
        ref.sendMessage(Message.raw("§f/rank list §7— List all ranks"));
        ref.sendMessage(Message.raw("§f/rank check <player> §7— Check a player's rank"));
        ref.sendMessage(Message.raw("§f/rank set <player> <rank> §7— [Admin] Set rank"));
        ref.sendMessage(Message.raw("§f/rank perms <rank> §7— [Admin] List rank permissions"));
        ref.sendMessage(Message.raw("§f/rank create <id> <display> <priority> §7— [Admin] Create rank"));
        ref.sendMessage(Message.raw("§f/rank grant <player> <perm> §7— [Admin] Grant permission"));
        ref.sendMessage(Message.raw("§f/rank revoke <player> <perm> §7— [Admin] Revoke permission"));
    }

    private void showRank(PlayerRef ref, UUID uuid) {
        Rank rank = manager.getRank(uuid);
        String name = manager.getName(uuid);
        ref.sendMessage(Message.raw("§6[Rank] §f" + name + " → " + rank.getPrefix().trim() + " §7(id: " + rank.getId() + ", priority: " + rank.getPriority() + ")"));
    }

    private void doSet(PlayerRef ref, String targetName, String rankId) {
        UUID targetUuid = manager.findByName(targetName);
        if (targetUuid == null) { ref.sendMessage(Message.raw("§c[Rank] Player not found: §f" + targetName)); return; }
        Rank rank = RankRegistry.getById(rankId);
        if (rank == null) { ref.sendMessage(Message.raw("§c[Rank] Unknown rank: §f" + rankId + ". Use /rank list.")); return; }
        manager.setRank(targetUuid, rankId);
        ref.sendMessage(Message.raw("§6[Rank] §fSet §e" + targetName + "§f to rank §e" + rank.getDisplayName() + "§f."));
    }

    private void doCheck(PlayerRef ref, String targetName) {
        UUID targetUuid = manager.findByName(targetName);
        if (targetUuid == null) { ref.sendMessage(Message.raw("§c[Rank] Player not found: §f" + targetName)); return; }
        Rank rank = manager.getRank(targetUuid);
        ref.sendMessage(Message.raw("§6[Rank] §f" + targetName + " → " + rank.getPrefix().trim()
                + " §7(priority: " + rank.getPriority() + ")"));
        var extra = manager.getExtraPerms(targetUuid);
        if (!extra.isEmpty()) {
            ref.sendMessage(Message.raw("  §7Extra perms: §f" + String.join(", ", extra)));
        }
    }

    private void doList(PlayerRef ref) {
        ref.sendMessage(Message.raw("§6[Rank] §eAll Ranks (priority order):"));
        for (Rank r : RankRegistry.getSorted()) {
            ref.sendMessage(Message.raw("  §f" + r.getId() + " §7→ " + r.getPrefix().trim()
                    + " §7(priority: " + r.getPriority() + ", perms: " + r.getPermissions().size() + ")"));
        }
    }

    private void doPerms(PlayerRef ref, String rankId) {
        Rank rank = RankRegistry.getById(rankId);
        if (rank == null) { ref.sendMessage(Message.raw("§c[Rank] Unknown rank: §f" + rankId)); return; }
        ref.sendMessage(Message.raw("§6[Rank] §e" + rank.getDisplayName() + " §fPermissions:"));
        for (String p : rank.getPermissions()) {
            ref.sendMessage(Message.raw("  §7• §f" + p));
        }
    }

    private void doCreate(PlayerRef ref, String id, String display, int priority) {
        if (RankRegistry.getById(id) != null) {
            ref.sendMessage(Message.raw("§c[Rank] Rank already exists: §f" + id));
            return;
        }
        Rank newRank = new Rank(id, display, "§7[" + display + "] §f", "§7", priority);
        RankRegistry.addRank(newRank);
        ref.sendMessage(Message.raw("§6[Rank] §fCreated rank §e" + id + "§f (priority §f" + priority + "§f). Use /rank perms to configure."));
    }

    private void doGrant(PlayerRef ref, String targetName, String perm) {
        UUID targetUuid = manager.findByName(targetName);
        if (targetUuid == null) { ref.sendMessage(Message.raw("§c[Rank] Player not found: §f" + targetName)); return; }
        manager.grantPermission(targetUuid, perm);
        ref.sendMessage(Message.raw("§6[Rank] §fGranted §e" + perm + "§f to §e" + targetName + "§f."));
    }

    private void doRevoke(PlayerRef ref, String targetName, String perm) {
        UUID targetUuid = manager.findByName(targetName);
        if (targetUuid == null) { ref.sendMessage(Message.raw("§c[Rank] Player not found: §f" + targetName)); return; }
        manager.revokePermission(targetUuid, perm);
        ref.sendMessage(Message.raw("§6[Rank] §fRevoked §e" + perm + "§f from §e" + targetName + "§f."));
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
