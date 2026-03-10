package com.howlstudio.permissions;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.UUID;

public class PermissionsListener {
    private final PermissionsManager manager;

    public PermissionsListener(PermissionsManager manager) {
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
            if (!rank.getId().equals("default")) {
                ref.sendMessage(Message.raw("§6[Ranks] §fWelcome back, " + rank.getPrefix() + " §f" + name + "§f!"));
            }
        }
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef ref = event.getPlayerRef();
        if (ref == null) return;
        UUID uuid = ref.getUuid();
        if (uuid != null) manager.onLeave(uuid);
    }

    private void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) return;
        PlayerRef sender = event.getSender();
        if (sender == null) return;
        UUID uuid = sender.getUuid();
        if (uuid == null) return;

        Rank rank = manager.getRank(uuid);
        if (!rank.getId().equals("default")) {
            // Prepend rank prefix to chat message
            String original = event.getContent();
            String name = sender.getUsername() != null ? sender.getUsername() : "?";
            event.setContent(rank.getPrefix() + " §f" + name + "§7: " + original);
        }
    }
}
