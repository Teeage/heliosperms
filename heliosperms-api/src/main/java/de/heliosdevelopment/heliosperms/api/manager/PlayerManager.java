package de.heliosdevelopment.heliosperms.api.manager;

import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.api.utils.TimeUnit;
import de.heliosdevelopment.heliosperms.api.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.api.utils.PermissionType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PlayerManager {

    private final List<PermissionPlayer> players = new ArrayList<>();
    private final DatabaseHandler databaseHandler;
    private final GroupManager groupManager;
    private final PermissionGroup defaultGroup;

    public PlayerManager(DatabaseHandler databaseHandler, GroupManager groupManager) {
        this.databaseHandler = databaseHandler;
        this.groupManager = groupManager;
        this.defaultGroup = groupManager.getDefaultGroup();
    }

    /**
     * Load a permission player instance from the database.
     *
     * @param uuid   of a player
     * @param name   of a player
     * @param bungee if this is a bungeecord server
     * @return the permission player object
     */
    public PermissionPlayer loadPlayer(UUID uuid, String name, boolean bungee) {
        Long databaseExpiration = databaseHandler.getExpiration(uuid);
        long expiration = databaseExpiration == null ? -1 : databaseExpiration;
        PermissionGroup group = groupManager.getGroup(databaseHandler.getGroup(uuid));
        if (group == null && bungee)
            databaseHandler.addUser(uuid, name, defaultGroup.getGroupId(), (long) -1);

        System.out.println("loadPlayer#databaseExpiration " + databaseExpiration);
        System.out.println("loadPlayer#expiration " + expiration);
        System.out.println("loadPlayer#group " + (group != null ? group.toString() : "null"));
        System.out.println("loadPlayer#defaultGroup " + defaultGroup.toString());

        PermissionPlayer permissionPlayer = new PermissionPlayer(uuid, name,
                group != null ? group : groupManager.getDefaultGroup(),
                databaseHandler.getPermissions(uuid.toString(), PermissionType.USER),
                expiration);
        System.out.println("loadPlayer#permissionPlayer " + permissionPlayer.toString());

        if (expiration != -1 && expiration - System.currentTimeMillis() < 0) {
            System.out.println("loadPlayer#setGroupExecution " + true);
            setGroup(permissionPlayer, defaultGroup, -1, TimeUnit.DAYS, "System", bungee);
        }
        players.add(permissionPlayer);
        return permissionPlayer;

    }

    /**
     * Unload a player from the system. Method will be executed automatically on server quit.
     *
     * @param uuid of the player
     */
    public void unloadPlayer(UUID uuid) {
        Optional<PermissionPlayer> permissionPlayer = getPlayer(uuid);
        permissionPlayer.ifPresent(players::remove);
    }

    /**
     * @param uuid
     * @return permission player object
     */
    public Optional<PermissionPlayer> getPlayer(UUID uuid) {
        for (PermissionPlayer permissionPlayer : players)
            if (permissionPlayer.getUuid().equals(uuid))
                return Optional.of(permissionPlayer);
        return Optional.empty();
    }

    /**
     * Set the group of a player. Recommended to execute this method only from bungeecord servers.
     *
     * @param permissionPlayer The affected player.
     * @param permissionGroup  The new group object.
     * @param duration         Duration a player has this specific group.
     * @param timeUnit         TimeUnit of the duration.
     * @param editor           The name of the editor. System for automatic changes.
     * @param bungee           If the update should send to the spigot servers via plugin message.
     */
    public void setGroup(PermissionPlayer permissionPlayer, PermissionGroup permissionGroup, int duration, TimeUnit
            timeUnit, String editor, boolean bungee) {
        System.out.println("setGroup#permissionGroup " + permissionGroup.getGroupId());
        System.out.println("setGroup#duration " + duration);
        System.out.println("setGroup#TimeUnit " + timeUnit.getName());
        System.out.println("setGroup#editor " + editor);
        System.out.println("setGroup#bungee " + bungee);

        databaseHandler.addHisEntry(permissionPlayer.getUuid(), permissionPlayer.getPermissionGroup().getGroupId(),
                permissionPlayer.getExpiration(), editor, databaseHandler.getLastDateOfChange(permissionPlayer.getUuid()));
        Long time = (long) (timeUnit.getMultiplier() * duration);
        int oldGroupId = permissionPlayer.getPermissionGroup().getGroupId();
        if (permissionPlayer.getPermissionGroup().equals(permissionGroup))
            permissionPlayer.setExpiration(duration == -1 ? -1 : permissionPlayer.getExpiration() + time);
        else {
            permissionPlayer.setPermissionGroup(permissionGroup);
            permissionPlayer.setExpiration(duration == -1 ? -1 : System.currentTimeMillis() + time);
        }
        databaseHandler.updateUser(permissionPlayer.getUuid(), permissionPlayer.getName(),
                permissionPlayer.getPermissionGroup().getGroupId(), permissionPlayer.getExpiration());
        if (bungee) {
            try {
                Class<?> bungeeUpdater = Class.forName("de.heliosdevelopment.heliosperms.bungee");
                Method method = bungeeUpdater.getDeclaredMethod("updateGroup",
                        String.class, Integer.class, Integer.class);
                method.invoke(null, permissionPlayer.getUuid().toString(),
                        oldGroupId, permissionGroup.getGroupId());
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update all per player permissions.
     */
    public void updatePermissions() {
        for (PermissionPlayer permissionPlayer : players)
            permissionPlayer.setPermissions(databaseHandler.getPermissions(permissionPlayer.getUuid().toString(), PermissionType.USER));
    }

    public List<PermissionPlayer> getPlayers() {
        return players;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }
}
