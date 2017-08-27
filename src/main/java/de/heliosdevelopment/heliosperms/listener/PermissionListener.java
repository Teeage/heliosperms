package de.heliosdevelopment.heliosperms.listener;

import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionListener implements Listener {

    private final PlayerManager playerManager;

    public PermissionListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            PermissionPlayer permissionPlayer = playerManager.getPlayer(((ProxiedPlayer) event.getSender()).getUniqueId());
            if (permissionPlayer != null)
                if (permissionPlayer.hasPermission(event.getPermission()))
                    event.setHasPermission(true);
        }

    }
}
