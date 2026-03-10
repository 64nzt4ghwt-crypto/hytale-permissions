package com.howlstudio.permissions;

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
        super("rank", "Rank management. /rank <set|info|list|myrank>");
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
            doMyRank(playerRef, uuid);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "myrank" -> doMyRank(playerRef, uuid);
            case "set" -> {
                // Requires MANAGE_RANKS
                if (!manager.hasPermission(uuid, Rank.Permission.MANAGE_RANKS)) {
                    playerRef.sendMessage(Message.raw("§c[Ranks] You don't have permission to set ranks."));
                    return;
                }
                if (args.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /rank set <player> <rank_id>")); return; }
                doSetRank(playerRef, uuid, args[1], args[2]);
            }
            case "info" -> {
                if (args.length < 2) { playerRef.sendMessage(Message.raw("§cUsage: /rank info <rank_id>")); return; }
                doRankInfo(playerRef, args[1]);
            }
            case "list" -> doRankList(playerRef);
            case "check" -> {
                if (args.length < 2) { playerRef.sendMessage(Message.raw("§cUsage: /rank check <player>")); return; }
                doCheckPlayer(playerRef, args[1]);
            }
            default -> sendHelp(playerRef, uuid);
        }
    }

    private void sendHelp(PlayerRef ref, UUID uuid) {
        ref.sendMessage(Message.raw("§6[Ranks] §eCommands:"));
        ref.sendMessage(Message.raw("§f/rank §7— Show your rank"));
        ref.sendMessage(Message.raw("§f/rank list §7— List all ranks"));
        ref.sendMessage(Message.raw("§f/rank info <id> §7— Show rank permissions"));
        ref.sendMessage(Message.raw("§f/rank check <player> §7— Show player's rank"));
        if (manager.hasPermission(uuid, Rank.Permission.MANAGE_RANKS)) {
            ref.sendMessage(Message.raw("§f/rank set <player> <rank> §7— Set player rank (admin)"));
        }
    }

    private void doMyRank(PlayerRef ref, UUID uuid) {
        Rank rank = manager.getRank(uuid);
        ref.sendMessage(Message.raw("§6[Ranks] §fYour rank: " + rank.getPrefix() + " §e" + rank.getDisplayName()
                + " §7(weight: " + rank.getWeight() + ")"));
        if (!rank.getPermissions().isEmpty()) {
            ref.sendMessage(Message.raw("  §7Perms: " + rank.getPermissions().size() + " granted"));
        }
    }

    private void doSetRank(PlayerRef ref, UUID actorUuid, String targetName, String rankId) {
        UUID targetUuid = manager.findByName(targetName);
        if (targetUuid == null) { ref.sendMessage(Message.raw("§c[Ranks] Player not found: §f" + targetName)); return; }

        Rank newRank = RankRegistry.getById(rankId);
        if (newRank == null) { ref.sendMessage(Message.raw("§c[Ranks] Unknown rank: §f" + rankId + "§c. Use /rank list.")); return; }

        Rank actorRank = manager.getRank(actorUuid);
        if (newRank.getWeight() >= actorRank.getWeight() && !actorRank.getId().equals("admin")) {
            ref.sendMessage(Message.raw("§c[Ranks] Can't assign a rank equal or higher than your own."));
            return;
        }

        manager.setRank(targetUuid, newRank);
        ref.sendMessage(Message.raw("§6[Ranks] §fSet §e" + targetName + "§f to " + newRank.getPrefix() + "§f."));
    }

    private void doRankInfo(PlayerRef ref, String rankId) {
        Rank rank = RankRegistry.getById(rankId);
        if (rank == null) { ref.sendMessage(Message.raw("§c[Ranks] Unknown rank: §f" + rankId)); return; }
        ref.sendMessage(Message.raw("§6[Ranks] " + rank.getPrefix() + " §e" + rank.getDisplayName()
                + " §7(weight: " + rank.getWeight() + ")"));
        if (rank.getPermissions().isEmpty()) {
            ref.sendMessage(Message.raw("  §7No special permissions."));
        } else {
            StringBuilder sb = new StringBuilder("  §7Perms: ");
            for (Rank.Permission p : rank.getPermissions()) {
                sb.append("§f").append(p.name().toLowerCase()).append("§7, ");
            }
            ref.sendMessage(Message.raw(sb.toString().replaceAll(", $", "")));
        }
    }

    private void doRankList(PlayerRef ref) {
        ref.sendMessage(Message.raw("§6[Ranks] §eAll Ranks:"));
        for (Rank rank : RankRegistry.getSortedByWeight()) {
            ref.sendMessage(Message.raw("  §f" + rank.getId() + " — " + rank.getPrefix()
                    + " §7(weight: " + rank.getWeight() + ", " + rank.getPermissions().size() + " perms)"));
        }
    }

    private void doCheckPlayer(PlayerRef ref, String targetName) {
        UUID targetUuid = manager.findByName(targetName);
        if (targetUuid == null) { ref.sendMessage(Message.raw("§c[Ranks] Player not found: §f" + targetName)); return; }
        Rank rank = manager.getRank(targetUuid);
        ref.sendMessage(Message.raw("§6[Ranks] §f" + targetName + " is " + rank.getPrefix() + " §e" + rank.getDisplayName()));
    }
}
