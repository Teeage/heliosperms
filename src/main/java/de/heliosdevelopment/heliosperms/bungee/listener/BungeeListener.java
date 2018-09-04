package de.heliosdevelopment.heliosperms.bungee.listener;

import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.bungee.Main;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeListener implements Listener {

    private final PlayerManager playerManager;
    private final MySQL mysql;

    public BungeeListener(PlayerManager playerManager, MySQL mysql) {
        this.playerManager = playerManager;
        this.mysql = mysql;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PostLoginEvent event) {
        PermissionPlayer permissionPlayer = playerManager.loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName(), true);
        if (Main.getInstance().getAdministrator().equalsIgnoreCase(event.getPlayer().getName())) {
            permissionPlayer.setPermissionGroup(playerManager.getGroupManager().getGroup(1));
            mysql.updateUser(event.getPlayer().getUniqueId().toString(), Main.getInstance().getAdministrator(), 1, (long) -1);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        playerManager.unloadPlayer(event.getPlayer().getUniqueId());
    }

}
