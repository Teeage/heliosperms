package de.heliosdevelopment.heliosperms.api.utils;

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
        String[] block = permission.split("\\.");
        for (int i = 0; i < block.length; i++) {
            String adminPermission = block[i] + ".*";
            if (adminPermission != null && permissions.contains(adminPermission.toLowerCase()))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "PermissionPlayer{" +
                "uuid=" + uuid.toString() +
                ", name='" + name + '\'' +
                ", permissionGroupId=" + permissionGroup.getGroupId() +
                ", expiration=" + expiration +
                '}';
    }
}
