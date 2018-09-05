package de.heliosdevelopment.heliosperms;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.heliosdevelopment.heliosperms.manager.GroupManager;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.utils.*;

public class HeliosPerms {
    private static HeliosPerms instance;
    private final MySQL connection;
    private final boolean isBungee;
    private final PlayerManager playerManager;

    public HeliosPerms(MySQL connection, PlayerManager playerManager, boolean isBungee) {
        instance = this;
        this.connection = connection;
        this.playerManager = playerManager;
        this.isBungee = isBungee;
    }


    public static HeliosPerms getInstance() {
        return instance;
    }

    public Boolean getIsBungee() {
        return isBungee;
    }

    /**
     * Gets the permissions of a player. Its is not required that the player is
     * online.
     *
     * @param uuid the uuid of the player
     * @return List with the permissions
     */
    public List<String> getPlayerPermissions(UUID uuid) {
        return connection.getPermissions(uuid.toString(), PermissionType.USER);
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
        return playerManager.getGroupManager().getGroup(connection.getGroup(uuid.toString()));


    }

    /**
     * Gets the name and color of a group by the group name
     *
     * @param name of the group
     * @return String with color and name of the group
     */
    public String getGroupName(String name) {
        PermissionGroup group = playerManager.getGroupManager().getGroup(name);
        if (group != null)
            return group.getColorCode() + group.getName();
        return "";
    }

    public GroupManager getGroupManager() {
        return playerManager.getGroupManager();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
