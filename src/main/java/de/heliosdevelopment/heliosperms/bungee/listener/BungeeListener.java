package de.heliosdevelopment.heliosperms.bungee.listener;

import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeListener implements Listener{

    private final PlayerManager playerManager;

    public BungeeListener(PlayerManager playerManager){
        this.playerManager = playerManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PostLoginEvent event) {
        PermissionPlayer permissionPlayer = playerManager.loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void onJoin(PlayerDisconnectEvent event) {
        playerManager.unloadPlayer(event.getPlayer().getUniqueId());
    }

}
