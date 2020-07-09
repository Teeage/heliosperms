package de.heliosdevelopment.heliosperms.bungee;

import de.heliosdevelopment.heliosperms.api.manager.BungeeUpdater;
import de.heliosdevelopment.heliosperms.api.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.api.utils.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class ExpirationHandler {

    private final PlayerManager playerManager;

    public ExpirationHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
        runExpirationTimer();
    }

    private void runExpirationTimer() {
        PermissionGroup defaultGroup = playerManager.getGroupManager().getDefaultGroup();
        if (defaultGroup == null) throw new NullPointerException("Could not find the default group!");
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
            for (PermissionPlayer permissionPlayer : playerManager.getPlayers()) {
                if (permissionPlayer.getExpiration() == -1) continue;
                if ((permissionPlayer.getExpiration() - System.currentTimeMillis()) <= 0) {
                    int oldGroupId = permissionPlayer.getPermissionGroup().getGroupId();
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(permissionPlayer.getUuid());
                    if (proxiedPlayer != null)
                        playerManager.setGroup(permissionPlayer, defaultGroup, -1, TimeUnit.DAYS, "System", true);
                    BungeeUpdater.updateGroup(permissionPlayer.getUuid().toString(), oldGroupId, defaultGroup.getGroupId());
                }
            }
        }, 30, 30, java.util.concurrent.TimeUnit.MINUTES);
    }
}
