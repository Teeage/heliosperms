package de.heliosdevelopment.heliosperms.utils;

import de.heliosdevelopment.heliosperms.HeliosPerms;

import java.util.List;
import java.util.UUID;

public class PermissionPlayer {

    private final UUID uuid;
    private final String name;
    private PermissionGroup permissionGroup;
    private List<String> permissions;
    private long expiration;

    public PermissionPlayer(UUID uuid, String name, PermissionGroup permissionGroup, List<String> permissions, long expiration) {
        this.uuid = uuid;
        this.name = name;
        if (permissionGroup == null)
            this.permissionGroup = HeliosPerms.getInstance().getGroupManager().getGroup(20);
        else
            this.permissionGroup = permissionGroup;
        this.permissions = permissions;
        this.expiration = expiration;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public long getExpiration() {
        return expiration;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public String getName() {
        return name;
    }

    public boolean hasPermission(String permission) {
        List<String> permissions = permissionGroup.getAllPermissions();
        permissions.addAll(this.permissions);
        if (permissions.contains("*")) return true;
        if (permissions.contains(permission.toLowerCase())) return true;
        String adminPermission = null;
        String[] block = permission.split("\\.");
        if (block.length > 1) {
            adminPermission = block[0] + ".*";
        }
        return (adminPermission != null) && (permissions.contains(adminPermission));
    }
}
