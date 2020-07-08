package de.heliosdevelopment.heliosperms.manager;

import de.heliosdevelopment.heliosperms.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.utils.PermissionType;
import de.heliosdevelopment.heliosperms.utils.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {

    private final List<PermissionPlayer> players = new ArrayList<>();
    private final DatabaseHandler databaseHandler;
    private final GroupManager groupManager;
    private final PermissionGroup defaultGroup;

    public PlayerManager(DatabaseHandler databaseHandler, GroupManager groupManager) {
        this.databaseHandler = databaseHandler;
        this.groupManager = groupManager;
        this.defaultGroup = groupManager.getGroup(20);
    }

    /**
     * Load a player from the database
     *
     * @param uuid of a player
     * @param name of a player
     * @param bungee if this is a bungeecord server
     * @return the permission player object
     */
    public PermissionPlayer loadPlayer(UUID uuid, String name, boolean bungee) {
        long expiration = databaseHandler.getExpiration(uuid.toString()) == null ? -1 : databaseHandler.getExpiration(uuid.toString());
        PermissionGroup group = groupManager.getGroup(databaseHandler.getGroup(uuid.toString()));
        PermissionPlayer permissionPlayer = new PermissionPlayer(uuid, name,
                group != null ? group : groupManager.getDefaultGroup(),
                databaseHandler.getPermissions(uuid.toString(), PermissionType.USER),
                expiration, defaultGroup);
        players.add(permissionPlayer);
        if (databaseHandler.getGroup(uuid.toString()) == -1 && bungee)
            databaseHandler.addUser(uuid.toString(), name, 20, (long) -1);
        return permissionPlayer;
    }

    /**
     * Unload a player
     *
     * @param uuid of the player
     */
    public void unloadPlayer(UUID uuid) {
        Optional<PermissionPlayer> permissionPlayer = getPlayer(uuid);
        permissionPlayer.ifPresent(players::remove);
    }

    /**
     *
     * @param uuid
     * @return
     */
    public Optional<PermissionPlayer> getPlayer(UUID uuid) {
        for (PermissionPlayer permissionPlayer : players)
            if (permissionPlayer.getUuid().equals(uuid))
                return Optional.of(permissionPlayer);
        return Optional.empty();
    }

    /**
     * Set the group of a player
     *
     * @param permissionPlayer
     * @param permissionGroup
     * @param duration
     * @param timeUnit
     */
    public void setGroup(PermissionPlayer permissionPlayer, PermissionGroup permissionGroup, int duration, TimeUnit timeUnit) {
        Long time = (long) (timeUnit.getMultiplier() * duration);
        int oldGroupId = permissionPlayer.getPermissionGroup().getGroupId();
        if (permissionPlayer.getPermissionGroup().equals(permissionGroup))
            permissionPlayer.setExpiration(permissionPlayer.getExpiration() + time);
        else {
            permissionPlayer.setPermissionGroup(permissionGroup);
            permissionPlayer.setExpiration(System.currentTimeMillis() + time);
        }
        update(permissionPlayer);
        sendUpdateToSpigot(permissionPlayer.getUuid(), oldGroupId, permissionGroup.getGroupId());
    }

    /**
     * Send a user update to the database
     *
     * @param permissionPlayer
     */
    public void update(PermissionPlayer permissionPlayer) {
        databaseHandler.updateUser(permissionPlayer.getUuid().toString(), permissionPlayer.getName(), permissionPlayer.getPermissionGroup().getGroupId(), permissionPlayer.getExpiration());
    }

    /**
     * Send an group update to the spigot servers
     *
     * @param uuid
     * @param oldGroupId
     * @param newGroupId
     */
    public void sendUpdateToSpigot(UUID uuid, int oldGroupId, int newGroupId) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
        if (proxiedPlayer != null) {
            BungeeUpdater.updateGroup(uuid.toString(), oldGroupId, newGroupId);
        }
    }

    /**
     * Update all per player permissions
     */
    public void updatePermissions() {
        for (PermissionPlayer permissionPlayer : players)
            permissionPlayer.setPermissions(databaseHandler.getPermissions(permissionPlayer.getUuid().toString(), PermissionType.USER));
    }

    public List<PermissionPlayer> getPlayers() {
        return players;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }
}