package de.heliosdevelopment.heliosperms.api.manager;

import de.heliosdevelopment.heliosperms.api.HeliosPerms;
import de.heliosdevelopment.heliosperms.api.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionType;

import java.util.List;

public class GroupManager {

    private List<PermissionGroup> groups;
    private final DatabaseHandler databaseHandler;
    private final PermissionGroup defaultGroup;

    public GroupManager(DatabaseHandler databaseHandler, boolean bungeecord) {
        this.databaseHandler = databaseHandler;
        this.groups = databaseHandler.getGroups();
        if (groups.isEmpty() && bungeecord) {
            databaseHandler.addGroup(99, "User", "§7", "User", null, true);
            databaseHandler.addGroup(1, "Administrator", "§c", "Administrator",
                    99, false);
            databaseHandler.addPermission(String.valueOf(1), PermissionType.GROUP, "bukkit.*");
            databaseHandler.addPermission(String.valueOf(1), PermissionType.GROUP, "minecraft.*");
            databaseHandler.addPermission(String.valueOf(1), PermissionType.GROUP, "heliosperms.*");
            this.groups = databaseHandler.getGroups();
        }
        defaultGroup = getInitialDefaultGroup();
        if (defaultGroup == null)
            System.out.println("[HeliosPerms] Das System konnte die default Gruppe nicht finden.");
    }

    public PermissionGroup getGroup(Integer id) {
        for (PermissionGroup group : groups) {
            if (Integer.valueOf(group.getGroupId()).equals(id))
                return group;
        }
        return null;
    }

    private PermissionGroup getInitialDefaultGroup() {
        for (PermissionGroup permissionGroup : groups) {
            if (permissionGroup.isDefaultGroup())
                return permissionGroup;
        }
        return null;
    }

    /**
     * @return the default group for new user
     */
    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    /**
     * @param name of the group
     * @return PermissionGroup object
     */
    public PermissionGroup getGroup(String name) {
        for (PermissionGroup group : groups) {
            if (group.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                return group;
        }
        return null;
    }

    /**
     * Delete a permission group
     *
     * @param permissionGroup
     * @return if the group was successfully deleted
     */
    public boolean removeGroup(PermissionGroup permissionGroup) {
        if (permissionGroup.equals(defaultGroup))
            return false;
        groups.remove(permissionGroup);
        databaseHandler.removeGroup(permissionGroup.getGroupId());
        return true;

    }

    /**
     * Updates the cached by the groups from the database
     */
    public void updateGroups() {
        groups = databaseHandler.getGroups();
    }

    /**
     * Updates the permissions from all groups
     */
    public void updatePermissions() {
        for (PermissionGroup group : groups)
            group.setPermissions(databaseHandler.getPermissions(Integer.valueOf(group.getGroupId()).toString(), PermissionType.GROUP));
    }

    /**
     * @return a list from all groups
     */
    public List<PermissionGroup> getGroups() {
        return groups;
    }
}
