package de.heliosdevelopment.heliosperms;

import java.util.List;
import java.util.UUID;

import de.heliosdevelopment.heliosperms.manager.GroupManager;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.utils.*;

public class HeliosPerms {
    private static MySQL connection;
    private static Boolean isBungee = false;
    private static PlayerManager playerManager;

    public HeliosPerms(MySQL con, PlayerManager playerManager) {
        HeliosPerms.connection = con;
        HeliosPerms.playerManager = playerManager;
    }

    public static void setBungee(Boolean isBungee) {
        HeliosPerms.isBungee = isBungee;
    }

    /**
     * Gets the permissions of a player. Its is not required that the player is
     * online.
     *
     * @param uuid the uuid of the player
     * @return List with the permissions
     */
    public static List<String> getPlayerPermissions(UUID uuid) {
        return connection.getPermissions(uuid.toString(), PermissionType.USER);
    }

    /**
     * Get the group of a player.
     *
     * @param uuid the uuid of the player
     * @return group of the player
     */
    public static PermissionGroup getGroup(UUID uuid) {
        PermissionPlayer player = playerManager.getPlayer(uuid);
        if (player != null)
            return player.getPermissionGroup();
        return playerManager.getGroupManager().getGroup(connection.getGroup(uuid.toString()));


    }

    /**
     * Gets the name and color of a group by the group name
     *
     * @param name of the group
     * @return String with color and name of the group
     */
    public static String getGroupName(String name) {
        PermissionGroup group = playerManager.getGroupManager().getGroup(name);
        if (group != null)
            return group.getColorCode() + group.getName();
        return "";
    }

    /**
     * Check if a player is online by name
     *
     * @param uuid of the player
     * @return Boolean
     */
    public static boolean isOnline(UUID uuid) {
        return playerManager.getPlayer(uuid) != null;
    }

    public static GroupManager getGroupManager() {
        return playerManager.getGroupManager();
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }
}
