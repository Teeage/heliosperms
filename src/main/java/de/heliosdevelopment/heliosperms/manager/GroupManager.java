package de.heliosdevelopment.heliosperms.manager;

import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupManager {

    private List<PermissionGroup> groups = new ArrayList<>();
    private final MySQL mysql;

    public GroupManager(MySQL mysql) {
        this.mysql = mysql;
        this.groups = mysql.getGroups();
        if (getGroup(20) == null) {
            groups.add(new PermissionGroup(20, "User", "User", "§7", -31, Arrays.asList("")));
            mysql.addGroup(20, "User", "§7", "User", -31);
            groups.add(new PermissionGroup(1, "Administrator", "Administrator", "§c",
                    20, Arrays.asList("bukkit.*", "minecraft.*", "heliosperms.admin")));
            mysql.addGroup(1, "Administrator", "§c", "Administrator",
                    20);
            mysql.addPermission(String.valueOf(1), PermissionType.GROUP, "bukkit.*");
            mysql.addPermission(String.valueOf(1), PermissionType.GROUP, "minecraft.*");
            mysql.addPermission(String.valueOf(1), PermissionType.GROUP, "heliosperms.*");
        }
    }

    public PermissionGroup getGroup(Integer id) {
        for (PermissionGroup group : groups) {
            if (Integer.valueOf(group.getGroupId()).equals(id))
                return group;
        }
        return null;
    }

    public PermissionGroup getGroup(String name) {
        for (PermissionGroup group : groups) {
            if (group.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                return group;
        }
        return null;
    }

    public void updateGroups() {
        groups = mysql.getGroups();
    }

    public void updatePermissions() {
        for (PermissionGroup group : groups)
            group.setPermissions(mysql.getPermissions(Integer.valueOf(group.getGroupId()).toString(), PermissionType.GROUP));
    }

    public List<PermissionGroup> getGroups() {
        return groups;
    }
}
