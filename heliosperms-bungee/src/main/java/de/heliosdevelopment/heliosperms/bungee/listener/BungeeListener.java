package de.heliosdevelopment.heliosperms.bungee.listener;

import de.heliosdevelopment.heliosperms.api.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.api.utils.TimeUnit;
import de.heliosdevelopment.heliosperms.bungee.Main;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeListener implements Listener {

    private final PlayerManager playerManager;

    public BungeeListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PostLoginEvent event) {
        PermissionPlayer permissionPlayer = playerManager.loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName(), true);
        if (Main.getInstance().getAdministrator().equalsIgnoreCase(event.getPlayer().getName())) {
            PermissionGroup adminGroup = playerManager.getGroupManager().getGroup(1);
            if (adminGroup != null && !permissionPlayer.getPermissionGroup().equals(adminGroup))
                playerManager.setGroup(permissionPlayer,
                        adminGroup, -1, TimeUnit.DAYS, "System", true);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        playerManager.unloadPlayer(event.getPlayer().getUniqueId());
    }

}
