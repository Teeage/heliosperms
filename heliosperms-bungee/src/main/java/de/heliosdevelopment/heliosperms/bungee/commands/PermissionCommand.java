package de.heliosdevelopment.heliosperms.bungee.commands;

import de.heliosdevelopment.heliosperms.bungee.Main;
import de.heliosdevelopment.heliosperms.api.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.bungee.BungeeUpdater;
import de.heliosdevelopment.heliosperms.api.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.api.utils.PermissionType;
import de.heliosdevelopment.heliosperms.api.utils.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PermissionCommand extends Command {

    private final DatabaseHandler databaseHandler;
    private final PlayerManager playerManager;

    public PermissionCommand(DatabaseHandler databaseHandler, PlayerManager playerManager) {
        super("hperms", "heliosperms.admin", "heliosperms", "perms", "rank");
        this.databaseHandler = databaseHandler;
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
     * perms deletegroup (id)
     * perms group (name) add (permission)
     * perms group (name) remove (permission)
     * perms group (name) list
     * perms group (name) users
     * perms deletegroup (id)
     * perms group (name) edit (key) (value)
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "§eHeliosPerms §7v" + Main.getInstance().getDescription().getVersion() + " by §eTeeage.", true);
            sendMessage(sender, "§eMit /heliosperms help kannst du die Hilfe aufrufen.", true);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendClickableMessage(sender, "§7- §e/perms user (name) setgroup (group) (duration) (timeUnit)");
                sendClickableMessage(sender, "§7- §e/perms user (name) getgroup");
                sendClickableMessage(sender, "§7- §e/perms user (name) add (permission)");
                sendClickableMessage(sender, "§7- §e/perms user (name) remove (permission)");
                sendClickableMessage(sender, "§7- §e/perms user (name) list");
                sendClickableMessage(sender, "§7- §e/perms groups");
                sendClickableMessage(sender, "§7- §e/perms addgroup (id) (name) (chatColor) (prefix) §e(parentGroupId) (defaultGroup)");
                sendMessage(sender, "§aExample: §7\"§7/perms addgroup 2 Developer b Dev 20 false§7\"", false);
                sendClickableMessage(sender, "§7- §e/perms deleteGroup (id)");
                sendClickableMessage(sender, "§7- §e/perms group (name) add (permission)");
                sendClickableMessage(sender, "§7- §e/perms group (name) remove (permission)");
                sendClickableMessage(sender, "§7- §e/perms group (name) list");
                sendClickableMessage(sender, "§7- §e/perms group (name) users");
                sendClickableMessage(sender, "§7- §e/perms group (name) edit (key) (value)");
                sendClickableMessage(sender, "§7- §e/perms group (name) info");
            } else if (args[0].equalsIgnoreCase("groups")) {
                if (playerManager.getGroupManager().getGroups().size() == 0)
                    sendMessage(sender, "§cKonnte keine Gruppe finden.", true);
                sendMessage(sender, "§7Es existieren folgende Gruppen:", true);
                for (PermissionGroup group : playerManager.getGroupManager().getGroups())
                    sendMessage(sender, "§7- " + group.getColorCode() + group.getName() + " | §7ID: §e"
                            + group.getGroupId(), false);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("deleteGroup")) {
                int groupId = -1;
                try {
                    groupId = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception) {
                    sendMessage(sender, "§7Bitte gebe eine Zahl an.", true);
                    return;
                }
                PermissionGroup permissionGroup = playerManager.getGroupManager().getGroup(groupId);
                if (permissionGroup != null) {
                    for (PermissionGroup group : playerManager.getGroupManager().getGroups()) {
                        if (group.getParentGroup()!= null && group.getParentGroup() == groupId) {
                            PermissionGroup parent = playerManager.getGroupManager().getGroup(group.getParentGroup());
                            group.setParentGroup(parent != null ? parent.getParentGroup() : -31);
                        }
                    }
                    if (playerManager.getGroupManager().removeGroup(permissionGroup))
                        sendMessage(sender, "§7Du hast die Gruppe §a" + permissionGroup.getName() + " §7entfernt.",
                                true);
                    else {
                        sendMessage(sender, "§7Du kannst die default Gruppe nicht entfernen.", true);
                        sendMessage(sender, "§7Du kannst sie jedoch mit /perms group (name) §7edit §7(key) §7(value) §7bearbeiten.",
                                true);
                    }

                } else
                    sendMessage(sender, "§cDie Gruppe existiert nicht!", true);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("user")) {
                if (args[2].equalsIgnoreCase("list")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) return;
                    sendMessage(sender, "§7Die Permissions von §a" + target.getName() + " §7:", true);
                    for (String s : databaseHandler.getPermissions(target.getUniqueId().toString(),
                            PermissionType.USER))
                        sendMessage(sender, "§7- " + s, false);
                } else if (args[2].equalsIgnoreCase("getgroup")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) {
                        sendMessage(sender, "§cDer Spieler ist nicht online!", true);
                        return;
                    }
                    Optional<PermissionPlayer> permissionPlayer = playerManager.getPlayer(target.getUniqueId());
                    if (permissionPlayer.isPresent()) {
                        if (permissionPlayer.get().getPermissionGroup() != null) {
                            sendMessage(sender,
                                    "§7Der Spieler ist in der Gruppe "
                                            + permissionPlayer.get().getPermissionGroup().getColorCode()
                                            + permissionPlayer.get().getPermissionGroup().getName(), true);

                            sendMessage(sender, "§7Er besitzt sie noch "
                                    + getRemainingTime(permissionPlayer.get().getExpiration()), true);
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
                    sendMessage(sender, "§7§m-------------------§7[§eHeliosPerms§7]§m------------------",
                            false);
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

                    sendMessage(sender, "§7§m-----------------------------------------------------",
                            false);
                } else if (args[2].equalsIgnoreCase("users")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cGruppe existiert nicht.", true);
                        return;
                    }
                    sendMessage(sender, "§7§m-------------------§7[§eHeliosPerms§7]§m------------------",
                            false);
                    sendMessage(sender, "§aGruppe: " + group.getColorCode() + group.getName(), false);
                    sendMessage(sender, "§eUsers: ", false);
                    if (group.getName().toLowerCase().equals("user"))
                        sendMessage(sender, String.valueOf(databaseHandler.getUsersByGroup(group.getGroupId()).size()),
                                false);
                    else
                        for (String s : databaseHandler.getUsersByGroup(group.getGroupId()))
                            sendMessage(sender, "§7" + s, false);
                    sendMessage(sender, "§7§m-----------------------------------------------------", false);
                } else if (args[2].equalsIgnoreCase("info")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cDie Gruppe existiert nicht.", true);
                        return;
                    }
                    sendMessage(sender, "", false);
                    sendMessage(sender, "§eGruppen Informationen:", false);
                    sendMessage(sender, "§7ID: §a" + group.getGroupId(), false);
                    sendMessage(sender, "§7Name: §a" + group.getColorCode() + group.getName(), false);
                    sendMessage(sender, "§7Name: §a" + group.getColorCode() + group.getName(), false);
                    sendMessage(sender, "§7ParentGroupID: §a" + (group.getParentGroup() != null ?
                            group.getParentGroup() : "Es existiert keine uebergeordnete Gruppe"), false);
                    sendMessage(sender, "", false);
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
                    if (!databaseHandler.hasPermission(target.getUniqueId().toString(),
                            PermissionType.USER, args[3].toLowerCase())) {
                        databaseHandler.addPermission(target.getUniqueId().toString(),
                                PermissionType.USER, args[3].toLowerCase());
                        BungeeUpdater.updatePermissions(PermissionType.USER);
                        sendMessage(sender, "§7Die Permission §a" + args[3] + " §7wurde erfolgreich hinzugefügt.",
                                true);
                    } else
                        sendMessage(sender, "§cPermission existiert bereits.", true);
                } else if (args[2].equalsIgnoreCase("remove")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) {
                        sendMessage(sender, "§cDer Spieler existiert nicht.", true);
                        return;
                    }
                    if (databaseHandler.hasPermission(target.getUniqueId().toString(),
                            PermissionType.USER, args[3].toLowerCase())) {
                        databaseHandler.removePermission(target.getUniqueId().toString(),
                                PermissionType.USER, args[3].toLowerCase());
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
                    if (!databaseHandler.hasPermission(Integer.valueOf(group.getGroupId()).toString(),
                            PermissionType.GROUP, args[3].toLowerCase())) {
                        databaseHandler.addPermission(Integer.valueOf(group.getGroupId()).toString(),
                                PermissionType.GROUP, args[3].toLowerCase());
                        sendMessage(sender, "§aPermission wurde gesetzt.", true);
                        playerManager.getGroupManager().updatePermissions();
                        BungeeUpdater.updatePermissions(PermissionType.GROUP);
                    } else
                        sendMessage(sender, "§cPermission existiert bereits.", true);

                } else if (args[2].equalsIgnoreCase("remove")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cDie Gruppe existiert nicht.", true);
                        return;
                    }
                    if (databaseHandler.hasPermission(Integer.valueOf(group.getGroupId()).toString(),
                            PermissionType.GROUP, args[3].toLowerCase())) {
                        databaseHandler.removePermission(Integer.valueOf(group.getGroupId()).toString(),
                                PermissionType.GROUP, args[3].toLowerCase());
                        sendMessage(sender, "§aPermission wurde entfernt.", true);
                        playerManager.getGroupManager().updatePermissions();
                        BungeeUpdater.updatePermissions(PermissionType.GROUP);
                    }
                }
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("group")) {
                if (args[2].equalsIgnoreCase("edit")) {
                    PermissionGroup group = playerManager.getGroupManager().getGroup(args[1]);
                    if (group == null) {
                        sendMessage(sender, "§cDie Gruppe existiert nicht.", true);
                        return;
                    }
                    String key = args[3].toLowerCase();
                    List<String> keys = Arrays.asList("name", "colorCode", "prefix", "parentGroup");
                    if (keys.contains(key)) {
                        if (key.equals("colorcode"))
                            databaseHandler.updateGroup(group.getGroupId(), "colorCode", "§" + args[4].
                                    replace("&", ""));
                        else if (key.equals("parentGroup"))
                            databaseHandler.updateGroup(group.getGroupId(), "parentGroup", args[4]);
                        else
                            databaseHandler.updateGroup(group.getGroupId(), key, args[4].replace("&",
                                    ""));
                        playerManager.getGroupManager().updateGroups();
                        sendMessage(sender, "§7Wert wurde geaendert.", true);
                    } else {
                        sendMessage(sender, "§cDas ist kein gueltiger Key.", true);
                        sendMessage(sender, "§cFolgende Keys sind gueltig:", true);
                        sendMessage(sender, "§cname, colorCode, prefix und parentGroup", true);
                    }
                }
            }
        } else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("user")) {
                if (args[2].equalsIgnoreCase("setgroup")) {
                    int duration;
                    try {
                        duration = Integer.parseInt(args[4]);
                    } catch (NumberFormatException exception) {
                        sendMessage(sender, "§cDie Dauer ist keine gültige Zahl.", true);
                        return;
                    }
                    TimeUnit timeUnit = TimeUnit.getByName(args[5]);
                    if (timeUnit == null) {
                        sendMessage(sender, "§cDas ist keine gültige Zeiteinheit. Gültige Einheiten: ",
                                true);
                        StringBuilder builder = new StringBuilder();
                        for (TimeUnit unit : TimeUnit.values())
                            builder.append(unit.getName() + ", ");
                        sendMessage(sender, "§e" + builder.toString(), true);
                        return;
                    }
                    UUID uuid = databaseHandler.getUuidByName(args[1]);
                    PermissionGroup group;
                    try {
                        Integer groupId = Integer.parseInt(args[3]);
                        group = playerManager.getGroupManager().getGroup(groupId);
                    } catch (NumberFormatException exception) {
                        group = playerManager.getGroupManager().getGroup(args[3]);
                    }
                    if (group == null) {
                        sendMessage(sender, "§cGruppe §7" + args[3] + " §cexistiert nicht.", true);
                        return;
                    }
                    Optional<PermissionPlayer> permissionPlayer = playerManager.getPlayer(uuid);
                    if (!permissionPlayer.isPresent())
                        permissionPlayer = Optional.of(playerManager.loadPlayer(uuid, args[1], true));
                    if (permissionPlayer.isPresent()) {
                        playerManager.setGroup(permissionPlayer.get(), group, duration, timeUnit, sender.getName(), true);
                        sendMessage(sender, "§aDie Gruppe von §e"
                                        + args[1] + " §awurde auf " + group.getColorCode() + group.getName() + " §agesetzt.",
                                true);
                    }
                }
            }
        } else if (args.length == 7) {
            if (args[0].equalsIgnoreCase("addgroup")) {
                Integer groupId;
                try {
                    groupId = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception) {
                    sendMessage(sender, "§e" + args[1] + " ist keine gueltige Zahl!", true);
                    return;
                }
                Integer parentGroup;
                try {
                    parentGroup = Integer.parseInt(args[5]);
                } catch (NumberFormatException exception) {
                    parentGroup = null;
                }
                if (playerManager.getGroupManager().getGroup(groupId) == null) {
                    databaseHandler.addGroup(groupId, args[2], "§" + args[3], args[4],
                            parentGroup, Boolean.parseBoolean(args[6]));
                    playerManager.getGroupManager().updateGroups();
                    sendMessage(sender, "§7Du hast die Gruppe §e" + args[2] + " §7erstellt.", true);
                } else {
                    sendMessage(sender, "§7Diese GruppenId ist bereits vergeben!", true);
                    sendMessage(sender, "§7Eine Uebersicht aller Gruppen findest du hier: /herliosperms groups.", true);
                    sendMessage(sender, "§7Infos ueber die gewaelte GruppenId findest du hier: /herliosperms group " + groupId + " info.", true);
                }
            }
        } else {
            sendMessage(sender, "§cCommand not found.", true);
            sendMessage(sender, "§cUse '/heliosperms help' for more informations.", true);
        }
    }


    private void sendMessage(CommandSender sender, String message, boolean prefix) {
        if (prefix)
            sender.sendMessage(new TextComponent("§7[§eHeliosPerms§7] " + message));
        else
            sender.sendMessage(new TextComponent(message));
    }

    private void sendClickableMessage(CommandSender sender, String message) {
        TextComponent textComponent = new TextComponent(message);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatColor.stripColor(message).substring(2)));
        sender.sendMessage(textComponent);
    }

    private String getRemainingTime(Long end) {
        long current = System.currentTimeMillis();
        if (end == -1L) {
            return "§ePERMANENT";
        }
        long millis = end - current;

        long seconds = 0L;
        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        long weeks = 0L;
        long years = 0L;
        while (millis >= 1000L) {
            millis -= 1000L;
            seconds += 1L;
        }
        while (seconds >= 60L) {
            seconds -= 60L;
            minutes += 1L;
        }
        while (minutes >= 60L) {
            minutes -= 60L;
            hours += 1L;
        }
        while (hours >= 24L) {
            hours -= 24L;
            days += 1L;
        }
        while (days >= 7L) {
            days -= 7L;
            weeks += 1L;
        }
        while (weeks >= 52L) {
            weeks -= 52L;
            years += 1L;
        }
        String result = "";
        if (years != 0L)
            if (years == 1L)
                result = result + "§e" + years + " §eJahr ";
            else
                result = result + "§e" + years + " §eJahre ";
        if (weeks != 0L)
            if (weeks == 1L)
                result = result + "§e" + weeks + " §eWoche ";
            else
                result = result + "§e" + weeks + " §eWochen ";
        if (days != 0L)
            if (days == 1)
                result = result + "§e" + days + " §eTag ";
            else
                result = result + "§e" + days + " §eTage ";
        if (hours != 0L)
            if (hours == 1)
                result = result + "§e" + hours + " §eStunde ";
            else
                result = result + "§e" + hours + " §eStunden ";
        if (minutes != 0L)
            if (minutes == 1)
                result = result + "§e" + minutes + " §eMinute ";
            else
                result = result + "§e" + minutes + " §eMinuten ";
        if (seconds != 0L)
            if (seconds == 1)
                result = result + "§e" + seconds + " §eSekunde ";
            else
                result = result + "§e" + seconds + " §eSekunden ";
        return result;
    }

}
