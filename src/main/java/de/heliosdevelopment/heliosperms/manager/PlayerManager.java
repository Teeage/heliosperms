package de.heliosdevelopment.heliosperms.manager;

import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.utils.PermissionType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {

    private final List<PermissionPlayer> players = new ArrayList<>();
    private final MySQL mysql;
    private final GroupManager groupManager;

    public PlayerManager(MySQL mysql, GroupManager groupManager) {
        this.mysql = mysql;
        this.groupManager = groupManager;
    }

    public PermissionPlayer loadPlayer(UUID uuid, String name, boolean bungee) {
        String expiration = mysql.getExpiration(uuid.toString()) == null ? String.valueOf(-1) : mysql.getExpiration(uuid.toString());
        PermissionGroup group = groupManager.getGroup(mysql.getGroup(uuid.toString()));
        PermissionPlayer permissionPlayer = new PermissionPlayer(uuid, name,
                group != null ? group : groupManager.getDefaultGroup(),
                mysql.getPermissions(uuid.toString(), PermissionType.USER),
                Long.valueOf(expiration));
        players.add(permissionPlayer);
        if (mysql.getGroup(uuid.toString()) == -1 && bungee)
            mysql.addUser(uuid.toString(), name, 20, (long) -1);
        return permissionPlayer;
    }

    public void unloadPlayer(UUID uuid) {
        Optional<PermissionPlayer> permissionPlayer = getPlayer(uuid);
        if (permissionPlayer.isPresent())
            players.remove(permissionPlayer.get());
    }

    public Optional<PermissionPlayer> getPlayer(UUID uuid) {
        for (PermissionPlayer permissionPlayer : players)
            if (permissionPlayer.getUuid().equals(uuid))
                return Optional.of(permissionPlayer);
        return Optional.empty();
    }

    public void setGroup(PermissionPlayer permissionPlayer, PermissionGroup permissionGroup, int duration) {
        Long time = (1000L * 60 * 60 * 24 * duration);
        if (permissionPlayer.getPermissionGroup().equals(permissionGroup))
            permissionPlayer.setExpiration(permissionPlayer.getExpiration() + time);
        else {
            permissionPlayer.setPermissionGroup(permissionGroup);
            permissionPlayer.setExpiration(System.currentTimeMillis() + time);
        }
        update(permissionPlayer);
        sendUpdateToSpigot(permissionPlayer.getUuid(), permissionGroup.getGroupId());
    }

    public void update(PermissionPlayer permissionPlayer) {
        mysql.updateUser(permissionPlayer.getUuid().toString(), permissionPlayer.getName(), permissionPlayer.getPermissionGroup().getGroupId(), permissionPlayer.getExpiration());
    }

    public void sendUpdateToSpigot(UUID uuid, int groupId){
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
        if (proxiedPlayer != null) {
            BungeeUpdater.updateGroup(uuid.toString(), groupId);
        }
    }

    public void updatePermissions() {
        for (PermissionPlayer permissionPlayer : players)
            permissionPlayer.setPermissions(mysql.getPermissions(permissionPlayer.getUuid().toString(), PermissionType.USER));
    }

    public List<PermissionPlayer> getPlayers() {
        return players;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }
}
