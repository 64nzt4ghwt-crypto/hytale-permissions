package com.howlstudio.perms;

import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public final class PermissionsPlugin extends JavaPlugin {

    private PermissionsManager permissionsManager;

    public PermissionsPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[PermissionsRanks] Loading...");
        permissionsManager = new PermissionsManager();
        new PermListener(permissionsManager).register();
        CommandManager.get().register(new RankCommand(permissionsManager));
        System.out.println("[PermissionsRanks] Ready. 6 default ranks. /rank for info.");
    }
}
