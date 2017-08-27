package de.heliosdevelopment.heliosperms.utils;

import de.heliosdevelopment.heliosperms.HeliosPerms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionGroup {

    private final int groupId;
    private final String name;
    private final String prefix, colorCode;
    private final int parentGroup;
    private List<String> permissions = new ArrayList<>();

    public PermissionGroup(int groupId, String name, String prefix, String colorCode, int parentGroup, List<String> permissions) {
        this.groupId = groupId;
        this.name = name;
        if (prefix != null && !Objects.equals(prefix, "-31") && !Objects.equals(prefix, "-"))
            this.prefix = prefix;
        else
            this.prefix = "";
        this.colorCode = colorCode;
        this.parentGroup = parentGroup;
        this.permissions = permissions;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public int getParentGroup() {
        return parentGroup;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getAllPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.addAll(this.permissions);
        if (parentGroup != -31) {
            PermissionGroup permissionGroup = HeliosPerms.getGroupManager().getGroup(parentGroup);
            if (permissionGroup != null)
                permissions.addAll(permissionGroup.getAllPermissions());
        }
        return permissions;
    }

    public List<String> getParentPermissions() {
        if (parentGroup != -31) {
            PermissionGroup permissionGroup = HeliosPerms.getGroupManager().getGroup(parentGroup);
            if (permissionGroup != null)
                return permissionGroup.getAllPermissions();
        }
        return new ArrayList<>();
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}