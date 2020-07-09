package de.heliosdevelopment.heliosperms.api.utils;

import de.heliosdevelopment.heliosperms.api.HeliosPerms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionGroup {

    private final int groupId;
    private final String name;
    private final String prefix, colorCode;
    private Integer parentGroup;
    private List<String> permissions;
    private final boolean defaultGroup;

    public PermissionGroup(int groupId, String name, String prefix, String colorCode, int parentGroup, List<String> permissions, boolean defaultGroup) {
        this.groupId = groupId;
        this.name = name;
        if (prefix != null && !Objects.equals(prefix, "-31") && !Objects.equals(prefix, "-"))
            this.prefix = prefix;
        else
            this.prefix = "";
        this.colorCode = colorCode;
        this.parentGroup = parentGroup == 0 ? null : parentGroup;
        this.permissions = permissions;
        this.defaultGroup = defaultGroup;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public Integer getParentGroup() {
        return parentGroup;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>(this.permissions);
        if (parentGroup != null) {
            PermissionGroup permissionGroup = HeliosPerms.getInstance().getGroupManager().getGroup(parentGroup);
            if (permissionGroup != null)
                permissions.addAll(permissionGroup.getAllPermissions());
        }
        return permissions;
    }

    public List<String> getParentPermissions() {
        if (parentGroup != -31) {
            PermissionGroup permissionGroup = HeliosPerms.getInstance().getGroupManager().getGroup(parentGroup);
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

    public void setParentGroup(int parentGroup) {
        this.parentGroup = parentGroup;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    @Override
    public String toString() {
        return "PermissionGroup{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", parentGroup=" + parentGroup +
                ", defaultGroup=" + defaultGroup +
                '}';
    }
}
