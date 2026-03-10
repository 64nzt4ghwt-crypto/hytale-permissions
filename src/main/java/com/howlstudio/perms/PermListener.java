package com.howlstudio.perms;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.UUID;

public class PermListener {
    private final PermissionsManager manager;

    public PermListener(PermissionsManager manager) {
        this.manager = manager;
    }

    public void register() {
        var bus = HytaleServer.get().getEventBus();
        bus.registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        bus.registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        bus.registerGlobal(PlayerChatEvent.class, this::onPlayerChat);
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        PlayerRef ref = player.getPlayerRef();
        if (ref == null) return;
        UUID uuid = ref.getUuid();
        String name = ref.getUsername() != null ? ref.getUsername() : (uuid != null ? uuid.toString().substring(0, 8) : "?");
        if (uuid != null) {
            manager.trackName(uuid, name);
            Rank rank = manager.getRank(uuid);
            if (!rank.getPrefix().isBlank()) {
                ref.sendMessage(Message.raw("§7Rank: " + rank.getPrefix().trim() + " §7(priority " + rank.getPriority() + ")"));
            }
        }
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef ref = event.getPlayerRef();
        if (ref == null || ref.getUuid() == null) return;
        // Could persist rank here
    }

    private void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) return;
        PlayerRef sender = event.getSender();
        if (sender == null || sender.getUuid() == null) return;

        UUID uuid = sender.getUuid();
        String name = sender.getUsername() != null ? sender.getUsername() : manager.getName(uuid);
        Rank rank = manager.getRank(uuid);
        String content = event.getContent();
        if (content == null) return;

        // Format chat with rank prefix if they have one
        if (!rank.getPrefix().isBlank()) {
            event.setContent(content); // content stays same
            // Would set formatter here for full prefix support — prefix shown via formatter
        }
    }
}
