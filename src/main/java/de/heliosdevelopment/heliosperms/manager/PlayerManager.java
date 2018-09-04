package de.heliosdevelopment.heliosperms.manager;

import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.utils.PermissionType;

import java.util.ArrayList;
import java.util.List;
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
         PermissionGroup group =  groupManager.getGroup(mysql.getGroup(uuid.toString()));
        PermissionPlayer permissionPlayer = new PermissionPlayer(uuid,
               group != null ? group : groupManager.getDefaultGroup(),
                mysql.getPermissions(uuid.toString(), PermissionType.USER),
                Long.valueOf(expiration));
        players.add(permissionPlayer);
        if (mysql.getGroup(uuid.toString()) == -1 && bungee)
            mysql.addUser(uuid.toString(), name, 20, (long) -1);
        System.out.println(permissionPlayer.getPermissionGroup().getName());
        return permissionPlayer;
    }

    public void unloadPlayer(UUID uuid) {
        PermissionPlayer permissionPlayer = getPlayer(uuid);
        if (permissionPlayer != null)
            players.remove(permissionPlayer);
    }

    public PermissionPlayer getPlayer(UUID uuid) {
        for (PermissionPlayer permissionPlayer : players)
            if (permissionPlayer.getUuid().equals(uuid))
                return permissionPlayer;
        return null;
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
