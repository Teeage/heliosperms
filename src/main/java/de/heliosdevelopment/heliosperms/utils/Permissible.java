package de.heliosdevelopment.heliosperms.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

public class Permissible extends PermissibleBase {

    private final PermissionPlayer permissionPlayer;

    public Permissible(Player player, PermissionPlayer permissionPlayer) {
        super(player);
        this.permissionPlayer = permissionPlayer;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return hasPermission(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return hasPermission(permission.getName());
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return hasPermission(permission.getName());
    }

    @Override
    public boolean hasPermission(String name) {
        return permissionPlayer.hasPermission(name);
    }

}
