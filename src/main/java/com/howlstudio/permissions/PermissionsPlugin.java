package com.howlstudio.permissions;

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
        new PermissionsListener(permissionsManager).register();
        CommandManager.get().register(new RankCommand(permissionsManager));
        System.out.println("[PermissionsRanks] Ready. " + RankRegistry.getAll().size()
                + " ranks loaded. /rank available.");
    }
}
