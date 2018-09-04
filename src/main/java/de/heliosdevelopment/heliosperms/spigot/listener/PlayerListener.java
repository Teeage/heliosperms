package de.heliosdevelopment.heliosperms.spigot.listener;


import de.heliosdevelopment.heliosperms.events.GroupChangeEvent;
import de.heliosdevelopment.heliosperms.spigot.Main;
import de.heliosdevelopment.heliosperms.utils.Permissible;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;

public class PlayerListener implements Listener {

    private final PlayerManager playerManager;
    private final boolean coloredTabList;
    private final boolean coloredChat;
    private final String tablistFormat;
    private final String chatFormat;


    public PlayerListener(PlayerManager playerManager, boolean coloredTabList, boolean coloredChat, String tablistFormat, String chatFormat) {
        this.playerManager = playerManager;
        this.coloredTabList = coloredTabList;
        this.coloredChat = coloredChat;
        this.tablistFormat = tablistFormat;
        this.chatFormat = chatFormat;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PermissionPlayer permissionPlayer = playerManager.loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName(), false);
        if (permissionPlayer == null) return;

        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Field field = Main.getInstance().getNMSClass("entity.CraftHumanEntity").getDeclaredField("perm");
            field.setAccessible(true);
            field.set(event.getPlayer(), new Permissible(event.getPlayer(), permissionPlayer));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        setPrefix(event.getPlayer());
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
        if (!coloredChat) return;
        Player player = e.getPlayer();
        PermissionGroup group = playerManager.getPlayer(player.getUniqueId()).getPermissionGroup();
        if (group == null)
            return;
        String format = chatFormat;
        format = ChatColor.translateAlternateColorCodes('&', format);
        if (format.contains("%colorCode%"))
            format = format.replace("%colorCode%", group.getColorCode());
        if (format.contains("%prefix%"))
            format = format.replace("%prefix%", group.getPrefix());
        if (format.contains("%name%"))
            format = format.replace("%name%", group.getName());
        if (format.contains("%player%"))
            format = format.replace("%player%", player.getName());
        e.setFormat(format + " %2$s");
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
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Scoreboard board = p.getScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PermissionGroup group = playerManager.getPlayer(player.getUniqueId()).getPermissionGroup();
            Team color = board.getTeam(Integer.valueOf(group.getGroupId()).toString());
            if (color == null) {
                color = board.registerNewTeam(Integer.valueOf(group.getGroupId()).toString());
                color.setPrefix(getPrefix(group));
            }
            color.addEntry(player.getName());
        }

        PermissionGroup group = playerManager.getPlayer(p.getUniqueId()).getPermissionGroup();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            Scoreboard board1 = pl.getScoreboard();
            Team color = board1.getTeam(Integer.valueOf(group.getGroupId()).toString());
            if (color == null) {
                color = board1.registerNewTeam(Integer.valueOf(group.getGroupId()).toString());
                color.setPrefix(getPrefix(group));
            }
            //┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃
            color.addEntry(p.getName());
            pl.setScoreboard(board1);
        }

        p.setScoreboard(board);
    }

    private String getPrefix(PermissionGroup group) {
        String prefix = tablistFormat;
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        if (prefix.contains("%colorCode%"))
            prefix = prefix.replace("%colorCode%", group.getColorCode());
        if (prefix.contains("%prefix%"))
            prefix = prefix.replace("%prefix%", group.getPrefix());
        if (prefix.contains("%name%"))
            prefix = prefix.replace("%name%", group.getName());
        if (prefix.contains("%player%"))
            prefix = prefix.replace("%player%", "");
        if (prefix.length() > 16) {
            System.out.println("[HeliosPerms] Du darfst bei Bukkit die Zeichen Länge von 16 Zeichen nicht überschreiten!");
            System.out.println(prefix);
            System.out.println("[HeliosPerms] Aktuell ist der Prefix " + prefix.length() + " Zeichen lang.");
            prefix = group.getColorCode();
        }
        return prefix;
    }

}
