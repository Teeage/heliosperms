package de.heliosdevelopment.heliosperms.api;

import de.heliosdevelopment.heliosperms.api.manager.GroupManager;
import de.heliosdevelopment.heliosperms.api.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.api.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.api.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HeliosPerms {
    private static HeliosPerms instance;
    private final DatabaseHandler databaseHandler;
    private final boolean bungeecord;
    private final PlayerManager playerManager;

    public HeliosPerms(DatabaseHandler databaseHandler, PlayerManager playerManager, boolean bungeecord) {
        instance = this;
        this.databaseHandler = databaseHandler;
        this.playerManager = playerManager;
        this.bungeecord = bungeecord;
    }


    public static HeliosPerms getInstance() {
        return instance;
    }

    /**
     *
     * @return if this instance runs on a bungeecord server
     */
    public boolean isBungee() {
        return bungeecord;
    }

    /**
     * Get the player permissions of a player.
     *
     * @param uuid the uuid of the player
     * @return a list with the per player permissions
     */
    public List<String> getPlayerPermissions(UUID uuid) {
        return databaseHandler.getPermissions(uuid.toString(), PermissionType.USER);
    }

    /**
     * Get the all permissions of a player.
     *
     * @param uuid the uuid of the player
     * @return a list with all permissions of a player
     */
    public List<String> getAllPermissions(UUID uuid) {
        List<String> permissions = new ArrayList<>();
        PermissionGroup permissionGroup = getGroup(databaseHandler.getGroup(uuid));
        if(permissionGroup != null) {
            permissions.addAll(permissionGroup.getAllPermissions());
        }
        permissions.addAll(databaseHandler.getPermissions(uuid.toString(), PermissionType.USER));

        return permissions;
    }

    /**
     * Get the group of a player.
     *
     * @param uuid the uuid of the player
     * @return group of the player
     */
    public PermissionGroup getGroup(UUID uuid) {
        Optional<PermissionPlayer> player = playerManager.getPlayer(uuid);
        if (player.isPresent())
            return player.get().getPermissionGroup();
        return playerManager.getGroupManager().getGroup(databaseHandler.getGroup(uuid));
    }

    /**
     * Get the group of a player.
     *
     * @param groupId the id from a group
     * @return the group with the given id
     */
    public PermissionGroup getGroup(int groupId) {
        return getGroupManager().getGroup(groupId);
    }

    public GroupManager getGroupManager() {
        return playerManager.getGroupManager();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
