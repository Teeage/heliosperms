package de.heliosdevelopment.heliosperms.manager;

import de.heliosdevelopment.heliosperms.bungee.Main;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class ExpirationHandler {

    private final PlayerManager playerManager;

    public ExpirationHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
        runExpirationTimer();
    }

    private void runExpirationTimer() {
        PermissionGroup userGroup = playerManager.getGroupManager().getDefaultGroup();
        if (userGroup == null) throw new NullPointerException("Could not find the default group!");
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
            for (PermissionPlayer permissionPlayer : playerManager.getPlayers()) {
                if (permissionPlayer.getExpiration() == -1) continue;
                if ((permissionPlayer.getExpiration() - System.currentTimeMillis()) <= 0) {
                    permissionPlayer.setPermissionGroup(userGroup);
                    permissionPlayer.setExpiration(-1);
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(permissionPlayer.getUuid());
                    if (proxiedPlayer != null)
                        Main.getInstance().getDatabaseHandler().updateUser(permissionPlayer.getUuid().toString(), proxiedPlayer.getName(), userGroup.getGroupId(), (long) -1);
                    BungeeUpdater.updateGroup(permissionPlayer.getUuid().toString(), userGroup.getGroupId());
                }
            }
        }, 30, 30, TimeUnit.MINUTES);
    }
}
