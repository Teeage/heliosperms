package de.heliosdevelopment.heliosperms.bungee.commands;

import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.bungee.Main;
import de.heliosdevelopment.heliosperms.manager.BungeeUpdater;
import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.utils.PermissionType;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.utils.PunishUnit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.UUID;

public class PermissionCommand extends Command {

    private final MySQL mysql;
    private PlayerManager playerManager;

    public PermissionCommand(MySQL mysql, PlayerManager playerManager) {
        super("hperms", "", "heliosperms", "perms", "rank");
        this.mysql = mysql;
        this.playerManager = playerManager;
    }

    /*
     * perms user (name) setgroup (group) (duration) <-- Zeit in Tagen
     * perms user (name) getGroup
     * perms user (name) add  (permission)
     * perms user (name) remove (permission)
     * perms user (name) list
     *
     * perms groups
     * perms addgroup (id) (name) (chatColor) (prefix) (parentGroup)
     * perms group (name) add (permission)
     * perms group (name) remove (permission)
     * perms group (name) list
     * perms group (name) users
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("heliosperms.admin"))
            return;
        if (args.length == 0) {
            sendMessage(sender, "§eHeliosPerms §7v" + Main.getInstance().getDescription().getVersion() + " by §eTeeage.", true);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendMessage(sender, "§e/perms user (name) setgroup (group) (duration)", false);
                sendMessage(sender, "§e/perms user (name) getgroup", false);
                sendMessage(sender, "§e/perms user (name) add (permission)", false);
                sendMessage(sender, "§e/perms user (name) remove (permission)", false);
                sendMessage(sender, "§e/perms user (name) list", false);
                sendMessage(sender, "§e/perms groups", false);
                sendMessage(sender, "§e/perms addgroup (id) (name) (chatColor) (prefix) (parentGroupId)", false);
                sendMessage(sender, "§aExample: §7\"§7/perms addgroup 2 Developer b Dev 20§7\"", false);
                sendMessage(sender, "§e/perms group (name) add (permission)", false);
                sendMessage(sender, "§e/perms group (name) remove (permission)", false);
                sendMessage(sender, "§e/perms group (name) list", false);
                sendMessage(sender, "§e/perms group (name) users", false);
            } else if (args[0].equalsIgnoreCase("groups")) {
                if (playerManager.getGroupManager().getGroups().size() == 0)
                    sendMessage(sender, "§cKonnte keine Gruppe finden.", true);
                for (PermissionGroup group : playerManager.getGroupManager().getGroups())
                    sendMessage(sender, "§e" + group.getName(), false);
            } else if (args[0].equalsIgnoreCase("test")) {
                for (String s : sender.getPermissions())
                    sendMessage(sender, s, true);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("user")) {
                if (args[2].equalsIgnoreCase("list")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) return;
                    for (String s : mysql.getPermissions(target.getUniqueId().toString(), PermissionType.USER))
                        sendMessage(sender, s, true);
                } else if (args[2].equalsIgnoreCase("getgroup")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) {
                        sendMessage(sender, "§cDer Spieler ist nicht online!", true);
                        return;
                    }
                    PermissionPlayer player = playerManager.getPlayer(target.getUniqueId());
                    if (player != null) {
                        if (player.getPermissionGroup() != null) {
                            sendMessage(sender,
                                    "§7Der Spieler ist in der Gruppe " + player.getPermissionGroup().getColorCode()
                                            + player.getPermissionGroup().getName(), true);

                            sendMessage(sender, "§7Er besitzt sie noch " + PunishUnit.getRemainingTime(player.getExpiration()), true);
                        }
                    }

                }
            } else if (args[0].equalsIgnoreCase("group")) {
                if (args[2].equalsIgnoreCase("list")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cGruppe existiert nicht.", true);
                        return;
                    }
                    sendMessage(sender, "§7§m-------------------§7[§eHeliosPerms§7]§m------------------", false);
                    sendMessage(sender, "§aGruppe: " + group.getColorCode() + group.getName(), false);
                    if (group.getPermissions().size() != 0) {
                        sendMessage(sender, "§eGruppenpermissions:", false);
                        for (String gperm : group.getPermissions())
                            sendMessage(sender, "§7" + gperm, false);
                    }

                    if (group.getParentPermissions().size() != 0) {
                        sendMessage(sender, "§eVererbte Permissions:", false);
                        for (String pperm : group.getParentPermissions())
                            sendMessage(sender, "§7" + pperm, false);
                    }

                    sendMessage(sender, "§7§m-----------------------------------------------------", false);
                } else if (args[2].equalsIgnoreCase("users")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cGruppe existiert nicht.", true);
                        return;
                    }
                    sendMessage(sender, "§7§m-------------------§7[§eHeliosPerms§7]§m------------------", false);
                    sendMessage(sender, "§aGruppe: " + group.getColorCode() + group.getName(), false);
                    sendMessage(sender, "§eUsers: ", false);
                    if (group.getName().toLowerCase().equals("user"))
                        sendMessage(sender, String.valueOf(mysql.getUsers(group.getGroupId()).size()), false);
                    else
                        for (String s : mysql.getUsers(group.getGroupId()))
                            sendMessage(sender, "§7" + s, false);
                    sendMessage(sender, "§7§m-----------------------------------------------------", false);
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("user")) {
                if (args[2].equalsIgnoreCase("add")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) {
                        sendMessage(sender, "§cDer Spieler existiert nicht.", true);
                        return;
                    }
                    if (!mysql.hasPermission(target.getUniqueId().toString(), PermissionType.USER, args[3])) {
                        mysql.addPermission(target.getUniqueId().toString(), PermissionType.USER, args[3]);
                        BungeeUpdater.updatePermissions(PermissionType.USER);
                        sendMessage(sender, "§7Die Permission §a" + args[3] + " §7wurde erfolgreich hinzugefügt.", true);
                    }
                } else if (args[2].equalsIgnoreCase("remove")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) {
                        sendMessage(sender, "§cDer Spieler existiert nicht.", true);
                        return;
                    }
                    if (mysql.hasPermission(target.getUniqueId().toString(), PermissionType.USER, args[3])) {
                        mysql.removePermission(target.getUniqueId().toString(), PermissionType.USER, args[3]);
                        BungeeUpdater.updatePermissions(PermissionType.USER);
                        sendMessage(sender, "§7Die Permission §a" + args[3] + " §7wurde erfolgreich entfernt.", true);
                    }
                }
            } else if (args[0].equalsIgnoreCase("group")) {
                if (args[2].equalsIgnoreCase("add")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cDie Gruppe existiert nicht.", true);
                        return;
                    }
                    if (!mysql.hasPermission(Integer.valueOf(group.getGroupId()).toString(), PermissionType.GROUP, args[3])) {
                        mysql.addPermission(Integer.valueOf(group.getGroupId()).toString(), PermissionType.GROUP, args[3]);
                        sendMessage(sender, "§aPermission wurde gesetzt.", true);
                        playerManager.getGroupManager().updatePermissions();
                        BungeeUpdater.updatePermissions(PermissionType.GROUP);
                    }

                } else if (args[2].equalsIgnoreCase("remove")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cGruppe existiert nicht.", true);
                        return;
                    }
                    if (mysql.hasPermission(Integer.valueOf(group.getGroupId()).toString(), PermissionType.GROUP, args[3])) {
                        mysql.removePermission(Integer.valueOf(group.getGroupId()).toString(), PermissionType.GROUP, args[3]);
                        sendMessage(sender, "§aPermission wurde entfernt.", true);
                        playerManager.getGroupManager().updatePermissions();
                        BungeeUpdater.updatePermissions(PermissionType.GROUP);
                    }
                }
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("user")) {
                if (args[2].equalsIgnoreCase("setgroup")) {
                    int duration;
                    try {
                        duration = Integer.valueOf(args[4]);
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                        return;
                    }
                    String uuid = mysql.getUuid(args[1]);
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[3]);
                    if (group == null) {
                        sendMessage(sender, "§cGruppe existiert nicht.", true);
                        return;
                    }
                    Long time = -1L;
                    if (mysql.getGroup(uuid) == group.getGroupId()) {
                        Long expiration = Long.valueOf(mysql.getExpiration(uuid));
                        if (expiration != null)
                            time = expiration + (1000L * 60 * 60 * 24 * duration);
                    } else if (duration != -1) {
                        time = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * duration);
                    }
                    mysql.updateUser(uuid, args[1], group.getGroupId(), time);
                    PermissionPlayer permissionPlayer = playerManager.getPlayer(UUID.fromString(uuid));
                    if (permissionPlayer != null) {
                        permissionPlayer.setPermissionGroup(group);
                        permissionPlayer.setExpiration(time);
                    }
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                    if (proxiedPlayer != null) {
                        // BungeeUpdater.updatePermissions(proxiedPlayer);
                        BungeeUpdater.updateGroup(uuid, group.getGroupId());
                    }
                    //ProxyServer.getInstance().getPluginManager().callEvent(new GroupChangeEvent(UUID.fromString(uuid), group.getGroupId()));
                    sendMessage(sender, "§aDie Gruppe von §e"
                            + args[1] + " §awurde auf " + group.getColorCode() + group.getName() + " §agesetzt.", true);
                }
            }
        } else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("addgroup")) {
                int groupId = -1;
                try {
                    groupId = Integer.valueOf(args[1]);
                } catch (NumberFormatException exception) {
                    sendMessage(sender, "§7Ist das eine Zahl? Näh oder?", true);
                }
                if (groupId == -1)
                    return;
                if (playerManager.getGroupManager().getGroup(groupId) == null) {
                    mysql.addGroup(Integer.valueOf(args[1]), args[2], "§" + args[3], args[4], Integer.valueOf(args[5]));
                    playerManager.getGroupManager().updateGroups();
                    sendMessage(sender, "§7Du hast die Gruppe §e" + args[2] + " §7erstellt.", true);
                } else {
                    sendMessage(sender, "§7Diese Gruppe existiert nicht!", true);
                }
            }
        } else {
            sendMessage(sender, "§c/heliosperms help", true);
        }
    }


    private void sendMessage(CommandSender sender, String message, boolean prefix) {
        if (prefix)
            sender.sendMessage(new TextComponent("§7[§ePerms§7] " + message));
        else
            sender.sendMessage(new TextComponent(message));
    }

}
