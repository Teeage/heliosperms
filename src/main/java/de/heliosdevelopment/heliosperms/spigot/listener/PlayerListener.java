package de.heliosdevelopment.heliosperms.spigot.listener;


import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.events.GroupChangeEvent;
import de.heliosdevelopment.heliosperms.spigot.Main;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;

public class PlayerListener implements Listener {

    private PlayerManager playerManager;
    private boolean coloredTabList = true;
    private boolean coleredChat = true;

    public PlayerListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PermissionPlayer permissionPlayer = playerManager.loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName(), false);
        new BukkitRunnable() {

            @Override
            public void run() {
                if (permissionPlayer == null) return;

                try {
                    Field field = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity").getDeclaredField("perm");
                    field.setAccessible(true);
                    field.set(event.getPlayer(), new Permissible(event.getPlayer(), permissionPlayer));
                } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                setPrefix(event.getPlayer());
            }
        }.runTaskLater(Main.getInstance(), 30);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerManager.unloadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        playerManager.unloadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!coleredChat) return;
        Player player = e.getPlayer();
        PermissionGroup group = playerManager.getPlayer(player.getUniqueId()).getPermissionGroup();
        String prefix = group.getColorCode() + group.getName();
        e.setFormat(prefix + " §8┃ §7" + player.getName() + " §e»§f " + "%2$s");
        if (player.hasPermission("heliosperms.chatcolor"))
            e.setMessage(e.getMessage().replace("&", "§"));

    }

    @EventHandler
    public void onGroupChange(GroupChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getUniqueId());
        if (player != null) {
            PermissionPlayer permissionPlayer = playerManager.getPlayer(event.getUniqueId());
            if (permissionPlayer != null) {
                permissionPlayer.setPermissionGroup(playerManager.getGroupManager().getGroup(event.getGroupId()));
                setPrefix(player);
            }
        }
    }

    private void setPrefix(Player p) {
        if (!coloredTabList) return;
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PermissionGroup group = playerManager.getPlayer(player.getUniqueId()).getPermissionGroup();
            Team color = board.getTeam(Integer.valueOf(group.getGroupId()).toString());
            if (color == null) {
                color = board.registerNewTeam(Integer.valueOf(group.getGroupId()).toString());
                color.setPrefix(group.getColorCode());
            }
            color.addEntry(player.getName());
        }

        PermissionGroup group = playerManager.getPlayer(p.getUniqueId()).getPermissionGroup();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            Scoreboard board1 = pl.getScoreboard();
            Team color = board1.getTeam(Integer.valueOf(group.getGroupId()).toString());
            if (color == null) {
                color = board1.registerNewTeam(Integer.valueOf(group.getGroupId()).toString());
                color.setPrefix(group.getColorCode());
            }
            //┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃
            color.addEntry(p.getName());
            pl.setScoreboard(board1);
        }

        p.setScoreboard(board);
    }

    private class Permissible extends PermissibleBase {

        private final PermissionPlayer permissionPlayer;

        Permissible(Player player, PermissionPlayer permissionPlayer) {
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


}
