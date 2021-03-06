package de.heliosdevelopment.heliosperms.listener;

import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class PermissionListener implements Listener {

    private final PlayerManager playerManager;

    public PermissionListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            Optional<PermissionPlayer> permissionPlayer = playerManager.getPlayer(((ProxiedPlayer) event.getSender()).getUniqueId());
            if (permissionPlayer.isPresent())
                if (permissionPlayer.get().hasPermission(event.getPermission()))
                    event.setHasPermission(true);
        }

    }
}
